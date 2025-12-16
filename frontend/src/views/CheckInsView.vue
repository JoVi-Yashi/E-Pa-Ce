<template>
  <div class="container animate-fade-in">
    <div class="header-actions">
      <div class="glass-header">
         <h1>Gestión de Check-Ins</h1>
      </div>
      <div class="actions">
        <router-link to="/" class="back-link">Volver</router-link>
        <button class="btn-primary" @click="resetForm(); showForm = true">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:8px"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
          Manual
        </button>
        <button class="btn-primary" @click="resetQrForm(); showQrForm = true">
           <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:8px"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>
           Generar QR
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
              <th>Participación</th>
              <th>Participante</th>
              <th>Evento</th>
              <th>Fecha/Hora</th>
              <th>Método</th>
              <th style="text-align: right">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="c in checkins" :key="c.idCheckIn">
              <td>#{{ c.idCheckIn }}</td>
              <td>ID: {{ c.participacionId }}</td>
              <td>{{ c.participanteNombre }}</td>
              <td>{{ c.eventoNombre }}</td>
              <td>{{ c.fechaHoraCheckIn }}</td>
              <td>
                <span class="method-badge">{{ c.metodoCheckIn }}</span>
              </td>
              <td style="text-align: right">
                <button class="icon-btn delete" @click="deleteCheckIn(c.idCheckIn)" title="Eliminar">
                   <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
       <div v-if="checkins.length === 0" class="empty-state">
        No hay Check-Ins registrados.
      </div>
    </div>

    <!-- Manual Form Overlay -->
    <div v-if="showForm" class="modal-overlay" @click.self="showForm = false">
      <div class="modal-content animate-slide-up">
        <div class="modal-header">
           <h3>Registrar Check-In Manual</h3>
           <button class="close-btn" @click="showForm = false">&times;</button>
        </div>
        <form @submit.prevent="submitForm" class="modal-body">
           <div class="form-group">
             <label>Evento</label>
             <select v-model="selectedEventoId" @change="onEventoSelect" required>
               <option :value="null" disabled>Seleccione un evento</option>
               <option v-for="e in eventos" :key="e.idEvento" :value="e.idEvento">
                 {{ e.nombre }}
               </option>
             </select>
           </div>

           <div class="form-group" v-if="selectedEventoId">
             <label>Participante (Pendiente)</label>
             <select v-model="form.participacionId" required>
               <option :value="null" disabled>Seleccione participante</option>
               <option v-for="p in participacionesEvento" :key="p.idParticipacion" :value="p.idParticipacion">
                 {{ p.participanteNombre }} (Doc: {{ p.participanteDocumento }})
               </option>
             </select>
           </div>
           
           <div class="modal-footer">
             <button type="button" class="btn-secondary" @click="showForm = false">Cancelar</button>
             <button type="submit" class="btn-primary" :disabled="!form.participacionId">Confirmar Check-In</button>
           </div>
        </form>
      </div>
    </div>

    <!-- QR Generation Form Overlay -->
    <div v-if="showQrForm" class="modal-overlay" @click.self="showQrForm = false">
      <div class="modal-content animate-slide-up">
        <div class="modal-header">
           <h3>Generar QR de Evento</h3>
           <button class="close-btn" @click="showQrForm = false">&times;</button>
        </div>
        <div class="modal-body">
          <p class="helper-text">Seleccione un evento para generar su código QR de asistencia.</p>
          <div class="form-group">
              <label>Evento</label>
              <select v-model="selectedEventoId" required>
                 <option :value="null" disabled>Seleccione un evento</option>
                 <option v-for="e in eventos" :key="e.idEvento" :value="e.idEvento">
                   {{ e.nombre }}
                 </option>
              </select>
          </div>
          
          <div v-if="selectedEventoId" class="qr-display" style="text-align: center; margin-top: 1rem;">
             <img :src="`https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=EVENTO:${selectedEventoId}`" alt="QR Code" />
             <p style="font-size: 0.8rem; margin-top: 0.5rem; color: #718096;">
               Escanea este código para registrar asistencia.
             </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const checkins = ref([]);
const eventos = ref([]);
const participacionesEvento = ref([]);
const loading = ref(false);
const error = ref('');
const showForm = ref(false);
const showQrForm = ref(false);

const form = ref({ participacionId: null, metodoCheckIn: 'MANUAL', ipCheckIn: '127.0.0.1' });
const selectedEventoId = ref(null);
const qrCode = ref('');
const generatedQrUrl = ref('');

const fetchCheckIns = async () => {
  loading.value = true;
  try {
    const [checkRes, eventRes] = await Promise.all([
        api.get('/checkins'),
        api.get('/eventos')
    ]);
    checkins.value = checkRes.data;
    eventos.value = eventRes.data;
  } catch (err) {
    error.value = 'Error al cargar datos: ' + err.message;
  } finally {
    loading.value = false;
  }
};

const onEventoSelect = async () => {
    if (!selectedEventoId.value) return;
    try {
        const res = await api.get(`/participaciones/evento/${selectedEventoId.value}`);
        // Filter those who might not have checked in?
        // Checking against checkins list might be complex client side if list is large, 
        // but for now let's show all participations of the event.
        participacionesEvento.value = res.data; 
    } catch (err) {
        console.error(err);
    }
}

const resetForm = () => {
  form.value = { participacionId: null, metodoCheckIn: 'MANUAL', ipCheckIn: '127.0.0.1' };
  selectedEventoId.value = null;
  participacionesEvento.value = [];
};

const resetQrForm = () => {
    qrCode.value = '';
}

const deleteCheckIn = async (id) => {
  if (!confirm('Eliminar seguro?')) return;
  try {
    await api.delete(`/checkins/${id}`);
    fetchCheckIns();
  } catch (err) {
    alert(err.message);
  }
};

const submitForm = async () => {
  try {
    await api.post('/checkins', form.value);
    showForm.value = false;
    fetchCheckIns();
  } catch (err) {
    alert('Error: ' + (err.response?.data?.message || err.message));
  }
};

const submitQrForm = async () => {
    try {
        await api.post(`/checkins/qr?codigo=${qrCode.value}`); 
        showQrForm.value = false;
        alert('Check-In QR Exitoso');
        fetchCheckIns();
    } catch (err) {
         alert('Error QR: ' + (err.response?.data?.message || err.message));
    }
}

onMounted(fetchCheckIns);
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

.method-badge {
    background: #e2e8f0;
    color: #4a5568;
    padding: 0.2rem 0.6rem;
    border-radius: 4px;
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

.helper-text {
    font-size: 0.9rem;
    color: #718096;
    margin-bottom: 1rem;
}

.form-group label {
  display: block;
  font-size: 0.875rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #4a5568;
}

.modal-footer {
  padding: 1.5rem 0 0 0;
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
