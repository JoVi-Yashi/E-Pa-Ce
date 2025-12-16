<template>
  <div class="container animate-fade-in">
    <div class="header-actions">
      <div class="glass-header">
        <h1>Gestión de Usuarios</h1>
      </div>
      <div class="actions">
        <router-link to="/" class="back-link">Volver</router-link>
        <button class="btn-secondary" @click="openImportModal" style="margin-right: 0.5rem;">
           <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:8px"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="17 8 12 3 7 8"></polyline><line x1="12" y1="3" x2="12" y2="15"></line></svg>
           Importar CSV
        </button>
        <button class="btn-primary" @click="openCreateModal">
           <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:8px"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="8.5" cy="7" r="4"></circle><line x1="20" y1="8" x2="20" y2="14"></line><line x1="23" y1="11" x2="17" y2="11"></line></svg>
           Nuevo Usuario
        </button>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div> Cargando usuarios...
    </div>
    <div v-if="error" class="error-msg">{{ error }}</div>

    <div class="card" v-if="!loading">
      <div class="table-responsive">
        <table class="styled-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Documento</th>
              <th>Nombre</th>
              <th>Email</th>
              <th>Rol</th>
              <th style="text-align: right">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.idParticipante">
              <td>#{{ user.idParticipante }}</td>
              <td>{{ user.docIdentidad }}</td>
              <td><strong>{{ user.nombre }}</strong></td>
              <td>{{ user.email }}</td>
              <td>
                <span class="role-badge">{{ user.rol?.nombreRol || 'N/A' }}</span>
              </td>
              <td style="text-align: right">
                <button class="icon-btn edit" @click="editUser(user)" title="Editar">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                </button>
                <button class="icon-btn delete" @click="deleteUser(user.idParticipante)" title="Eliminar">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
       <div v-if="users.length === 0" class="empty-state">
        No hay usuarios registrados.
      </div>
    </div>

    <!-- Modal Overlay -->
    <div v-if="showForm" class="modal-overlay" @click.self="showForm = false">
      <div class="modal-content animate-slide-up">
        <div class="modal-header">
           <h3>{{ isEdit ? 'Editar Usuario' : 'Nuevo Usuario' }}</h3>
           <button class="close-btn" @click="showForm = false">&times;</button>
        </div>
        <form @submit.prevent="submitForm" class="modal-body">
          <div class="form-grid">
             <div class="form-group">
                <label>Documento de Identidad</label>
                <input v-model="form.docIdentidad" type="number" required placeholder="Cédula / Documento" :disabled="isEdit" />
             </div>
             <div class="form-group">
                <label>Nombre</label>
                <input v-model="form.nombre" required placeholder="Nombre" />
             </div>
             <div class="form-group">
                <label>Apellido</label>
                <input v-model="form.apellido" required placeholder="Apellido" />
             </div>
             <div class="form-group full-width">
                <label>Email</label>
                <input v-model="form.email" type="email" required placeholder="usuario@ejemplo.com" />
             </div>
             <div class="form-group" v-if="!isEdit">
                <label>Contraseña</label>
                <input v-model="form.password" type="password" required minlength="6" placeholder="******" />
             </div>
              <div class="form-group">
                <label>Rol</label>
                <select v-model="form.rolId" required>
                  <option v-for="role in roles" :key="role.idRol" :value="role.idRol">
                    {{ role.nombreRol }}
                  </option>
                </select>
             </div>
          </div>
          
          <div class="modal-footer">
            <button type="button" class="btn-secondary" @click="showForm = false">Cancelar</button>
            <button type="submit" class="btn-primary">
               {{ isEdit ? 'Actualizar' : 'Crear Usuario' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Import Modal -->
    <div v-if="showImportForm" class="modal-overlay" @click.self="showImportForm = false">
      <div class="modal-content animate-slide-up">
        <div class="modal-header">
           <h3>Importar Usuarios (CSV)</h3>
           <button class="close-btn" @click="showImportForm = false">&times;</button>
        </div>
        <div class="modal-body">
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
        </div>
        <div class="modal-footer">
             <button class="btn-secondary" @click="showImportForm = false">Cerrar</button>
             <button class="btn-primary" @click="uploadCsv" :disabled="!importFile || loading">
                {{ loading ? 'Procesando...' : 'Importar' }}
             </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

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

const form = ref({
  idParticipante: null,
  docIdentidad: '',
  nombre: '',
  apellido: '', 
  email: '',
  password: '',
  rolId: null
});

const fetchData = async () => {
  loading.value = true;
  try {
    const [usersRes, rolesRes] = await Promise.all([
      api.get('/participantes'),
      api.get('/roles')
    ]);
    users.value = usersRes.data;
    roles.value = rolesRes.data;
  } catch (err) {
    error.value = 'Error cargando datos: ' + err.message;
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
    idParticipante: null,
    docIdentidad: '',
    nombre: '',
    apellido: '',
    email: '',
    password: '',
    rolId: null
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
};

const editUser = (user) => {
  form.value = { 
      ...user, 
      password: '', 
      rolId: user.rol?.idRol || user.rolId 
  };
  isEdit.value = true;
  showForm.value = true;
};

const deleteUser = async (id) => {
  if (!confirm('¿Estás seguro de eliminar este usuario?')) return;
  try {
    await api.delete(`/participantes/${id}`);
    fetchUsers();
  } catch (err) {
    alert('Error eliminando: ' + err.message);
  }
};

const submitForm = async () => {
  try {
    if (isEdit.value) {
      await api.put(`/participantes/${form.value.idParticipante}`, form.value);
    } else {
      await api.post('/auth/register', form.value);
    }
    showForm.value = false;
    fetchUsers();
  } catch (err) {
    alert('Error guardando: ' + (err.response?.data?.message || err.message));
  }
};

const handleFileSelect = (e) => {
    if (e.target.files.length > 0) {
        importFile.value = e.target.files[0];
    }
};

const handleDrop = (e) => {
    if (e.dataTransfer.files.length > 0) {
        importFile.value = e.dataTransfer.files[0];
    }
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
        fetchUsers();
    } catch(err) {
        alert(err.response?.data || err.message);
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
  fetchData();
});
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

.role-badge {
    background: #e2e8f0;
    color: #2d3748;
    padding: 0.25rem 0.75rem;
    border-radius: 50px;
    font-size: 0.8rem;
    font-weight: 600;
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
  max-width: 600px;
  box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1);
  max-height: 90vh;
  overflow-y: auto;
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

.form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
}

.full-width {
    grid-column: 1 / -1;
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
</style>
