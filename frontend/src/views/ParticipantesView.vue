<template>
  <div class="container animate-fade-in">
    <PageHeader 
      title="Gestión de Usuarios" 
      subtitle="Administra el personal, roles y permisos de acceso al sistema"
    >
      <template #actions>
        <button v-if="authStore.hasPermission('PARTICIPANTE:UPDATE_ALL')" class="btn-glass-header" @click="openImportModal">
           <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="17 8 12 3 7 8"></polyline><line x1="12" y1="3" x2="12" y2="15"></line></svg>
           <span>Importar CSV</span>
        </button>
        <button v-if="authStore.hasPermission('PARTICIPANTE:CREATE')" class="btn-premium-header" @click="openCreateModal">
           <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="8.5" cy="7" r="4"></circle><line x1="20" y1="8" x2="20" y2="14"></line><line x1="23" y1="11" x2="17" y2="11"></line></svg>
           <span>Nuevo Usuario</span>
        </button>
      </template>
    </PageHeader>

    <LoadingSpinner v-if="loading" />
    <div v-if="error" class="error-msg">{{ error }}</div>

    <BaseCard v-if="!loading">
      <div v-if="users.length > 0">
        <!-- Search & Filters -->
        <div class="filters-container">
          <div class="filter-item search">
            <label>Búsqueda</label>
            <div class="search-wrapper">
               <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
               <input v-model="search" type="text" placeholder="Nombre, apellido o documento..." />
            </div>
          </div>
          <div class="filter-group">
            <div class="filter-item">
              <label>Rol</label>
              <div class="custom-select-wrapper">
                <select v-model="filterRole">
                  <option value="">Cualquier rol</option>
                  <option value="ALL_ASSIGNED">Con todos los roles</option>
                  <option v-for="role in roles" :key="role.idRol" :value="role.nombreRol">{{ role.nombreRol }}</option>
                </select>
                <svg class="select-icon" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="6 9 12 15 18 9"></polyline></svg>
              </div>
            </div>
            <div class="filter-item">
              <label>Estado</label>
              <div class="custom-select-wrapper">
                <select v-model="filterStatus">
                  <option value="">Cualquier estado</option>
                  <option value="HABILITADO">Habilitado</option>
                  <option value="INHABILITADO">Inhabilitado</option>
                </select>
                <svg class="select-icon" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="6 9 12 15 18 9"></polyline></svg>
              </div>
            </div>
            <div class="filter-actions" v-if="hasActiveFilters">
              <button class="btn-clear animate-slide-in-right" @click="resetFilters" title="Limpiar todos los filtros">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M18 6L6 18M6 6l12 12"></path>
                  </svg>
                  <span>Limpiar</span>
              </button>
            </div>
          </div>
        </div>

        <div class="table-top-actions">
           <TablePagination 
            v-if="processedUsers.length > 0"
            :totalItems="processedUsers.length" 
            v-model:itemsPerPage="itemsPerPage" 
            v-model:currentPage="currentPage" 
          />
        </div>

        <div class="table-responsive mobile-cards">
          <table class="styled-table">
            <thead>
              <tr>
                <th @click="toggleSort('documentoIdentidad')" class="sortable">
                  Documento
                  <span class="sort-indicator" v-if="sortBy === 'documentoIdentidad'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
                </th>
                <th @click="toggleSort('nombre')" class="sortable">
                  Nombre
                  <span class="sort-indicator" v-if="sortBy === 'nombre'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
                </th>
                <th @click="toggleSort('email')" class="sortable">
                  Email
                  <span class="sort-indicator" v-if="sortBy === 'email'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
                </th>
                <th>Roles</th>
                <th @click="toggleSort('estado')" class="sortable">
                  Estado
                  <span class="sort-indicator" v-if="sortBy === 'estado'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
                </th>
                <th v-if="authStore.hasPermission('PARTICIPANTE:UPDATE_ALL') || authStore.hasPermission('PARTICIPANTE:UPDATE_OWN') || authStore.hasPermission('PARTICIPANTE:DELETE_ALL') || authStore.hasPermission('PARTICIPANTE:DELETE_OWN')" style="text-align: right">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="processedUsers.length === 0">
                <td colspan="6" style="text-align: center; padding: 3rem; color: var(--text-secondary);">
                  <div style="display: flex; flex-direction: column; align-items: center; gap: 1rem;">
                    <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" style="opacity: 0.3"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
                    <p style="font-weight: 600; font-size: 1.1rem;">No se encontraron coincidencias</p>
                    <p style="font-size: 0.9rem; margin-top: -0.5rem;">Prueba ajustando los filtros o el término de búsqueda.</p>
                    <button class="btn-sm-outline" @click="resetFilters" style="margin-top: 0.5rem;">Limpiar Filtros</button>
                  </div>
                </td>
              </tr>
              <tr v-else v-for="user in paginatedUsers" :key="user.documentoIdentidad">
                <td data-label="Documento">{{ user.documentoIdentidad }}</td>
                <td data-label="Nombre">
                   <div class="user-info-wrapper">
                      <div class="user-avatar-small">
                         <img v-if="user.fotoPerfil" :src="user.fotoPerfil" alt="Avatar" />
                         <span v-else>{{ getInitials(user.nombre) }}</span>
                      </div>
                      <div class="user-text-info">
                         <strong>{{ user.nombre }} {{ user.apellido }}</strong>
                      </div>
                   </div>
                </td>
                <td data-label="Email">{{ user.email }}</td>
                <td data-label="Roles">
                  <div class="roles-display">
                    <span v-if="getUserRolesList(user).length >= 4" class="role-badge all-roles">Todos</span>
                    <template v-else>
                      <span 
                        v-for="role in getUserRolesList(user)" 
                        :key="role" 
                        class="role-badge"
                        :class="getRoleBadgeClass(role)"
                      >{{ role }}</span>
                    </template>
                    <span v-if="getUserRolesList(user).length === 0" class="role-badge no-role">Sin rol</span>
                  </div>
                </td>
                <td data-label="Estado">
                   <span class="status-badge" :class="user.estado === 'HABILITADO' ? 'active' : 'inactive'">
                      {{ user.estado }}
                   </span>
                </td>
                <td v-if="authStore.hasPermission('PARTICIPANTE:UPDATE_ALL') || authStore.hasPermission('PARTICIPANTE:UPDATE_OWN') || authStore.hasPermission('PARTICIPANTE:DELETE_ALL') || authStore.hasPermission('PARTICIPANTE:DELETE_OWN')" data-label="Acciones" style="text-align: right">
                   <div class="action-buttons">
                     <TableActionButton v-if="authStore.can('UPDATE', 'PARTICIPANTE', user.documentoIdentidad)" type="edit" title="Editar" @click="editUser(user)">
                       <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                     </TableActionButton>
                     <TableActionButton v-if="authStore.can('DELETE', 'PARTICIPANTE', user.documentoIdentidad)" type="delete" title="Eliminar" @click="deleteUser(user.documentoIdentidad)">
                       <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                     </TableActionButton>
                   </div>
                 </td>
              </tr>
            </tbody>
          </table>
        </div>

        <TablePagination 
          v-if="processedUsers.length > 0"
          :totalItems="processedUsers.length" 
          v-model:itemsPerPage="itemsPerPage" 
          v-model:currentPage="currentPage" 
        />
      </div>
      
      <EmptyState 
        v-else 
        title="No hay usuarios" 
        description="No se han encontrado usuarios registrados en el sistema."
      >
        <template #actions>
             <button class="btn-primary" @click="openCreateModal">Crear Primer Usuario</button>
        </template>
      </EmptyState>
    </BaseCard>

    <!-- Create/Edit Modal -->
    <BaseModal 
        :show="showForm" 
        :title="isEdit ? 'Editar Usuario' : 'Nuevo Usuario'"
        @close="showForm = false"
    >
        <form id="userForm" @submit.prevent="submitForm">
           <div class="user-edit-header" v-if="isEdit || form.fotoPerfil">
             <div class="user-avatar-preview">
                 <img v-if="form.fotoPerfil" :src="form.fotoPerfil" class="preview-img" alt="Previsualización" />
                 <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                     <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                     <circle cx="12" cy="7" r="4"></circle>
                 </svg>
             </div>
             <input type="file" ref="fileInput" @change="handleFileChange" accept="image/*" style="display: none" />
            <div class="user-info-text">
              <h3>{{ form.nombre || 'Nuevo' }} {{ form.apellido || 'Usuario' }}</h3>
              <p>{{ (isEdit && user) ? form.email : 'Configuración de imagen' }}</p>
            </div>
          </div>

          <div class="form-grid">
             <div class="form-group full-width">
                <label>Documento de Identidad</label>
                <input v-model="form.documentoIdentidad" type="number" required placeholder="Cédula / Documento" :disabled="isEdit" />
             </div>
             <div class="form-row">
               <div class="form-group">
                  <label>Nombre</label>
                  <input v-model="form.nombre" required placeholder="Nombre" />
               </div>
               <div class="form-group">
                  <label>Apellido</label>
                  <input v-model="form.apellido" required placeholder="Apellido" />
               </div>
             </div>
             <div class="form-group full-width">
                <label>Email</label>
                <input v-model="form.email" type="email" required placeholder="usuario@ejemplo.com" />
             </div>
             <div class="form-group" v-if="!isEdit">
                <label>Contraseña</label>
                <input v-model="form.password" type="password" required minlength="6" placeholder="******" />
             </div>
              <div class="form-group" v-if="isEdit">
                <label>Estado de Cuenta</label>
                <select v-model="form.estado" class="form-control">
                   <option value="HABILITADO">Habilitado</option>
                   <option value="INHABILITADO">Inhabilitado</option>
                </select>
              </div>
              <div class="form-group full-width">
                <label>Roles Asignados</label>
                <div class="roles-selector">
                   <div v-for="role in roles" :key="role.idRol" class="role-checkbox">
                      <input 
                         type="checkbox" 
                         :id="'role-' + role.idRol" 
                         :value="role.idRol"
                         v-model="form.selectedRoles"
                      />
                      <label :for="'role-' + role.idRol">{{ role.nombreRol }}</label>
                   </div>
                </div>
                <div class="roles-actions">
                   <button type="button" class="btn-sm" @click="selectAllRoles">
                      {{ allRolesSelected ? 'Quitar Todos' : 'Seleccionar Todos' }}
                   </button>
                </div>
                <p class="helper-text" v-if="form.selectedRoles.length === 0" style="color: #e53e3e; font-size: 0.85rem; margin-top: 0.5rem;">
                   Selecciona al menos un rol
                </p>
              </div>
          </div>
        </form>
        
        <template #footer>
            <button type="button" class="btn-secondary" @click="showForm = false">Cancelar</button>
            <button type="submit" form="userForm" class="btn-primary">
               {{ isEdit ? 'Actualizar' : 'Crear Usuario' }}
            </button>
        </template>
    </BaseModal>

     <BaseModal
        :show="showImportForm"
        title="Importar Usuarios (CSV)"
        maxWidth="800px"
        @close="showImportForm = false"
    >
       <div class="import-instructions">
          <p>Cargue un archivo CSV con las columnas: <strong>Doc, Nombre, Apellido, Email, Password, Rol</strong></p>
          <button @click="downloadTemplate" class="link-btn">
             <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:4px"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
             Descargar Plantilla CSV
          </button>
       </div>

       <div 
         class="dropzone" 
         @dragover.prevent 
         @drop.prevent="handleDrop" 
         @click="$refs.fileInput.click()"
       >
          <input type="file" ref="fileInput" @change="handleFileSelect" accept=".csv" hidden />
          <p v-if="!importFile">Arrastra un archivo CSV aquí o haz clic para seleccionar</p>
          <p v-else>Archivo seleccionado: <strong>{{ importFile.name }}</strong></p>
       </div>

       <!-- Preview Section -->
       <div v-if="previewData.length > 0" class="import-preview-container">
           <div class="preview-header">
               <h4>Previsualización de Datos ({{ previewData.length }} filas)</h4>
               <div class="preview-stats">
                   <span class="stat valid">{{ validRowsCount }} Válidos</span>
                   <span class="stat invalid" v-if="invalidRowsCount > 0">{{ invalidRowsCount }} Con errores</span>
               </div>
           </div>
           
           <div class="table-responsive preview-table">
               <table class="styled-table mini">
                   <thead>
                       <tr>
                           <th>Estado</th>
                           <th>Doc</th>
                           <th>Nombre completo</th>
                           <th>Email</th>
                           <th>Rol</th>
                       </tr>
                   </thead>
                   <tbody>
                       <tr v-for="(row, idx) in previewData" :key="idx" :class="{ 'row-error': row.errors.length > 0 }">
                           <td>
                               <span v-if="row.errors.length === 0" class="badge-dot success" title="Todo bien"></span>
                               <span v-else class="badge-dot error" :title="row.errors.join(', ')"></span>
                           </td>
                           <td>{{ row.doc }}</td>
                           <td>{{ row.nombre }} {{ row.apellido }}</td>
                           <td>{{ row.email }}</td>
                           <td>{{ row.rol }}</td>
                       </tr>
                   </tbody>
               </table>
           </div>
           
           <div v-if="invalidRowsCount > 0" class="validation-warning">
               <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
               <span>Corrija los errores en su archivo antes de continuar. Los documentos o emails duplicados no están permitidos.</span>
           </div>
       </div>
       
       <div v-if="importResult" class="import-report">
           <div class="report-summary">
               <div class="summary-item success">
                   <span class="label">Exitosos</span>
                   <span class="value">{{ importResult.successful }}</span>
               </div>
               <div class="summary-item failed">
                   <span class="label">Fallidos</span>
                   <span class="value">{{ importResult.failed }}</span>
               </div>
           </div>
           <div v-if="importResult.errors.length > 0" class="error-container">
               <h4>Log de Errores:</h4>
               <ul class="error-list">
                   <li v-for="(err, i) in importResult.errors" :key="i">{{ err }}</li>
               </ul>
           </div>
       </div>

        <template #footer>
             <button class="btn-secondary" @click="showImportForm = false">Cancelar</button>
             <button class="btn-primary" @click="uploadCsv" :disabled="!importFile || loading || invalidRowsCount > 0 || previewData.length === 0">
                {{ loading ? 'Importando...' : 'Iniciar Importación Masiva' }}
             </button>
        </template>
    </BaseModal>

     <!-- Image Cropper Modal -->
     <CropModal 
       :show="showCropper" 
       :imageSrc="tempImage" 
       @save="onCropSaved" 
       @cancel="showCropper = false" 
     />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import api from '../services/api';
