import { defineStore } from 'pinia';
import api from '../services/api';
import { useNotificationStore } from './notifications';
import { useRouter } from 'vue-router';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.user?.roles?.includes('ROLE_ADMIN'), // Adjust based on your role structure
  },
  actions: {
    async login(email, password) {
      try {
        const response = await api.post('/auth/login', { email, password });
        this.token = response.data.token; // Matches JwtResponse.java
        this.user = { 
            email: response.data.email, 
            nombre: response.data.nombre, 
            roles: response.data.roles 
        };
        
        localStorage.setItem('token', this.token);
        localStorage.setItem('user', JSON.stringify(this.user));
        
        // Show success notification
        const notificationStore = useNotificationStore();
        notificationStore.showSuccess('¡Bienvenido! Has iniciado sesión correctamente.');
        
        return Promise.resolve(response.data);
      } catch (error) {
        // Show error notification
        const notificationStore = useNotificationStore();
        notificationStore.showError('Credenciales inválidas. Por favor, inténtalo de nuevo.');
        return Promise.reject(error);
      }
    },
    async register(user) {
      try {
        const response = await api.post('/auth/signup', user);
        // Show success notification
        const notificationStore = useNotificationStore();
        notificationStore.showSuccess('Registro exitoso. Ahora puedes iniciar sesión.');
        return response;
      } catch (error) {
        // Show error notification
        const notificationStore = useNotificationStore();
        notificationStore.showError('Error en el registro. Por favor, verifica tus datos.');
        throw error;
      }
    },
    logout() {
      this.token = null;
      this.user = null;
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      
      // Show info notification
      const notificationStore = useNotificationStore();
      notificationStore.showInfo('Has cerrado sesión correctamente.');
    },
  },
});