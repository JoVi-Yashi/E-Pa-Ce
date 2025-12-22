<template>
  <div class="container animate-fade-in">
    <PageHeader 
      title="Gestión de Roles" 
      subtitle="Define las jerarquías y permisos específicos para cada perfil de usuario"
    >
      <template #actions v-if="authStore.hasPermission('ROL:MANAGE')">
        <button class="btn-premium-header" @click="resetForm(); showForm = true">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path></svg>
          <span>Crear Rol</span>
        </button>
      </template>
    </PageHeader>

    <LoadingSpinner v-if="loading" />
    <div v-if="error" class="error-msg">{{ error }}</div>

    <div class="card" v-if="!loading">
      <div v-if="roles.length > 0">
        <div class="filters-container">
          <div class="filter-item search">
            <label>Búsqueda de Rol</label>
            <div class="search-wrapper">
               <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
               <input v-model="search" type="text" placeholder="Buscar por nombre de rol..." />
            </div>
          </div>
          <div class="filter-group">
            <div class="filter-item">
                <label>Estado Acciones</label>
                <div class="custom-select-wrapper">
                  <select v-model="filterStatus">
                    <option value="">Todos</option>
                    <option value="CON">Con Acciones</option>
                    <option value="SIN">Sin Acciones</option>
                  </select>
                  <svg class="select-icon" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="6 9 12 15 18 9"></polyline></svg>
                </div>
            </div>
            <div class="filter-actions" v-if="search || filterStatus">
                 <button class="btn-clear animate-slide-in-right" @click="resetFilters" title="Limpiar todos los filtros">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M18 6L6 18M6 6l12 12"></path>
                    </svg>
                    <span>Limpiar</span>
                 </button>
            </div>
          </div>
        </div>

        <TablePagination 
          v-if="processedRoles.length > 0"
          :totalItems="processedRoles.length" 
          v-model:itemsPerPage="itemsPerPage" 
          v-model:currentPage="currentPage" 
        />
        <div class="table-responsive">
          <table class="styled-table">
            <thead>
              <tr>
                <th>Nombre Rol</th>
                <th v-if="authStore.hasPermission('ROL:MANAGE')" style="text-align: right">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="processedRoles.length === 0">
                <td colspan="2" style="text-align: center; padding: 3rem; color: var(--text-secondary);">
                  <div style="display: flex; flex-direction: column; align-items: center; gap: 1rem;">
                    <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" style="opacity: 0.3"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
                    <p style="font-weight: 600; font-size: 1.1rem;">No se encontraron coincidencias</p>
                    <p style="font-size: 0.9rem; margin-top: -0.5rem;">Prueba con otro nombre o limpia la búsqueda.</p>
                    <button class="btn-sm-outline" @click="search = ''" style="margin-top: 0.5rem;">Limpiar Búsqueda</button>
                  </div>
                </td>
              </tr>
              <tr v-else v-for="r in paginatedRoles" :key="r.idRol">
                <td>
                  <div 
                    class="role-name-info"
                    @mouseenter="e => handleMouseEnter(e, r)"
                    @mouseleave="hoveredRole = null"
                  >
                     <div :class="['status-pill', (r.permisos && r.permisos.length > 0) ? 'status-green' : 'status-red']">
                        {{ (r.permisos && r.permisos.length > 0) ? 'CON ACCIONES' : 'SIN ACCIONES' }}
                     </div>
                     <strong>{{ r.nombreRol }}</strong>

                     <!-- Hover Preview Popover (Only for Admins) -->
                     <Teleport to="body">
                       <div v-if="hoveredRole === r.idRol" 
                            class="permissions-preview-popover animate-fade-in"
                            :style="{ top: popoverPos.y + 'px', left: popoverPos.x + 'px' }">
                          <div class="popover-header">
                              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="color: var(--primary-color)"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>
                              <span>Resumen de {{ r.nombreRol }}</span>
                          </div>
                          <div class="popover-content">
                              <div v-if="!r.permisos || r.permisos.length === 0" class="no-perms-hint">
                                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>
                                  <span>Sin permisos asignados</span>
                              </div>
                              <div v-else class="popover-grid">
                                  <div v-for="(acts, mod) in getPermissionsByModule(r.permisos)" :key="mod" class="popover-mod-group">
                                      <span class="popover-mod-name">{{ mod === 'CONFIGURACION' ? 'ATRIBUTOS' : mod }}</span>
                                      <div class="popover-acts-list">
                                          <span v-for="a in acts" :key="a" class="popover-act-tag">{{ cleanActionName(a) }}</span>
                                      </div>
                                  </div>
                              </div>
                          </div>
                          <div class="popover-footer">
                              {{ r.permisos?.length || 0 }} permisos totales
                          </div>
                       </div>
                     </Teleport>
                  </div>
                </td>
                <td style="text-align: right;" v-if="authStore.hasPermission('ROL:MANAGE')">
                   <div class="action-buttons" style="justify-content: flex-end;">
                      <!-- Lock/Unlock Toggle -->
                      <TableActionButton 
                              :type="isUnlocked(r.idRol) ? 'security' : 'security-locked'"
                              @click="toggleLock(r.idRol)" 
                              :title="isUnlocked(r.idRol) ? 'Bloquear acciones' : 'Desbloquear acciones (Requiere contraseña)'"
                              :active="isUnlocked(r.idRol)"
                      >
                          <svg v-if="!isUnlocked(r.idRol)" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                          <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 9.9-1"></path></svg>
                      </TableActionButton>
      
                      <!-- Edit Button -->
                      <TableActionButton 
                              type="edit"
                              :disabled="!isUnlocked(r.idRol)"
                              @click="editRol(r)" 
                              title="Editar"
                      >
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                      </TableActionButton>
      
                      <!-- Delete Button with Strict Security Check -->
                      <TableActionButton 
                              type="delete"
                              :disabled="!isUnlocked(r.idRol)"
                              @click="promptSecurityCheck('delete', r.idRol)" 
                              title="Eliminar (Requiere confirmación)"
                      >
                         <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                      </TableActionButton>
                   </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <TablePagination 
          v-if="processedRoles.length > 0"
          :totalItems="processedRoles.length" 
          v-model:itemsPerPage="itemsPerPage" 
          v-model:currentPage="currentPage" 
        />
      </div>
      
      <EmptyState 
        v-else 
        title="No hay roles" 
        description="No se encontraron roles registrados."
      >
        <template #actions>
             <button class="btn-primary" @click="resetForm(); showForm = true">Crear Primer Rol</button>
        </template>
      </EmptyState>
    </div>

    <!-- Security Modal - Modern Design -->
    <BaseModal
        :show="showSecurityModal"
        title=" "
        maxWidth="420px"
        @close="closeSecurityModal"
    >
        <div class="security-modal-body">
            <div class="security-icon-wrapper animate-bounce-soft">
                <div class="security-pulse"></div>
                <div class="lock-icon-circle">
                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                        <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                    </svg>
                </div>
            </div>
            
            <h3 class="security-title">Zona Protegida</h3>
            <p class="security-desc">
                Esta acción requiere verificación de identidad. Por favor, confirma tus credenciales de administrador para continuar.
            </p>
            
            <form @submit.prevent="verifyAndExecute" class="security-form">
                <div class="form-group">
                    <div class="password-input-wrapper">
                        <svg class="input-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                        <input 
                            v-model="securityPassword" 
                            type="password" 
                            required 
                            placeholder="Ingrese su contraseña" 
                            autofocus 
                            class="modern-password-input"
                        />
                    </div>
                </div>
                
                <div v-if="securityError" class="security-error-msg animate-shake">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
                    {{ securityError }}
                </div>
                
                <div class="security-actions">
                    <button type="button" class="btn-ghost" @click="closeSecurityModal">Cancelar</button>
                    <button type="submit" class="btn-security-confirm" :disabled="verifying">
                        <span v-if="verifying" class="loader-dots"></span>
                        <span v-else>Confirmar Acceso</span>
                    </button>
                </div>
            </form>
        </div>
    </BaseModal>

    <!-- Form Modal -->
    <BaseModal 
        :show="showForm" 
        :title="form.idRol ? 'Configurar Rol & Permisos' : 'Nuevo Rol del Sistema'"
        maxWidth="900px"
        @close="showForm = false"
    >
        <form id="rolForm" @submit.prevent="submitForm" class="role-form-layout">
           <div class="role-basic-info">
               <div class="form-group">
                   <label>Nombre Identificador del Rol</label>
                   <input v-model="form.nombreRol" placeholder="Ej. MONITOR_AULA, COORDINADOR" required class="modern-input" />
                   <small class="helper-text">Este nombre se usará en el sistema para asignar permisos.</small>
               </div>
               
                <div class="role-summary-card glass-panel">
                   <div class="summary-title">Resumen de Configuración</div>
                   <div class="summary-stats-grid">
                       <div class="summary-stat">
                           <span class="stat-value">{{ form.permisos.length }}</span>
                           <span class="stat-label">Acciones</span>
                       </div>
                       <div class="summary-stat">
                           <span class="stat-value">{{ modules.filter(m => hasModulePerms(m)).length }}</span>
                           <span class="stat-label">Módulos</span>
                       </div>
                   </div>
                   <div class="role-health-bar">
                        <div class="health-fill" :style="{ width: (form.permisos.length / allPossiblePermissions.length * 100) + '%' }"></div>
                   </div>
                   <p class="summary-footer">
                       {{ allSelected ? 'Acceso Total al Sistema' : 'Nivel de acceso parcial' }}
                   </p>
                </div>
           </div>

           <div class="role-permissions-section">
               <div class="permissions-header">
                   <div>
                       <label class="section-label">Matriz de Permisos</label>
                       <p class="section-sublabel">Define qué puede hacer este rol en cada módulo.</p>
                   </div>
                   <button type="button" class="btn-toggle-all" @click="toggleAllPermissions" :class="{ active: allSelected }">
                      <span class="toggle-icon">
                          <svg v-if="allSelected" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
                          <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect></svg>
                      </span>
                      {{ allSelected ? 'Sistema Completo Habilitado' : 'Habilitar Acceso Total' }}
                   </button>
                </div>
               
               <div class="modules-grid-modern">
                 <div v-for="mod in modules" :key="mod" :class="['module-card', { 'module-active': hasModulePerms(mod) }]">
                    <div class="module-card-header">
                       <div class="module-header-left">
                          <span class="module-title">{{ mod === 'CONFIGURACION' ? 'ATRIBUTOS' : mod }}</span>
                          <span class="module-badge" v-if="hasModulePerms(mod)">Activo</span>
                       </div>
                       <button type="button" class="btn-mod-toggle" @click="toggleModule(mod)" :title="isModuleFull(mod) ? 'Quitar todos' : 'Marcar todos'">
                          <svg v-if="isModuleFull(mod)" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
                          <svg v-else width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect></svg>
                       </button>
                    </div>
                    
                    <div class="module-card-body">
                       <div class="permissions-list">
                           <label 
                                v-for="action in moduleActionsMap[mod]" 
                                :key="action" 
                                class="perm-toggle-item"
                                :class="{ 'perm-selected': form.permisos.includes(`${mod}:${action}`) }"
                           >
                              <input type="checkbox" :value="`${mod}:${action}`" v-model="form.permisos" class="hidden-checkbox">
                              <div class="toggle-visual">
                                  <span class="toggle-dot">
                                      <svg v-if="form.permisos.includes(`${mod}:${action}`)" viewBox="0 0 24 24" width="12" height="12" stroke="currentColor" stroke-width="4" fill="none" stroke-linecap="round" stroke-linejoin="round" style="color: white; display: block;"><polyline points="20 6 9 17 4 12"></polyline></svg>
                                  </span>
                              </div>
                              <span class="perm-name">{{ cleanActionName(action) }}</span>
                           </label>
                       </div>
                    </div>
                 </div>
               </div>
           </div>

        </form>

        <template #footer>
            <div class="modal-footer-actions">
                <button type="button" class="btn-secondary" @click="showForm = false">Cancelar</button>
                <button type="submit" form="rolForm" class="btn-primary">Guardar Configuración</button>
            </div>
        </template>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import api from '../services/api';