import { useAuthStore } from '../stores/auth';
import PageHeader from '../components/layout/PageHeader.vue';
import LoadingSpinner from '../components/ui/LoadingSpinner.vue';
import BaseModal from '../components/modals/BaseModal.vue';
import EmptyState from '../components/ui/EmptyState.vue';
import TableActionButton from '../components/ui/TableActionButton.vue';
import BaseTable from '../components/tables/BaseTable.vue';
import FilterBar from '../components/forms/FilterBar.vue';
import BaseCard from '../components/ui/BaseCard.vue';
import BaseButton from '../components/ui/BaseButton.vue';
import { useNotificationStore } from '../stores/notifications';
import { useConfirmStore } from '../stores/confirm';
import TablePagination from '../components/ui/TablePagination.vue';
import CropModal from '../components/modals/CropModal.vue';

const authStore = useAuthStore();
const notificationStore = useNotificationStore();
const confirmStore = useConfirmStore();

const users = ref([]);
const roles = ref([]);
const loading = ref(false);
const error = ref('');
const showForm = ref(false);
const isEdit = ref(false);

// Import State
const showImportForm = ref(false);
const importFile = ref(null);
const importResult = ref(null);
const fileInput = ref(null);
const previewData = ref([]);

// Cropper State
const showCropper = ref(false);
const tempImage = ref('');

