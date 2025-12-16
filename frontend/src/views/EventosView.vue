<template>
  <div class="container animate-fade-in">
    <div class="header-actions">
      <div class="glass-header">
        <h1>Gestión de Eventos</h1>
      </div>
      <div class="actions">
         <router-link to="/" class="back-link">Volver</router-link>
         <button @click="openCreateModal">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:8px"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
            Crear Evento
         </button>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div> Cargando eventos...
    </div>
    
    <div v-if="error" class="error-msg">{{ error }}</div>

    <div class="card" v-if="!loading">
      <div class="table-responsive">
        <table class="styled-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nombre</th>
              <th>Descripción</th>
              <th>Estado</th>
              <th style="text-align: right">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="evento in eventos" :key="evento.idEvento">
              <td>#{{ evento.idEvento }}</td>
              <td><strong>{{ evento.nombre }}</strong></td>
              <td>{{ evento.descripcion }}</td>
              <td>
                <span :class="['status-badge', evento.estado.toLowerCase()]">{{ evento.estado }}</span>
              </td>
              <td style="text-align: right">
                <button class="icon-btn edit" @click="editEvento(evento)" title="Editar">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                </button>
                <button class="icon-btn delete" @click="deleteEvento(evento.idEvento)" title="Eliminar">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div v-if="eventos.length === 0" class="empty-state">
        No hay eventos registrados.
      </div>
    </div>

    <!-- Modal/Form Overlay -->
    <div v-if="showForm" class="modal-overlay" @click.self="showForm = false">
      <div class="modal-content animate-slide-up">
        <div class="modal-header">
           <h3>{{ isEdit ? 'Editar Evento' : 'Nuevo Evento' }}</h3>
           <button class="close-btn" @click="showForm = false">&times;</button>
        </div>
        
        <form @submit.prevent="submitForm" class="modal-body">
          <div class="form-grid">
            <div class="form-group">
              <label>Nombre</label>
              <input v-model="form.nombre" required placeholder="Nombre del evento" />
            </div>
            
            <div class="form-group full-width">
              <label>Descripción</label>
              <textarea v-model="form.descripcion" rows="3" placeholder="Detalles del evento..."></textarea>
            </div>
            
            <div class="form-group">
              <label>Fecha Inicio</label>
              <input v-model="form.fechaInicio" type="datetime-local" />
            </div>
            
            <div class="form-group">
              <label>Fecha Fin</label>
              <input v-model="form.fechaFin" type="datetime-local" />
            </div>

            <div class="form-group">
              <label>Duración (Horas)</label>
              <input v-model="form.duracionHoras" type="number" step="0.1" />
            </div>
            
            <div class="form-group">
              <label>Aforo Máximo</label>
              <input v-model="form.aforoMaximo" type="number" />
            </div>
            
            <div class="form-group">
               <label>Estado</label>
               <select v-model="form.estado">
                 <option value="PROGRAMADO">PROGRAMADO</option>
                 <option value="EN_CURSO">EN_CURSO</option>
                 <option value="FINALIZADO">FINALIZADO</option>
                 <option value="CANCELADO">CANCELADO</option>
               </select>
            </div>
            
            <div class="form-group">
              <label>Modalidad</label>
              <select v-model="form.modalidadEventoId" required>
                <option v-for="m in modalidades" :key="m.idModalidadEvento" :value="m.idModalidadEvento">
                  {{ m.nombreModalidadEvento }}
                </option>
              </select>
            </div>
            
            <div class="form-group">
              <label>Tipo de Evento</label>
              <select v-model="form.tipoEventoId" required>
                <option v-for="t in tipos" :key="t.idTipoEvento" :value="t.idTipoEvento">
                  {{ t.nombreTipoEvento }}
                </option>
              </select>
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn-secondary" @click="showForm = false">Cancelar</button>
            <button type="submit" class="btn-primary">
               {{ isEdit ? 'Actualizar' : 'Guardar Evento' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import api from '../services/api';

const eventos = ref([]);
const modalidades = ref([]);
const tipos = ref([]);
const loading = ref(false);
const error = ref('');
const showForm = ref(false);
const isEdit = ref(false);

const form = ref({
  idEvento: null,
  nombre: '',
  descripcion: '',
  fechaInicio: '',
  fechaFin: '',
  duracionHoras: 0,
  aforoMaximo: 0,
  estado: 'PROGRAMADO',
  modalidadEventoId: null,
  tipoEventoId: null
});

watch([() => form.value.fechaInicio, () => form.value.fechaFin], ([start, end]) => {
    if (start && end) {
        const s = new Date(start);
        const e = new Date(end);
        const diff = (e - s) / (1000 * 60 * 60);
        form.value.duracionHoras = diff > 0 ? parseFloat(diff.toFixed(2)) : 0;
    }
});

const fetchData = async () => {
  loading.value = true;
  try {
    const [evRes, modRes, tipRes] = await Promise.all([
      api.get('/eventos'),
      api.get('/modalidades'),
      api.get('/tipos-evento')
    ]);
    eventos.value = evRes.data;
    modalidades.value = modRes.data;
    tipos.value = tipRes.data;
  } catch (err) {
    error.value = 'Error cargando datos: ' + err.message;
  } finally {
    loading.value = false;
  }
};

const fetchEventos = async () => { // Keep for refresh
    try {
        const res = await api.get('/eventos');
        eventos.value = res.data;
    } catch(err) { console.error(err); }
};

const resetForm = () => {
  form.value = {
    idEvento: null,
    nombre: '',
    descripcion: '',
    fechaInicio: '',
    fechaFin: '',
    duracionHoras: 0,
    aforoMaximo: 0,
    estado: 'PROGRAMADO',
    modalidadEventoId: null,
    tipoEventoId: null
  };
};

const openCreateModal = () => {
  isEdit.value = false;
  resetForm();
  showForm.value = true;
};

const editEvento = (evento) => {
  form.value = { ...evento, modalidadEventoId: evento.idModalidadEvento, tipoEventoId: evento.idTipoEvento };
  isEdit.value = true;
  showForm.value = true;
};

const deleteEvento = async (id) => {
  if (!confirm('Seguro que deseas eliminar este evento?')) return;
  try {
    await api.delete(`/eventos/${id}`);
    fetchEventos();
  } catch (err) {
    alert('Error eliminando: ' + err.message);
  }
};

const submitForm = async () => {
  try {
    if (isEdit.value) {
      await api.put(`/eventos/${form.value.idEvento}`, form.value);
    } else {
      await api.post('/eventos', form.value);
    }
    showForm.value = false;
    fetchEventos();
  } catch (err) {
    alert('Error guardando: ' + (err.response?.data || err.message));
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

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 50px;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
}

.status-badge.programado { background: #ebf8ff; color: #3182ce; }
.status-badge.en_curso { background: #f0fff4; color: #38a169; }
.status-badge.finalizado { background: #faf5ff; color: #805ad5; }
.status-badge.cancelado { background: #frf2f2; color: #e53e3e; }

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
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1), 0 10px 10px -5px rgba(0,0,0,0.04);
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
  background: transparent;
}
</style>