import PageHeader from '../components/layout/PageHeader.vue';
import LoadingSpinner from '../components/ui/LoadingSpinner.vue';
import BaseModal from '../components/modals/BaseModal.vue';
import EmptyState from '../components/ui/EmptyState.vue';
import TableActionButton from '../components/ui/TableActionButton.vue';
import { useAuthStore } from '../stores/auth';
import { useNotificationStore } from '../stores/notifications';
import { useConfirmStore } from '../stores/confirm';
import TablePagination from '../components/ui/TablePagination.vue';

const authStore = useAuthStore();
const notificationStore = useNotificationStore();
const confirmStore = useConfirmStore();

// Basic State
const roles = ref([]);
const loading = ref(false);
const error = ref('');
const showForm = ref(false);
const hoveredRole = ref(null);
const popoverPos = ref({ x: 0, y: 0 });

const handleMouseEnter = (event, role) => {
    if (!authStore.isAdmin) return;
    
    hoveredRole.value = role.idRol;
    const rect = event.target.getBoundingClientRect();
    popoverPos.value = {
        x: rect.left,
        y: rect.bottom + 8
    };
};

const getPermissionsByModule = (perms) => {
    const groups = {};
    if (!perms) return groups;
    perms.forEach(p => {
        if (!p.includes(':')) return;
        const [mod, act] = p.split(':');
        if (!groups[mod]) groups[mod] = [];
        groups[mod].push(act);
    });
    return groups;
};