// Search and Filter State
const search = ref('');
const filterRole = ref('');
const filterStatus = ref('');
const sortBy = ref('nombre');
const sortDir = ref('asc');

// Pagination State
const currentPage = ref(1);
const itemsPerPage = ref(10);

const hasActiveFilters = computed(() => {
    return search.value !== '' || filterRole.value !== '' || filterStatus.value !== '';
});

const resetFilters = () => {
    search.value = '';
    filterRole.value = '';
    filterStatus.value = '';
};

// Reset to first page when filters change
watch([search, filterRole, filterStatus], () => {
  currentPage.value = 1;
});

const processedUsers = computed(() => {
    let result = [...users.value];

    // Search filter
    if (search.value) {
        const query = search.value.toLowerCase();
        result = result.filter(u => 
            (u.nombre && u.nombre.toLowerCase().includes(query)) || 
            (u.apellido && u.apellido.toLowerCase().includes(query)) || 
            (u.documentoIdentidad && u.documentoIdentidad.toString().includes(query))
        );
    }

    // Role filter
    if (filterRole.value) {
        if (filterRole.value === 'ALL_ASSIGNED') {
            result = result.filter(u => {
                const rolesList = getUserRolesList(u);
                return rolesList.length >= roles.value.length && roles.value.length > 0;
            });
        } else {
            result = result.filter(u => {
                const rolesList = getUserRolesList(u);
                const cleanFilterRole = filterRole.value.replace('ROLE_', '');
                return rolesList.includes(cleanFilterRole);
            });
        }
    }

    // Status filter
    if (filterStatus.value) {
        result = result.filter(u => u.estado === filterStatus.value);
    }

    // Sorting
    result.sort((a, b) => {
        let valA, valB;
        if (sortBy.value === 'nombre') {
            valA = ((a.nombre || '') + ' ' + (a.apellido || '')).toLowerCase();
            valB = ((b.nombre || '') + ' ' + (b.apellido || '')).toLowerCase();
        } else if (sortBy.value === 'email') {
            valA = (a.email || '').toLowerCase();
            valB = (b.email || '').toLowerCase();
        } else {
            valA = a[sortBy.value];
            valB = b[sortBy.value];
        }

        if (valA === undefined || valA === null) valA = '';
        if (valB === undefined || valB === null) valB = '';

        if (valA < valB) return sortDir.value === 'asc' ? -1 : 1;
        if (valA > valB) return sortDir.value === 'asc' ? 1 : -1;
        return 0;
    });

    return result;
});

