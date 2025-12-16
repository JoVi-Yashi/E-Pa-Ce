<template>
  <div class="container animate-fade-in">
    <div class="header-actions">
      <div class="glass-header">
         <h1>Gestión de Participaciones</h1>
      </div>
      <div class="actions">
         <router-link to="/" class="back-link">Volver</router-link>
         <button class="btn-primary" @click="openCreateModal">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:8px"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="8.5" cy="7" r="4"></circle><line x1="20" y1="8" x2="20" y2="14"></line><line x1="23" y1="11" x2="17" y2="11"></line></svg>
            Registrar Participación
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
              <th>Evento</th>
              <th>Participante</th>
              <th>Documento</th>
              <th>Fecha Inscripción</th>
              <th style="text-align: right">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="p in participaciones" :key="p.idParticipacion">
              <td>#{{ p.idParticipacion }}</td>
              <td>
                 <strong>{{ p.eventoNombre }}</strong>
                 <br><small class="text-muted">ID: {{ p.eventoId }}</small>
              </td>
              <td>{{ p.participanteNombre }}</td>
              <td>{{ p.participanteDocumento }}</td>
              <td>{{ p.fechaInscripcion }}</td>
              <td style="text-align: right">
                <button class="icon-btn delete" @click="deleteParticipacion(p.idParticipacion)" title="Eliminar">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div v-if="participaciones.length === 0" class="empty-state">
        No hay participaciones registradas.
      </div>
    </div>

    <!-- Form Modal -->
    <div v-if="showForm" class="modal-overlay" @click.self="showForm = false">
      <div class="modal-content animate-slide-up">
        <div class="modal-header">
          <h3>Registrar Participación</h3>
          <button class="close-btn" @click="showForm = false">&times;</button>
        </div>
        <form @submit.prevent="submitForm" class="modal-body">
          <div class="form-group">
            <label>ID Evento:</label>
            <input v-model="form.eventoId" type="number" required placeholder="ID del evento" />
          </div>
          <div class="form-group">
            <label>Documento Participante (CC):</label>
            <input v-model="form.participanteDocumento" type="number" required placeholder="Número de documento" />
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
import api from '../services/api';

const participaciones = ref([]);
const loading = ref(false);
const error = ref('');
const showForm = ref(false);

const form = ref({
  eventoId: null,
  participanteDocumento: null
});

const fetchParticipaciones = async () => {
  loading.value = true;
  try {
    const res = await api.get('/participaciones');
    participaciones.value = res.data;
  } catch (err) {
    error.value = 'Error cargando participaciones: ' + err.message;
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  form.value = {
    eventoId: null,
    participanteDocumento: null
  };
};

const openCreateModal = () => {
  resetForm();
  showForm.value = true;
};

const deleteParticipacion = async (id) => {
  if (!confirm('Seguro que deseas eliminar esta participación?')) return;
  try {
    await api.delete(`/participaciones/${id}`);
    fetchParticipaciones();
  } catch (err) {
    alert('Error eliminando: ' + err.message);
  }
};

const submitForm = async () => {
  try {
    await api.post('/participaciones', form.value);
    showForm.value = false;
    fetchParticipaciones();
  } catch (err) {
    alert('Error guardando: ' + (err.response?.data || err.message));
  }
};

onMounted(() => {
  fetchParticipaciones();
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

.text-muted {
  color: #718096;
  font-size: 0.85rem;
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
</style>
