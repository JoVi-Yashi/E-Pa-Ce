<template>
  <div class="config-component">
    <div class="component-header">
       <h2>Modalidades</h2>
       <button class="btn-primary" @click="resetForm(); showForm = true">
          Nueva Modalidad
       </button>
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
              <th>Nombre Modalidad</th>
              <th style="text-align: right">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="m in modalidades" :key="m.idModalidadEvento">
              <td>#{{ m.idModalidadEvento }}</td>
              <td><strong>{{ m.nombreModalidadEvento }}</strong></td>
              <td style="text-align: right">
                <button class="icon-btn edit" @click="editModalidad(m)" title="Editar">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                </button>
                <button class="icon-btn delete" @click="deleteModalidad(m.idModalidadEvento)" title="Eliminar">
                   <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="modalidades.length === 0" class="empty-state">
        No hay modalidades registradas.
      </div>
    </div>

    <!-- Form Modal -->
    <div v-if="showForm" class="modal-overlay" @click.self="showForm = false">
      <div class="modal-content animate-slide-up">
        <div class="modal-header">
           <h3>{{ form.idModalidadEvento ? 'Editar' : 'Crear' }} Modalidad</h3>
           <button class="close-btn" @click="showForm = false">&times;</button>
        </div>
        <form @submit.prevent="submitForm" class="modal-body">
           <div class="form-group">
               <label>Nombre Modalidad</label>
               <input v-model="form.nombreModalidadEvento" placeholder="Ej. Presencial, Virtual" required />
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
import { ref, onMounted } from 'vue';
import api from '../../services/api';

const modalidades = ref([]);
const loading = ref(false);
const error = ref('');
const showForm = ref(false);
const form = ref({ idModalidadEvento: null, nombreModalidadEvento: '' });

const fetchModalidades = async () => {
  loading.value = true;
  try {
    const res = await api.get('/modalidades');
    modalidades.value = res.data;
  } catch (err) {
    error.value = 'Error: ' + err.message;
  } finally {
    loading.value = false;
  }
};

const resetForm = () => form.value = { idModalidadEvento: null, nombreModalidadEvento: '' };

const editModalidad = (m) => {
    form.value = { ...m };
    showForm.value = true;
};

const deleteModalidad = async (id) => {
    if(!confirm('¿Eliminar modalidad?')) return;
    try {
        await api.delete(`/modalidades/${id}`);
        fetchModalidades();
    } catch(err) { alert(err.message); }
};

const submitForm = async () => {
    try {
        if (form.value.idModalidadEvento) {
            await api.put(`/modalidades/${form.value.idModalidadEvento}`, form.value);
        } else {
            await api.post('/modalidades', form.value);
        }
        showForm.value = false;
        fetchModalidades();
    } catch (err) { alert(err.message); }
};

onMounted(fetchModalidades);
</script>

<style scoped>
.component-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
}

/* Reusing styles from global implicitly, but adding necessary scoped styles */
.icon-btn {
  background: transparent;
  color: #718096;
  padding: 0.5rem;
  margin-left: 0.5rem;
  box-shadow: none;
}
.icon-btn:hover { background: #f7fafc; transform: translateY(-2px); }
.icon-btn.edit:hover { color: #667eea; }
.icon-btn.delete:hover { color: #e53e3e; }

.modal-overlay {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5); display: flex;
  justify-content: center; align-items: center; z-index: 1000;
  backdrop-filter: blur(4px);
}
.modal-content {
  background: white; border-radius: 16px; width: 90%; max-width: 500px;
  box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1);
}
.modal-header {
  padding: 1.5rem; border-bottom: 1px solid #e2e8f0;
  display: flex; justify-content: space-between; align-items: center;
}
.modal-body { padding: 1.5rem; }
.form-group label {
  display: block; font-size: 0.875rem; font-weight: 600;
  margin-bottom: 0.5rem; color: #4a5568;
}
.modal-footer {
  padding: 1.5rem; border-top: 1px solid #e2e8f0;
  display: flex; justify-content: flex-end; gap: 1rem;
}
.btn-secondary {
  background: white; color: #4a5568; border: 1px solid #cbd5e0;
}
.close-btn {
  background: transparent; color: #a0aec0; font-size: 1.5rem; padding: 0; box-shadow: none;
}
</style>