const totalPages = computed(() => Math.ceil(processedUsers.value.length / itemsPerPage.value));

const paginatedUsers = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value;
    return processedUsers.value.slice(start, start + itemsPerPage.value);
});

const toggleSort = (field) => {
    if (sortBy.value === field) {
        sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc';
    } else {
        sortBy.value = field;
        sortDir.value = 'asc';
    }
};

const validRowsCount = computed(() => previewData.value.filter(r => r.errors.length === 0).length);
const invalidRowsCount = computed(() => previewData.value.filter(r => r.errors.length > 0).length);
const hasErrors = computed(() => invalidRowsCount.value > 0);

const form = ref({
  documentoIdentidad: '',
  nombre: '',
  apellido: '', 
  email: '',
  password: '',
  selectedRoles: [],
  estado: 'HABILITADO'
});

const fetchData = async () => {
  loading.value = true;
  error.value = ''; // Reset error
  
  try {
    // Fetch users first as it's the main content
    try {
      const usersRes = await api.get('/participantes');
      users.value = usersRes.data;
      currentPage.value = 1; // Reset to first page
    } catch (userErr) {
       console.error("Error fetching users:", userErr);
       error.value = "Error cargando usuarios: " + (userErr.response?.data?.message || userErr.message);
    }

    // Attempt to fetch roles independently
    try {
      const rolesRes = await api.get('/roles');
      roles.value = rolesRes.data;
    } catch (roleErr) {
      console.error("Error fetching roles:", roleErr);
      if (!error.value) { 
         // Optional warning
      }
    }

  } catch (err) {
    // Catch any unexpected errors
    error.value = 'Error inesperado: ' + err.message;
  } finally {
    loading.value = false;
  }
};

