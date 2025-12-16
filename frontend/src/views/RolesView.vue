<template>
  <div class="container animate-fade-in">
    <div class="header-actions">
      <div class="glass-header">
         <h1>Gestión de Roles</h1>
      </div>
      <div class="actions">
        <router-link to="/" class="back-link">Volver</router-link>
        <button class="btn-primary" @click="resetForm(); showForm = true">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:8px"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path></svg>
          Crear Rol
        </button>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div> Cargando...
    </div>
    <div v-if="error" class="error-msg">{{ error }}</div>

    <div class="card" v-if="!loading">
      <div class="table-responsive">
        <table class="styled-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nombre Rol</th>
              <th style="text-align: right">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="r in roles" :key="r.idRol">
              <td>#{{ r.idRol }}</td>
              <td><strong>{{ r.nombreRol }}</strong></td>
              <td style="text-align: right">
                <button class="icon-btn edit" @click="editRol(r)" title="Editar">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                </button>
                <button class="icon-btn delete" @click="deleteRol(r.idRol)" title="Eliminar">
                   <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="roles.length === 0" class="empty-state">
        No hay roles registrados.
      </div>
    </div>

    <!-- Form Modal -->
    <div v-if="showForm" class="modal-overlay" @click.self="showForm = false">
      <div class="modal-content animate-slide-up">
        <div class="modal-header">
           <h3>{{ form.idRol ? 'Editar' : 'Crear' }} Rol</h3>
           <button class="close-btn" @click="showForm = false">&times;</button>
        </div>
        <form @submit.prevent="submitForm" class="modal-body">
           <div class="form-group">
               <label>Nombre del Rol</label>
               <input v-model="form.nombreRol" placeholder="Ej. ADMIN, STUDENT" required />
           </div>

           <div class="form-group permissions-group">
               <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                   <label style="margin-bottom: 0;">Permisos</label>
                   <button type="button" class="btn-sm" @click="toggleAllPermissions">
                      {{ allSelected ? 'Deseleccionar Todos' : 'Seleccionar Todos' }}
                   </button>
               </div>
               <div class="table-responsive">
                 <table class="perm-table">
                    <thead>
                      <tr>
                        <th>Módulo</th>
                        <th v-for="action in actions" :key="action">{{ action }}</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="mod in modules" :key="mod">
                        <td>{{ mod }}</td>
                        <td v-for="action in actions" :key="action" style="text-align: center;">
                           <input type="checkbox" :value="`${mod}:${action}`" v-model="form.permisos">
                        </td>
                      </tr>
                    </tbody>
                 </table>
               </div>
           </div>
           <div class="modal-footer">
               <button type="button" class="btn-secondary" @click="showForm = false">Cancelar</button>
               <button type="submit" class="btn-primary">Guardar</button>
           </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import api from '../services/api';

const roles = ref([]);
const loading = ref(false);
const error = ref('');
const showForm = ref(false);
const form = ref({ idRol: null, nombreRol: '', permisos: [] });

const modules = ['EVENTOS', 'PARTICIPANTES', 'CHECKINS', 'ROLES', 'CONFIGURACION'];
const actions = ['VER', 'CREAR', 'EDITAR', 'ELIMINAR'];

const allPossiblePermissions = computed(() => {
    const perms = [];
    modules.forEach(m => {
        actions.forEach(a => perms.push(`${m}:${a}`));
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

const fetchRoles = async () => {
  loading.value = true;
  try {
    const res = await api.get('/roles');
    roles.value = res.data.map(r => ({
        ...r,
        permisos: r.permisos || []
    }));
  } catch (err) {
    error.value = 'Error al cargar roles: ' + err.message;
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  form.value = { idRol: null, nombreRol: '', permisos: [] };
};

const editRol = (r) => {
  form.value = { ...r, permisos: [...(r.permisos || [])] };
  showForm.value = true;
};

const deleteRol = async (id) => {
  if (!confirm('Eliminar seguro?')) return;
  try {
    await api.delete(`/roles/${id}`);
    fetchRoles();
  } catch (err) {
    alert(err.message);
  }
};

const submitForm = async () => {
  try {
    if (form.value.idRol) {
      await api.put(`/roles/${form.value.idRol}`, form.value);
    } else {
      await api.post('/roles', form.value);
    }
    showForm.value = false;
    fetchRoles();
  } catch (err) {
    alert(err.message);
  }
};

onMounted(fetchRoles);
</script>

<style scoped>
.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.back-link {
  color: var(--text-secondary);
  font-weight: 500;
}

.back-link:hover {
  color: var(--text-primary);
}

/* Icon Buttons */
.icon-btn {
  background: transparent;
  color: #718096;
  padding: 0.5rem;
  margin-left: 0.5rem;
  box-shadow: none;
}

.icon-btn:hover {
  background: #f7fafc;
  transform: translateY(-2px);
}
.icon-btn.edit:hover { color: #667eea; }
.icon-btn.delete:hover { color: #e53e3e; }

/* Modal Styling */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1);
}

.modal-header {
  padding: 1.5rem;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-body {
  padding: 1.5rem;
}

.form-group label {
  display: block;
  font-size: 0.875rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #4a5568;
}

.modal-footer {
  padding: 1.5rem;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.btn-secondary {
  background: white;
  color: #4a5568;
  border: 1px solid #cbd5e0;
}

.close-btn {
  background: transparent;
  color: #a0aec0;
  font-size: 1.5rem;
  padding: 0;
  box-shadow: none;
}

.close-btn:hover {
  color: #4a5568;
}

.btn-sm {
    padding: 0.25rem 0.5rem;
    font-size: 0.75rem;
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

.permissions-group .table-responsive {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
}

.perm-table {
  width: 100%;
  border-collapse: collapse;
}

.perm-table th, .perm-table td {
  padding: 0.75rem;
  border-bottom: 1px solid #e2e8f0;
  font-size: 0.85rem;
}

.perm-table th {
  background: #f7fafc;
  color: #4a5568;
  font-weight: 600;
  position: sticky;
  top: 0;
  z-index: 10;
}
</style>