const form = ref({ idRol: null, nombreRol: '', permisos: [] });

// Pagination State
const currentPage = ref(1);
const itemsPerPage = ref(10);
const search = ref('');
const filterStatus = ref('');

const resetFilters = () => {
    search.value = '';
    filterStatus.value = '';
};

const processedRoles = computed(() => {
  if (!roles.value) return [];
  let result = [...roles.value];
  
  if (search.value) {
    const q = search.value.toLowerCase();
    result = result.filter(r => r.nombreRol && r.nombreRol.toLowerCase().includes(q));
  }
  
  if (filterStatus.value === 'CON') {
    result = result.filter(r => r.permisos && r.permisos.length > 0);
  } else if (filterStatus.value === 'SIN') {
    result = result.filter(r => !r.permisos || r.permisos.length === 0);
  }
  
  return result;
});

const totalPages = computed(() => {
    const total = processedRoles.value.length;
    return Math.max(1, Math.ceil(total / itemsPerPage.value));
});

const paginatedRoles = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  return processedRoles.value.slice(start, start + itemsPerPage.value);
});

// Security State
const showSecurityModal = ref(false);
const securityPassword = ref('');
const securityError = ref('');
const verifying = ref(false);
const pendingAction = ref(null); 
const unlockedRows = ref(new Set()); // Track unlocked rows