const fetchUsers = async () => {
   try {
    const res = await api.get('/participantes');
    users.value = res.data;
   } catch(err) { console.error(err); }
};

const resetForm = () => {
  form.value = {
    documentoIdentidad: '',
    nombre: '',
    apellido: '',
    email: '',
    password: '',
    selectedRoles: [],
    estado: 'HABILITADO',
    fotoPerfil: ''
  };
};

const openCreateModal = () => {
  isEdit.value = false;
  resetForm();
  showForm.value = true;
};

const openImportModal = () => {
    showImportForm.value = true;
    importFile.value = null;
    importResult.value = null;
    previewData.value = [];
};

// Multi-role selection helpers
const allRolesSelected = computed(() => {
    return roles.value.length > 0 && form.value.selectedRoles.length === roles.value.length;
});

const selectAllRoles = () => {
    if (allRolesSelected.value) {
        form.value.selectedRoles = [];
    } else {
        form.value.selectedRoles = roles.value.map(r => r.idRol);
    }
};

const editUser = (user) => {
  // When editing, load existing roles from rolesIds (multiple) or rolId (single)
  let selectedRoles = [];
  if (user.rolesIds && Array.isArray(user.rolesIds) && user.rolesIds.length > 0) {
      selectedRoles = [...user.rolesIds];
  } else if (user.rolesNombres && Array.isArray(user.rolesNombres) && user.rolesNombres.length > 0) {
      // Fallback: map names to IDs if IDs are missing from DTO
      selectedRoles = user.rolesNombres.map(name => {
         const cleanName = name.replace('ROLE_', '');
         const role = roles.value.find(r => r.nombreRol === cleanName);
         return role ? role.idRol : null;
      }).filter(id => id !== null);
  } else if (user.rolId) {
      selectedRoles = [user.rolId];
  }
  
  form.value = { 
      ...user, 
      password: '', 
      selectedRoles: selectedRoles,
      fotoPerfil: user.fotoPerfil || ''
  };
  isEdit.value = true;
  showForm.value = true;
};

const triggerFileInput = () => {
  fileInput.value.click();
};

const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    if (file.size > 2 * 1024 * 1024) { // 2MB limit
      notificationStore.showError('La imagen es demasiado grande. Máximo 2MB.');
      return;
    }
    const reader = new FileReader();
    reader.onload = (e) => {
      tempImage.value = e.target.result;
      showCropper.value = true;
    };
    reader.readAsDataURL(file);
    // Clear input to allow same file selection
    event.target.value = '';
  }
};

const onCropSaved = (croppedImage) => {
  form.value.fotoPerfil = croppedImage;
  showCropper.value = false;
};

const deleteUser = async (id) => {
  const confirmed = await confirmStore.ask({
      title: 'Eliminar Usuario',
      message: '¿Estás seguro de eliminar este usuario? Esta acción no se puede deshacer.',
      confirmText: 'Eliminar',
      type: 'danger'
  });

  if (!confirmed) return;

  try {
    await api.delete(`/participantes/${id}`);
    notificationStore.showSuccess('Usuario eliminado correctamente');
    fetchUsers();
  } catch (err) {
    notificationStore.showError('Error eliminando: ' + (err.response?.data?.message || err.message));
  }
};

const submitForm = async () => {
  // Validate at least one role selected
  if (form.value.selectedRoles.length === 0) {
      notificationStore.showWarning('Por favor, selecciona al menos un rol.');
      return;
  }
  
  try {
    if (isEdit.value) {
      // For update, send the list of role IDs
      // Clean payload to prevent 400 Bad Request
      const updateData = {
          documentoIdentidad: form.value.documentoIdentidad,
          nombre: form.value.nombre,
          apellido: form.value.apellido,
          email: form.value.email,
          rolesIds: form.value.selectedRoles.map(Number),
          estado: form.value.estado,
          fotoPerfil: form.value.fotoPerfil
      };
      
      if (form.value.password && form.value.password.trim() !== '') {
         updateData.password = form.value.password;
      }
      
      console.log('Sending Update Payload:', updateData);
      await api.put(`/participantes/${form.value.documentoIdentidad}`, updateData);
    } else {
      // For registration, map selected role IDs to role names
      const selectedRoleNames = form.value.selectedRoles.map(roleId => {
          const role = roles.value.find(r => r.idRol === roleId);
          return role ? role.nombreRol : 'MONITOR';
      });
      
      const registerData = {
          documentoIdentidad: form.value.documentoIdentidad,
          nombre: form.value.nombre,
          apellido: form.value.apellido,
          email: form.value.email,
          password: form.value.password,
          roles: selectedRoleNames,
          fotoPerfil: form.value.fotoPerfil
      };
      
      await api.post('/auth/signup', registerData);
      notificationStore.showSuccess('Usuario creado correctamente');
    }
    showForm.value = false;
    fetchUsers();
  } catch (err) {
    notificationStore.showError('Error guardando: ' + (err.response?.data?.message || err.message));
  }
};

