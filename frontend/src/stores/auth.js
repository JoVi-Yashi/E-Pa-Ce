import { defineStore } from 'pinia';
import api from '../services/api';
import { useNotificationStore } from './notifications';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
    expiresAt: localStorage.getItem('expiresAt') || null,
    activeRole: localStorage.getItem('activeRole') || null,
    rolePermissions: JSON.parse(localStorage.getItem('rolePermissions')) || {},
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    // Add an initialization hook or use computed to ensure expiresAt is recovered
    getExpiresAt: (state) => {
      if (state.expiresAt) return state.expiresAt;
      if (state.token) {
        try {
          const payload = JSON.parse(atob(state.token.split('.')[1]));
          if (payload.exp) return (payload.exp * 1000).toString();
        } catch (e) {
          console.warn('Failed to recover expiry from token', e);
        }
      }
      return null;
    },
    isExpired: (state) => {
      const exp = state.expiresAt || (state.token ? JSON.parse(atob(state.token.split('.')[1])).exp * 1000 : null);
      if (!exp) return true;
      return new Date().getTime() > parseInt(exp);
    },
    // ... rest of getters
    isAdmin: (state) => {
      const cleanActiveRole = state.activeRole?.replace('ROLE_', '');
      const permissions = state.rolePermissions[cleanActiveRole] || [];
      return cleanActiveRole === 'ADMIN' || permissions.includes('ALL:ALL');
    },

    // Composite check for modular permissions + ownership
    can: (state) => (action, module, ownerId = null) => {
      if (!state.activeRole) return false;
      const cleanActiveRole = state.activeRole.replace('ROLE_', '');
      const permissions = state.rolePermissions[cleanActiveRole] || [];

      if (permissions.includes('ALL:ALL')) return true;

      // Check for global action (ALL)
      const allPerm = `${module}:${action}_ALL`;
      const crudAllPerm = `${module}:CRUD_ALL`;
      if (permissions.includes(allPerm) || permissions.includes(crudAllPerm)) return true;

      // Check for owner-specific action (OWN)
      const ownPerm = `${module}:${action}_OWN`;
      const crudOwnPerm = `${module}:CRUD_OWN`;
      if (permissions.includes(ownPerm) || permissions.includes(crudOwnPerm)) {
        // If we have OWN permission, we must also be the owner
        return ownerId === state.user?.id;
      }

      // Fallback to legacy exact match or generic CRUD
      const legacyPerm = `${module}:${action}`;
      return permissions.includes(legacyPerm);
    },

    // Check if user has a specific permission or the ALL:ALL wildcard in their ACTIVE role
    hasPermission: (state) => (permission) => {
      if (!state.activeRole) return false;
      
      const cleanActiveRole = state.activeRole.replace('ROLE_', '');
      const permissions = state.rolePermissions[cleanActiveRole] || [];
      
      if (permissions.includes('ALL:ALL')) return true;
      if (permissions.includes(permission)) return true;

      // Parse current check
      const parts = permission.split(':');
      if (parts.length !== 2) return false;
      const mod = parts[0];
      const act = parts[1];

      // Smart Access for READ/VIEW: If we check for the base module view
      if (act === 'READ' || act === 'VIEW') {
          // Check for any variant that grants reading the module
          const readVariants = [
              `${mod}:READ`, `${mod}:READ_ALL`, `${mod}:READ_OWN`,
              `${mod}:VIEW`, `${mod}:VIEW_ALL`, `${mod}:VIEW_OWN`
          ];
          
          // Legacy support for CRUD strings
          readVariants.push(`${mod}:CRUD_ALL`, `${mod}:CRUD_OWN`);

          // Module-specific exceptions: specific actions that imply reading the module
          if (mod === 'CHECKIN') {
              readVariants.push('CHECKIN:QR', 'CHECKIN:MANUAL');
          }
          if (mod === 'CERTIFICADO') {
              readVariants.push('CERTIFICADO:EMITIR');
          }
          if (mod === 'ROL') {
              readVariants.push('ROL:MANAGE');
          }
          if (mod === 'CONFIGURACION') {
              readVariants.push('CONFIGURACION:MANAGE');
          }

          if (permissions.some(p => readVariants.includes(p))) return true;
      }

      // Inheritance: READ_ALL implies READ_OWN, UPDATE_ALL implies UPDATE_OWN, etc.
      if (act.endsWith('_OWN')) {
          const allVersion = permission.replace('_OWN', '_ALL');
          const crudAll = `${mod}:CRUD_ALL`;
          if (permissions.includes(allVersion) || permissions.includes(crudAll)) return true;
      }

      return false;
    },
    
    // Get available roles with both internal ID and display name
    userRoles: (state) => {
      const roleMapper = {
        'USER': 'MONITOR',
        'ADMIN': 'ADMIN',
        'OPERADOR': 'OPERADOR',
        'ORGANIZADOR': 'OPERADOR',
        'GESTOR': 'OPERADOR',
        'INVITADO': 'INVITADO'
      };
      
      const allRoles = state.user?.roles || [];
      return allRoles.map(r => {
          const raw = r.replace('ROLE_', '');
          return {
            raw: raw,
            display: roleMapper[raw] || raw
          };
        });
    },
    
    // Get the display name of the currently active role
    currentActiveRole: (state) => {
      const roleMapper = {
        'USER': 'MONITOR',
        'ADMIN': 'ADMIN',
        'OPERADOR': 'OPERADOR',
        'ORGANIZADOR': 'OPERADOR',
        'GESTOR': 'OPERADOR',
        'INVITADO': 'INVITADO'
      };
      if (!state.activeRole) return 'Usuario';
      const clean = state.activeRole.replace('ROLE_', '');
      return roleMapper[clean] || clean;
    },
    
    // Get the raw active role for internal use
    rawActiveRole: (state) => state.activeRole?.replace('ROLE_', '') || '',
  },
  actions: {
    async login(email, password) {
      try {
        const cleanEmail = email?.toLowerCase();
        const response = await api.post('/auth/login', { email: cleanEmail, password });
        this.setSession(response.data);
        
        const notificationStore = useNotificationStore();
        notificationStore.showSuccess('¡Bienvenido! Has iniciado sesión correctamente.');
        
        return Promise.resolve(response.data);
      } catch (error) {
        const notificationStore = useNotificationStore();
        notificationStore.showError('Credenciales inválidas. Por favor, inténtalo de nuevo.');
        return Promise.reject(error);
      }
    },

    async refreshToken() {
      try {
        const response = await api.post('/auth/refresh-token');
        this.setSession(response.data);
        return Promise.resolve(response.data);
      } catch (error) {
        console.error('Failed to refresh token:', error);
        return Promise.reject(error);
      }
    },

    setSession(data) {
      console.log('🔄 Session Sychronizing...', {
        email: data.email,
        activeRole: data.activeRole,
        rolesCount: data.roles?.length,
        permissions: data.rolePermissions
      });
      
      this.token = data.token;
      this.user = { 
          id: data.id,
          email: data.email, 
          nombre: data.nombre, 
          apellido: data.apellido,
          fotoPerfil: data.fotoPerfil,
          roles: data.roles 
      };
      
      this.activeRole = data.activeRole || 'Invitado';
      this.rolePermissions = data.rolePermissions || {};
      
      // Calculate Expiry from JWT if possible, fallback to 20 mins
      try {
        const payload = JSON.parse(atob(this.token.split('.')[1]));
        if (payload.exp) {
          this.expiresAt = (payload.exp * 1000).toString();
        } else {
          this.expiresAt = (new Date().getTime() + (20 * 60 * 1000)).toString();
        }
      } catch (e) {
        this.expiresAt = (new Date().getTime() + (20 * 60 * 1000)).toString();
      }
      
      localStorage.setItem('token', this.token);
      localStorage.setItem('expiresAt', this.expiresAt);
      localStorage.setItem('user', JSON.stringify(this.user));
      localStorage.setItem('activeRole', this.activeRole);
      localStorage.setItem('rolePermissions', JSON.stringify(this.rolePermissions));
    },
    
    async register(user) {
      try {
        const userData = { ...user, email: user.email?.toLowerCase() };
        const response = await api.post('/auth/signup', userData);
        const notificationStore = useNotificationStore();
        notificationStore.showSuccess('Registro exitoso. Ahora puedes iniciar sesión.');
        return response;
      } catch (error) {
        const notificationStore = useNotificationStore();
        const errorMessage = error.response?.data?.message || 'Error en el registro. Por favor, verifica tus datos.';
        notificationStore.showError(errorMessage);
        throw error;
      }
    },
    
    async switchRole(rawRoleName) {
      // Check if user actually has this role
      const hasRole = this.user?.roles?.some(r => r.replace('ROLE_', '') === rawRoleName);
      
      if (hasRole) {
        const oldRole = this.activeRole;
        this.activeRole = rawRoleName;
        localStorage.setItem('activeRole', rawRoleName);
        
        try {
          await api.post('/auditoria/log-role-switch', { from: oldRole, to: rawRoleName });
        } catch (e) {
          console.warn('Could not log role switch to audit:', e);
        }
 
        const notificationStore = useNotificationStore();
        notificationStore.showSuccess(`Ahora estás usando el rol: ${rawRoleName}`);
        
        // Dynamic reload: Redirect to Home to clear view states
        window.location.href = '/'; 
        return true;
      }
      return false;
    },
    
    logout() {
      this.token = null;
      this.user = null;
      this.activeRole = null;
      this.expiresAt = null;
      localStorage.removeItem('token');
      localStorage.removeItem('expiresAt');
      localStorage.removeItem('user');
      localStorage.removeItem('activeRole');
      localStorage.removeItem('rolePermissions');
      
      const notificationStore = useNotificationStore();
      notificationStore.showInfo('Has cerrado sesión correctamente.');
    },
  },
});