const isUnlocked = (id) => unlockedRows.value.has(id);

const toggleLock = (id) => {
    if (isUnlocked(id)) {
        unlockedRows.value.delete(id);
    } else {
        promptSecurityCheck('unlock', id);
    }
};

const moduleActionsMap = {
    'EVENTO': ['READ_ALL', 'READ_OWN', 'CREATE', 'UPDATE_ALL', 'UPDATE_OWN', 'DELETE_ALL', 'DELETE_OWN', 'SHOW_QR', 'READ_PARTICIPANTS'],
    'PARTICIPANTE': ['READ_ALL', 'READ_OWN', 'CREATE', 'UPDATE_ALL', 'UPDATE_OWN', 'DELETE_ALL', 'DELETE_OWN'],
    'PARTICIPACION': ['READ_ALL', 'READ_OWN', 'CREATE', 'UPDATE_ALL', 'UPDATE_OWN', 'DELETE_ALL', 'DELETE_OWN'],
    'CERTIFICADO': ['READ_ALL', 'READ_OWN', 'EMITIR', 'DELETE_ALL'],
    'CHECKIN': ['READ_ALL', 'READ_OWN', 'QR', 'MANUAL', 'DELETE_ALL'],
    'ROL': ['READ', 'MANAGE'],
    'AUDITORIA': ['READ'],
    'CONFIGURACION': ['READ_ALL', 'CREATE', 'UPDATE_ALL', 'DELETE_ALL', 'MANAGE'],
    'REPORTE': ['VIEW'],
    'DASHBOARD': ['READ'],
    'ALL': ['ALL']
};

const modules = Object.keys(moduleActionsMap);

const hasModulePerms = (mod) => {
    return form.value.permisos.some(p => p.startsWith(`${mod}:`));
};

const cleanActionName = (action) => {
    const translations = {
        'READ_ALL': 'Ver Todo',
        'READ_OWN': 'Ver Propio',
        'READ': 'Leer/Ver',
        'CREATE': 'Crear',
        'UPDATE_OWN': 'Editar Propio',
        'UPDATE_ALL': 'Editar Todo',
        'DELETE_OWN': 'Eliminar Propio',
        'DELETE_ALL': 'Eliminar Todo',
        'CRUD_OWN': 'Gestionar Propio',
        'CRUD_ALL': 'Gestionar Todo',
        'VIEW': 'Visualizar',
        'EMITIR': 'Emitir/Generar',
        'QR': 'Uso Escáner QR',
        'MANUAL': 'Ingreso Manual',
        'MANAGE': 'Administrar',
        'ALL': 'Control Total',
        'SHOW_QR': 'Mostrar QR Acceso',
        'READ_PARTICIPANTS': 'Ver Participantes'
    };
    return translations[action] || action.replace('_', ' ');
};

const isModuleFull = (mod) => {
    const actions = moduleActionsMap[mod];
    return actions.every(a => form.value.permisos.includes(`${mod}:${a}`));
};

const toggleModule = (mod) => {
    const actions = moduleActionsMap[mod].map(a => `${mod}:${a}`);
    if (isModuleFull(mod)) {
        form.value.permisos = form.value.permisos.filter(p => !actions.includes(p));
    } else {
        const otherPerms = form.value.permisos.filter(p => !actions.includes(p));
        form.value.permisos = [...otherPerms, ...actions];
    }
};

const allPossiblePermissions = computed(() => {
    const perms = [];
    Object.entries(moduleActionsMap).forEach(([mod, acts]) => {
        acts.forEach(a => perms.push(`${mod}:${a}`));
    });
    return perms;
});

const allSelected = computed(() => {
    return form.value.permisos.length === allPossiblePermissions.value.length;
});

const toggleAllPermissions = () => {
    if (allSelected.value) {
        form.value.permisos = [];
    } else {
        form.value.permisos = [...allPossiblePermissions.value];
    }
};