const handleFileSelect = (e) => {
    if (e.target.files.length > 0) {
        importFile.value = e.target.files[0];
        processFilePreview(importFile.value);
    }
};

const handleDrop = (e) => {
    if (e.dataTransfer.files.length > 0) {
        importFile.value = e.dataTransfer.files[0];
        processFilePreview(importFile.value);
    }
};

const processFilePreview = (file) => {
    const reader = new FileReader();
    reader.onload = (e) => {
        const text = e.target.result;
        const lines = text.split(/\r?\n/);
        const results = [];
        
        if (lines.length < 1) return;
        
        // Detect delimiter from first line
        const firstLine = lines[0];
        const delimiter = firstLine.includes(';') ? ';' : ',';
        
        // Skip header
        for (let i = 1; i < lines.length; i++) {
            if (!lines[i].trim()) continue;
            
            const cols = lines[i].split(delimiter).map(c => c.trim());
            const rowErrors = [];
            
            // Expected: Doc, Nombre, Apellido, Email, Password, Rol
            const docValue = cols[0];
            const nombreValue = cols[1];
            const apellidoValue = cols[2];
            const emailValue = cols[3];
            const rolValue = cols[5] || 'USER';

            // Validation
            if (!docValue || isNaN(docValue)) rowErrors.push('Doc inválido');
            if (!nombreValue) rowErrors.push('Falta nombre');
            if (!emailValue || !emailValue.includes('@')) rowErrors.push('Email inválido');
            
            // Check duplicates in users list
            if (users.value.some(u => u.documentoIdentidad.toString() === docValue)) {
                rowErrors.push('Documento ya existe');
            }
            if (users.value.some(u => u.email.toLowerCase() === emailValue?.toLowerCase())) {
                rowErrors.push('Email ya existe');
            }

            results.push({
                doc: docValue,
                nombre: nombreValue,
                apellido: apellidoValue,
                email: emailValue,
                rol: rolValue,
                errors: rowErrors
            });
        }
        previewData.value = results;
    };
    reader.readAsText(file);
};

const downloadTemplate = () => {
    // We use semicolon to ensure Excel opens it in separate columns in most Spanish-speaking locales,
    // but the system now supports both , and ;
    const csvContent = "Doc;Nombre;Apellido;Email;Password;Rol\n" + 
                       "10123456789;Juan;Perez;juan.perez@ejemplo.com;Pass1234;MONITOR\n" +
                       "10223344556;Maria;Gomez;maria.gomez@ejemplo.com;Pass1234;ADMIN\n" +
                       "10334455667;Carlos;Rodriguez;carlos.rod@ejemplo.com;Pass1234;OPERADOR\n" +
                       "10445566778;Ana;Martínez;ana.mtz@ejemplo.com;Pass1234;INVITADO";
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement("a");
    const url = URL.createObjectURL(blob);
    link.setAttribute("href", url);
    link.setAttribute("download", "plantilla_usuarios.csv");
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
};

const uploadCsv = async () => {
    if (!importFile.value) return;
    loading.value = true;
    const formData = new FormData();
    formData.append('file', importFile.value);
    
    try {
        const res = await api.post('/participantes/import', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
        importResult.value = res.data;
        notificationStore.showSuccess('Importación completada');
        fetchUsers();
    } catch(err) {
        notificationStore.showError(err.response?.data || err.message);
    } finally {
        loading.value = false;
    }
};

// Helper: Get user's initials
const getInitials = (name) => {
  if (!name) return '?';
  return name.charAt(0).toUpperCase();
};

// Helper: Get user's roles as a list of clean names
const getUserRolesList = (user) => {
    const cleanRole = (r) => {
        if (!r) return r;
        return r.replace('ROLE_', '');
    };

    // Priority 1: Use rolesNombres array from backend (multiple roles)
    if (user.rolesNombres && Array.isArray(user.rolesNombres) && user.rolesNombres.length > 0) {
        return user.rolesNombres.map(cleanRole);
    }
    // Priority 2: Single rolNombre for backward compatibility
    if (user.rolNombre) {
        return [cleanRole(user.rolNombre)];
    }
    // Priority 3: roles array (from auth response)
    if (user.roles && Array.isArray(user.roles)) {
        return user.roles
            .filter(r => !r.includes(':'))
            .map(cleanRole);
    }
    return [];
};

// Helper: Get badge class based on role name
const getRoleBadgeClass = (roleName) => {
    const roleClasses = {
        'ADMIN': 'role-admin',
        'OPERADOR': 'role-operador',
        'MONITOR': 'role-monitor',
        'INVITADO': 'role-invitado'
    };
    return roleClasses[roleName.toUpperCase()] || 'role-badge';
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped>
/* Page specific styles only */
.roles-display {
    display: flex;
    flex-wrap: wrap;
    gap: 0.35rem;
}

.role-badge {
    background: #e2e8f0;
    color: #2d3748;
    padding: 0.2rem 0.6rem;
    border-radius: 50px;
    font-size: 0.75rem;
    font-weight: 600;
    white-space: nowrap;
}

.role-badge.role-admin {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
}

.role-badge.role-operador {
    background: #3182ce;
    color: white;
}

.role-badge.role-monitor {
    background: #38a169;
    color: white;
}

.role-badge.role-invitado {
    background: #718096;
    color: white;
}

.status-badge {
    padding: 0.25rem 0.75rem;
    border-radius: 50px;
    font-size: 0.7rem;
    font-weight: 700;
    text-transform: uppercase;
}

.status-badge.active {
    background: #c6f6d5;
    color: #22543d;
}

.status-badge.inactive {
    background: #fed7d7;
    color: #822727;
}

.role-badge.role-organizador {
    background: #d69e2e;
    color: white;
}

.role-badge.role-user {
    background: #4a5568;
    color: white;
}

.role-badge.all-roles {
    background: linear-gradient(135deg, #667eea, #48bb78);
    color: white;
}

.role-badge.no-role {
    background: #fed7d7;
    color: #c53030;
}

.form-grid {
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
}

.form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.25rem;
}

.full-width {
    width: 100%;
}

.action-buttons {
    display: flex;
    justify-content: flex-end;
    gap: 0.5rem;
    align-items: center;
}

.user-edit-header {
    display: flex;
    align-items: center;
    gap: 1.25rem;
    padding: 1.25rem;
    background: #f8fafc;
    border-radius: 12px;
    margin-bottom: 2rem;
    border: 1px solid #e2e8f0;
}

.user-avatar-preview {
    width: 64px;
    height: 64px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.user-avatar-preview svg {
    width: 32px;
    height: 32px;
}

.user-info-text h3 {
    margin: 0;
    font-size: 1.1rem;
    color: #1e293b;
    font-weight: 700;
}

.user-info-text p {
    margin: 4px 0 0;
    font-size: 0.9rem;
    color: #64748b;
}

.user-info-wrapper {
    display: flex;
    align-items: center;
    gap: 12px;
}

.user-avatar-small {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: linear-gradient(135deg, #667eea, #764ba2);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 0.75rem;
    font-weight: 700;
    overflow: hidden;
    flex-shrink: 0;
}

.user-avatar-small img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.user-avatar-preview {
    width: 80px;
    height: 80px;
    background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #94a3b8;
    position: relative;
    overflow: hidden;
    border: 2px solid #e2e8f0;
}

.preview-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.edit-overlay {
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.4);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 0.4rem;
    color: white;
    opacity: 0;
    transition: all 0.3s;
    backdrop-filter: blur(2px);
}

.edit-overlay span {
    font-size: 0.65rem;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.05em;
}

.user-avatar-preview:hover .edit-overlay {
    opacity: 1;
}

.form-group label {
  display: block;
  font-size: 0.875rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #4a5568;
}

.dropzone {
    border: 2px dashed #cbd5e0;
    border-radius: 8px;
    padding: 2rem;
    text-align: center;
    cursor: pointer;
    background: #f7fafc;
    transition: all 0.2s;
}
.dropzone:hover {
    border-color: #667eea;
    background: #ebf8ff;
}
.dropzone p {
    color: #718096;
    margin: 0;
}
.import-report {
    margin-top: 1.5rem;
    padding: 1rem;
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
}
.report-summary {
    display: flex;
    gap: 1.5rem;
    margin-bottom: 1rem;
    justify-content: center;
}
.summary-item {
    display: flex;
    flex-direction: column;
    align-items: center;
}
.summary-item .label {
    font-size: 0.75rem;
    text-transform: uppercase;
    color: #718096;
    font-weight: 600;
}
.summary-item .value {
    font-size: 1.5rem;
    font-weight: 700;
}
.summary-item.success .value { color: #38a169; }
.summary-item.failed .value { color: #e53e3e; }

.error-container {
    background: #fff5f5;
    border: 1px solid #fed7d7;
    border-radius: 6px;
    padding: 0.75rem;
    max-height: 150px;
    overflow-y: auto;
}
.error-container h4 {
    margin: 0 0 0.5rem;
    color: #c53030;
    font-size: 0.9rem;
}
.error-list {
    margin: 0;
    padding-left: 1.25rem;
    color: #c53030;
    font-size: 0.85rem;
}
.error-list li {
    margin-bottom: 0.25rem;
}

/* Roles Selector */
.roles-selector {
    display: flex;
    flex-wrap: wrap;
    gap: 0.75rem;
    padding: 1rem;
    background: #f7fafc;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    max-height: 200px;
    overflow-y: auto;
}

.role-checkbox {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    background: white;
    padding: 0.5rem 0.75rem;
    border-radius: 6px;
    border: 1px solid #e2e8f0;
    transition: all 0.2s;
}

.role-checkbox:hover {
    border-color: #667eea;
    background: #ebf4ff;
}

.role-checkbox input[type="checkbox"] {
    width: 16px;
    height: 16px;
    accent-color: #667eea;
}

.role-checkbox label {
    margin: 0;
    font-size: 0.85rem;
    font-weight: 500;
    color: #2d3748;
    cursor: pointer;
}

.roles-actions {
    margin-top: 0.75rem;
    display: flex;
    justify-content: flex-end;
}

.btn-sm {
    padding: 0.35rem 0.75rem;
    font-size: 0.8rem;
    background: #edf2f7;
    color: #4a5568;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-weight: 600;
    transition: all 0.2s;
}
.btn-sm:hover {
    background: #e2e8f0;
    color: #2d3748;
}

/* Import Styles */
.import-instructions {
    margin-bottom: 1rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.import-instructions p { margin: 0; font-size: 0.85rem; color: #4a5568; }
.link-btn {
    background: transparent;
    color: var(--primary-accent);
    text-decoration: underline;
    font-size: 0.85rem;
    font-weight: 600;
    cursor: pointer;
    box-shadow: none;
}

.import-preview-container {
    margin-top: 1.5rem;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    overflow: hidden;
}

.preview-header {
    background: #f8fafc;
    padding: 0.75rem 1rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #e2e8f0;
}

.preview-header h4 { margin: 0; font-size: 0.9rem; color: #1e293b; }
.preview-stats { display: flex; gap: 1rem; }
.stat { font-size: 0.75rem; font-weight: 700; padding: 2px 8px; border-radius: 4px; }
.stat.valid { background: #f0fdf4; color: #16a34a; }
.stat.invalid { background: #fef2f2; color: #dc2626; }

.preview-table { max-height: 250px; }
.styled-table.mini { font-size: 0.8rem; }
.styled-table.mini th { padding: 0.5rem 1rem; }
.styled-table.mini td { padding: 0.4rem 1rem; }

.row-error { background-color: #fff5f5 !important; }

.badge-dot { width: 10px; height: 10px; border-radius: 50%; display: inline-block; }
.badge-dot.success { background: #22c55e; }
.badge-dot.error { background: #ef4444; animation: pulse 2s infinite; }

.validation-warning {
    background: #fffbeb;
    color: #d97706;
    padding: 0.75rem;
    display: flex;
    align-items: center;
    gap: 0.75rem;
    font-size: 0.8rem;
    font-weight: 500;
    border-top: 1px solid #fef3c7;
}

@keyframes pulse {
    0% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.7); }
    70% { transform: scale(1); box-shadow: 0 0 0 6px rgba(239, 68, 68, 0); }
    100% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(239, 68, 68, 0); }
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
    height: 16px; /* Fixed height for label area */
    display: flex;
    align-items: center;
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
    width: 100%;
}

.search-wrapper:focus-within, .custom-select-wrapper:focus-within {
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-wrapper svg, .custom-select-wrapper .select-icon {
    color: #94a3b8;
    flex-shrink: 0;
    display: flex;
    align-items: center;
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
    line-height: 42px;
}

.select-icon {
    margin-left: 8px;
    pointer-events: none;
}

.filter-actions {
    display: flex;
    align-items: center;
    height: 42px; /* Match input/select height */
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
    box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.btn-clear:hover {
    background: #fff5f5;
    color: #e53e3e;
    border-color: #fecaca;
    transform: translateY(-2px);
    box-shadow: 0 4px 6px -1px rgba(229, 62, 62, 0.1);
}

.btn-clear svg {
    flex-shrink: 0;
    /* Ensure no unwanted spacing */
    margin: 0;
}

.animate-slide-in-right {
    animation: slideInRight 0.4s ease-out forwards;
}

@keyframes slideInRight {
    from {
        opacity: 0;
        transform: translateX(20px);
    }
    to {
        opacity: 1;
        transform: translateX(0);
    }
}

.sortable {
    cursor: pointer;
    user-select: none;
    transition: background 0.2s;
}

.sortable:hover {
    background: #f7fafc;
}

.sort-indicator {
    display: inline-block;
    margin-left: 4px;
    font-size: 0.8rem;
    color: #667eea;
}

@media (max-width: 768px) {
    .filters-container {
        flex-direction: column;
        align-items: stretch;
    }
    .filter-item {
        width: 100%;
    }
    .filter-item select {
        width: 100%;
    }
}
</style>