const mapLegacyPermissions = (perms) => {
    if (!perms) return [];
    const migrationMap = {
        'EVENTO:READ': 'EVENTO:READ_ALL',
        'PARTICIPANTE:READ': 'PARTICIPANTE:READ_ALL',
        'PARTICIPACION:READ': 'PARTICIPACION:READ_ALL',
        'CERTIFICADO:VIEW': 'CERTIFICADO:READ_ALL',
        'CERTIFICACION:VIEW': 'CERTIFICADO:READ_ALL',
        'REPORTE:VIEW': 'CHECKIN:READ_ALL',
        'PARTICIPACION:CRUD_ALL': 'PARTICIPACION:DELETE_ALL',
        'PARTICIPACION:CRUD_OWN': 'PARTICIPACION:DELETE_OWN',
        'CERTIFICADO:EMITIR_ALL': 'CERTIFICADO:EMITIR'
    };

    return perms.map(p => migrationMap[p] || p);
};

const fetchRoles = async () => {
  loading.value = true;
  try {
    const res = await api.get('/roles');
    roles.value = res.data.map(r => ({
        ...r,
        permisos: mapLegacyPermissions(r.permisos || [])
    }));
    currentPage.value = 1; // RESET
  } catch (err) {
    notificationStore.showError('Error al cargar roles: ' + (err.response?.data?.message || err.message));
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  form.value = { idRol: null, nombreRol: '', permisos: [] };
};

const promptSecurityCheck = (type, payload) => {
    pendingAction.value = { type, payload };
    securityPassword.value = '';
    securityError.value = '';
    showSecurityModal.value = true;
};

const closeSecurityModal = () => {
    showSecurityModal.value = false;
    pendingAction.value = null;
    securityPassword.value = '';
};

const verifyAndExecute = async () => {
    if (!securityPassword.value) return;
    
    verifying.value = true;
    securityError.value = '';
    
    try {
        const userStr = localStorage.getItem('user');
        if (!userStr) throw new Error("No hay sesión activa");
        const user = JSON.parse(userStr);

        await api.post('/auth/login', { 
            email: user.email, 
            password: securityPassword.value 
        });
        
        showSecurityModal.value = false;
        
        // Execute Action
        if (pendingAction.value.type === 'unlock') {
            unlockedRows.value.add(pendingAction.value.payload);
        } else if (pendingAction.value.type === 'edit') {
            editRol(pendingAction.value.payload);
        } else if (pendingAction.value.type === 'delete') {
            await deleteRol(pendingAction.value.payload);
        }
    } catch (err) {
        notificationStore.showError('Contraseña incorrecta o error de verificación.');
    } finally {
        verifying.value = false;
    }
};

const editRol = (r) => {
  form.value = { ...r, permisos: [...(r.permisos || [])] };
  showForm.value = true;
};

const deleteRol = async (id) => {
  // if (!confirm('Eliminar seguro?')) return; // handled by security check
  try {
    await api.delete(`/roles/${id}`);
    notificationStore.showSuccess('Rol eliminado correctamente');
    fetchRoles();
  } catch (err) {
    notificationStore.showError('Error al eliminar rol: ' + (err.response?.data?.message || err.message));
  }
};

const submitForm = async () => {
  try {
    if (form.value.idRol) {
      await api.put(`/roles/${form.value.idRol}`, form.value);
      notificationStore.showSuccess('Rol actualizado correctamente');
    } else {
      await api.post('/roles', form.value);
      notificationStore.showSuccess('Rol creado correctamente');
    }
    showForm.value = false;
    await fetchRoles();
    
    // Sync permissions with the current session dynamically
    try {
        await authStore.refreshToken();
        notificationStore.showInfo('Sincronizando permisos...');
        
        // Final reload to ensure all UI elements reflect changes
        setTimeout(() => {
            window.location.reload();
        }, 800);
    } catch (e) {
        console.warn("Could not sync session permissions after update", e);
    }
} catch (err) {
    notificationStore.showError('Error al guardar rol: ' + (err.response?.data?.message || err.message));
  }
};

onMounted(fetchRoles);
</script>

<style scoped>
/* Page specific styles */
.role-name-info {
    display: flex;
    align-items: center;
    gap: 12px;
}

.status-pill {
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 0.65rem;
    font-weight: 800;
    text-transform: uppercase;
}

.status-green {
    background: #f0fdf4;
    color: #16a34a;
    border: 1px solid #bcf0da;
}

.status-red {
    background: #fef2f2;
    color: #dc2626;
    border: 1px solid #fecaca;
}

/* Table Button Fixes */
/* Table Button Styles are now managed by TableActionButton component */
.action-buttons {
    display: flex;
    gap: 4px;
    align-items: center;
}

.role-form-layout {
    display: grid;
    grid-template-columns: 280px 1fr;
    gap: 2rem;
    min-height: 500px;
}

.role-basic-info {
    border-right: 1px solid #e2e8f0;
    padding-right: 1.5rem;
}

.role-summary-card {
    border-radius: 16px;
    padding: 1.5rem;
    margin-top: 2rem;
    border: 1px solid rgba(226, 232, 240, 0.8);
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

/* Filters & Sort Styles */
.filters-container {
    display: flex;
    gap: 1.5rem;
    margin-bottom: 1.5rem;
    align-items: flex-end;
    flex-wrap: wrap;
    background: #f8fafc;
    padding: 1rem;
    border-radius: 12px;
    border: 1px solid #edf2f7;
}

.filter-item {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.filter-item.search {
    flex: 1;
    max-width: 500px;
}

.filter-group {
    display: flex;
    gap: 1.5rem;
    align-items: flex-end;
    margin-left: auto;
}

.filter-item label {
    font-size: 0.75rem;
    font-weight: 700;
    color: #94a3b8;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-bottom: 6px;
}

.search-wrapper, .custom-select-wrapper {
    box-sizing: border-box;
    display: flex;
    align-items: center;
    background: white;
    border: 1px solid #e2e8f0;
    border-radius: 10px;
    height: 42px;
    padding: 0 12px;
    transition: all 0.2s;
}

.search-wrapper:focus-within, .custom-select-wrapper:focus-within {
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-wrapper svg, .custom-select-wrapper .select-icon {
    color: #94a3b8;
    flex-shrink: 0;
}

.search-wrapper svg {
    margin-right: 10px;
}

.search-wrapper input {
    border: none;
    background: transparent;
    padding: 0;
    margin: 0;
    width: 100%;
    height: 100%;
    font-size: 0.95rem;
    color: #1a202c;
    flex: 1;
    outline: none;
}

.custom-select-wrapper select {
    border: none;
    background: transparent;
    padding: 0;
    margin: 0;
    width: 100%;
    height: 100%;
    font-size: 0.95rem;
    color: #1e293b;
    cursor: pointer;
    font-weight: 600;
    outline: none;
    appearance: none;
    flex: 1;
}

.select-icon {
    margin-left: 8px;
    pointer-events: none;
}

.filter-actions {
    display: flex;
    align-items: center;
    height: 42px;
}

.btn-clear {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    background: white;
    border: 1px solid #e2e8f0;
    color: #64748b;
    padding: 0 1.25rem;
    height: 100%;
    border-radius: 10px;
    font-size: 0.9rem;
    font-weight: 700;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-clear:hover {
    background: #fff5f5;
    color: #e53e3e;
    border-color: #fecaca;
    transform: translateY(-2px);
}

.animate-slide-in-right {
    animation: slideInRight 0.4s ease-out forwards;
}

@keyframes slideInRight {
    from { opacity: 0; transform: translateX(20px); }
    to { opacity: 1; transform: translateX(0); }
}

.summary-title {
    font-size: 0.8rem;
    font-weight: 700;
    color: #64748b;
    text-transform: uppercase;
    margin-bottom: 1.25rem;
    letter-spacing: 0.025em;
}

.summary-stats-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1rem;
    margin-bottom: 1.25rem;
}

.summary-stat {
    display: flex;
    flex-direction: column;
    align-items: center;
    background: white;
    padding: 0.75rem;
    border-radius: 10px;
    border: 1px solid #e2e8f0;
}

.stat-label {
    font-size: 0.7rem;
    color: #94a3b8;
    text-transform: uppercase;
    font-weight: 600;
}

.stat-value {
    font-size: 1.25rem;
    font-weight: 800;
    color: #0f172a;
}

.role-health-bar {
    height: 6px;
    background: #e2e8f0;
    border-radius: 10px;
    margin-bottom: 0.75rem;
    overflow: hidden;
}

.health-fill {
    height: 100%;
    background: var(--primary-gradient);
    transition: width 0.3s ease;
}

.summary-footer {
    font-size: 0.75rem;
    color: #64748b;
    text-align: center;
    font-weight: 500;
}

.permissions-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
    padding-bottom: 0.75rem;
    border-bottom: 2px solid #f1f5f9;
}

.permissions-header label {
    font-size: 0.9rem;
    font-weight: 700;
    color: #1e293b;
    margin: 0;
    padding-right: 1.5rem; /* Space from the button */
}

.permissions-header .btn-sm-outline {
    margin-left: auto; /* Push to the right if in a flex-start context, but keep distance */
    margin-right: 0;
    padding: 8px 18px;
    font-size: 0.75rem;
    letter-spacing: 0.025em;
}

.btn-sm-outline {
    background: transparent;
    border: 1.5px solid var(--primary-accent);
    color: var(--primary-accent);
    padding: 6px 12px;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
}

.btn-sm-outline:hover {
    background: var(--primary-accent);
    color: white;
    transform: translateY(-1px);
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.modules-grid-modern {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 1.25rem;
    overflow-y: auto;
    max-height: 550px;
    padding-right: 0.5rem;
}

.module-group {
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    overflow: hidden;
    transition: all 0.2s;
}

.module-group-header {
    background: #f8fafc;
    padding: 0.85rem 1.25rem;
    font-weight: 800;
}

/* --- New Security Modal Styles --- */
.security-modal-body {
    text-align: center;
    padding: 1rem 0;
}

.security-icon-wrapper {
    position: relative;
    width: 64px;
    height: 64px;
    margin: 0 auto 1.5rem;
    display: flex;
    align-items: center;
    justify-content: center;
}

.lock-icon-circle {
    width: 100%;
    height: 100%;
    background: #eef2ff;
    color: #4f46e5;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    z-index: 2;
}

.security-pulse {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    border-radius: 50%;
    background: rgba(79, 70, 229, 0.3);
    z-index: 1;
    animation: pulse-ring 2s cubic-bezier(0.215, 0.61, 0.355, 1) infinite;
}

@keyframes pulse-ring {
    0% { transform: scale(0.8); opacity: 0.8; }
    100% { transform: scale(2.2); opacity: 0; }
}

.security-title {
    font-size: 1.5rem;
    font-weight: 800;
    color: #1e293b;
    margin-bottom: 0.5rem;
}

.security-desc {
    font-size: 0.95rem;
    color: #64748b;
    margin-bottom: 2rem;
    line-height: 1.5;
}

.password-input-wrapper {
    position: relative;
    margin-bottom: 1rem;
}

.input-icon {
    position: absolute;
    left: 14px;
    top: 50%;
    transform: translateY(-50%);
    color: #94a3b8;
}

.modern-password-input {
    width: 100%;
    padding: 14px 14px 14px 44px;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    font-size: 1rem;
    transition: all 0.2s;
    outline: none;
    background: #f8fafc;
}

.modern-password-input:focus {
    border-color: #4f46e5;
    background: white;
    box-shadow: 0 0 0 4px rgba(79, 70, 229, 0.1);
}

.security-actions {
    display: flex;
    gap: 1rem;
    margin-top: 2rem;
    justify-content: center;
}

.btn-security-confirm {
    background: #4f46e5;
    color: white;
    border: none;
    padding: 12px 24px;
    border-radius: 10px;
    font-weight: 600;
    font-size: 1rem;
    cursor: pointer;
    transition: all 0.2s;
    flex: 1;
}

.btn-security-confirm:hover {
    background: #4338ca;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
}

.btn-ghost {
    background: transparent;
    border: none;
    color: #64748b;
    font-weight: 600;
    cursor: pointer;
    padding: 0 1rem;
}

.security-error-msg {
    color: #ef4444;
    font-size: 0.85rem;
    font-weight: 600;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    margin-top: 0.5rem;
    background: #fef2f2;
    padding: 8px;
    border-radius: 8px;
}

/* --- New Permissions UI --- */
.permissions-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-bottom: 2rem;
    border-bottom: 2px solid #f1f5f9;
    padding-bottom: 1rem;
}

.section-label {
    font-size: 1.1rem;
    font-weight: 800;
    color: #1e293b;
    display: block;
    margin-bottom: 4px;
}

.section-sublabel {
    font-size: 0.85rem;
    color: #94a3b8;
    margin: 0;
}

.btn-toggle-all {
    background: white;
    border: 1px solid #e2e8f0;
    color: #64748b;
    padding: 8px 16px;
    border-radius: 8px;
    font-weight: 600;
    font-size: 0.85rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 8px;
    transition: all 0.2s;
}

.btn-toggle-all.active {
    background: #eef2ff;
    color: #4f46e5;
    border-color: #c7d2fe;
}

.module-card {
    background: white;
    border: 1px solid #f1f5f9;
    border-radius: 16px;
    padding: 1.25rem;
    transition: all 0.3s ease;
    display: flex;
    flex-direction: column;
}

.module-card:hover {
    box-shadow: 0 10px 30px -10px rgba(0,0,0,0.08);
    transform: translateY(-2px);
    border-color: #e2e8f0;
}

.module-card.module-active {
    border-left: 4px solid #4f46e5;
}

.module-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
}

.module-title {
    font-weight: 800;
    font-size: 0.95rem;
    color: #334155;
}

.module-badge {
    background: #4f46e5;
    color: white;
    font-size: 0.65rem;
    padding: 2px 8px;
    border-radius: 12px;
    font-weight: 700;
    text-transform: uppercase;
}

.permissions-list {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
}

.perm-toggle-item {
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
    padding: 6px;
    border-radius: 8px;
    transition: background 0.2s;
}

.perm-toggle-item:hover {
    background: #f8fafc;
}

.hidden-checkbox {
    display: none;
}

.toggle-visual {
    width: 36px;
    height: 20px;
    background: #e2e8f0;
    border-radius: 20px;
    position: relative;
    transition: all 0.3s ease;
}

.perm-name {
    font-size: 0.9rem;
    color: #64748b;
    font-weight: 500;
    user-select: none;
}

.perm-selected .perm-name {
    color: #1e293b;
    font-weight: 600;
}

@media (max-width: 768px) {
    .role-form-layout {
        grid-template-columns: 1fr;
    }
    .role-basic-info {
        border-right: none;
        border-bottom: 1px solid #e2e8f0;
        padding-right: 0;
        padding-bottom: 1.5rem;
    }
}
.module-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem;
    border-bottom: 1px solid #f1f5f9;
    background: #f8fafc;
}

.module-header-left {
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.btn-mod-toggle {
    background: white;
    border: 1px solid #e2e8f0;
    width: 28px;
    height: 28px;
    padding: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 6px;
    cursor: pointer;
    color: #94a3b8;
    transition: all 0.2s;
    box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.btn-mod-toggle:hover {
    border-color: #667eea;
    color: #667eea;
    background: #f0f7ff;
}

.permissions-list {
    padding: 0.5rem;
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.perm-toggle-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 8px 10px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s;
}

.perm-toggle-item:hover {
    background: #f8fafc;
}

.perm-selected {
    background: #f0f7ff !important;
}

.hidden-checkbox {
    position: absolute;
    opacity: 0;
    cursor: pointer;
    height: 0;
    width: 0;
}

.toggle-visual {
    width: 36px;
    height: 18px;
    background: #e2e8f0;
    border-radius: 20px;
    position: relative;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    flex-shrink: 0;
    cursor: pointer;
}

.perm-selected .toggle-visual {
    background: #4f46e5;
}

.toggle-dot {
    width: 14px;
    height: 14px;
    background: white;
    border-radius: 50%;
    position: absolute;
    top: 2px;
    left: 2px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 1px 2px rgba(0,0,0,0.1);
    display: flex;
    align-items: center;
    justify-content: center;
}

.perm-selected .toggle-dot {
    transform: translateX(18px);
}

.perm-name {
    font-size: 0.85rem;
    font-weight: 500;
    color: #475569;
}

.perm-selected .perm-name {
    color: #1e1b4b;
    font-weight: 700;
}

.modules-grid-modern {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 1.25rem;
}

.module-card {
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    overflow: hidden;
    background: white;
    transition: all 0.2s;
}

.module-card:hover {
    border-color: #cbd5e1;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.module-active {
    border-color: #c7d2fe;
}

.module-title {
    font-weight: 800;
    font-size: 0.75rem;
    letter-spacing: 0.05em;
    color: #64748b;
}

.module-active .module-title {
    color: #4f46e5;
}

.module-badge {
    font-size: 0.65rem;
    background: #e0e7ff;
    color: #4338ca;
    padding: 1px 6px;
    border-radius: 10px;
    font-weight: 800;
}
/* Hover Preview Popover */
.role-name-info {
    position: relative;
    cursor: default;
}

.permissions-preview-popover {
    position: fixed; /* Fix for table clipping */
    z-index: 10000;
    width: 320px;
    background: rgba(255, 255, 255, 0.98);
    border-radius: 16px;
    box-shadow: 0 20px 50px rgba(0,0,0,0.3), 0 0 0 1px rgba(0,0,0,0.1);
    padding: 0;
    margin-top: 5px;
    pointer-events: none !important; /* Forces scroll events to pass through */
    overflow: hidden;
    backdrop-filter: blur(12px);
    border: 1px solid rgba(255,255,255,1);
    transform: translateY(10px);
    user-select: none;
}

.popover-header {
    background: linear-gradient(to right, #f8fafc, #f1f5f9);
    padding: 12px 16px;
    border-bottom: 1px solid #e2e8f0;
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 0.85rem;
    font-weight: 800;
    color: #0f172a;
}

.popover-content {
    max-height: 300px;
    overflow-y: auto;
    padding: 16px;
    scrollbar-width: thin;
}

.popover-content::-webkit-scrollbar {
    width: 4px;
}

.popover-content::-webkit-scrollbar-thumb {
    background: #cbd5e1;
    border-radius: 10px;
}

.popover-grid {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.popover-mod-group {
    display: flex;
    flex-direction: column;
    gap: 8px;
    padding-bottom: 12px;
    border-bottom: 1px dashed #e2e8f0;
}

.popover-mod-group:last-child {
    border-bottom: none;
    padding-bottom: 0;
}

.popover-mod-name {
    font-size: 0.75rem;
    font-weight: 900;
    color: var(--primary-color);
    text-transform: uppercase;
    letter-spacing: 1px;
}

.popover-acts-list {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
}

.popover-act-tag {
    background: #f8fafc;
    color: #334155;
    font-size: 0.7rem;
    padding: 4px 10px;
    border-radius: 6px;
    font-weight: 600;
    border: 1px solid #e2e8f0;
    box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.no-perms-hint {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px 0;
    color: #94a3b8;
    gap: 8px;
    font-size: 0.85rem;
}

.popover-footer {
    background: #f1f5f9;
    padding: 8px 14px;
    font-size: 0.7rem;
    font-weight: 600;
    color: #64748b;
    text-align: right;
    border-top: 1px solid #e2e8f0;
}

.text-truncate {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
</style>
