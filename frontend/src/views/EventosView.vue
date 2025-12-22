<template>
  <div class="container animate-fade-in">
    <PageHeader 
      title="Gestión de Eventos" 
      subtitle="Organiza, monitorea y gestiona los eventos activos de la plataforma"
    >
      <template #actions v-if="authStore.hasPermission('EVENTO:CREATE')">
         <button class="btn-premium-header" @click="openCreateModal">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
            <span>Crear Evento</span>
         </button>
      </template>
    </PageHeader>

    <LoadingSpinner v-if="loading" />
    
    <div v-if="error" class="error-msg">{{ error }}</div>

    <div class="card" v-if="!loading">
      <div v-if="eventos.length > 0">
        <!-- Filters -->
        <div v-if="eventos.length > 0" class="filters-container" style="margin-bottom: 1.5rem;">
          <div class="filter-item search">
            <label>Búsqueda de Eventos</label>
            <div class="search-wrapper">
               <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
               <input v-model="search" type="text" placeholder="Buscar por nombre de evento..." />
            </div>
          </div>
        </div>

        <TablePagination 
          v-if="processedEventos.length > 0"
          :totalItems="processedEventos.length" 
          v-model:itemsPerPage="itemsPerPage" 
          v-model:currentPage="currentPage" 
        />
        <div class="table-responsive mobile-cards">
          <table class="styled-table">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Capacidad</th>
                <th>Presencia</th>
                <th>Estado</th>
                <th v-if="authStore.hasPermission('EVENTO:UPDATE_ALL') || authStore.hasPermission('EVENTO:UPDATE_OWN') || authStore.hasPermission('EVENTO:DELETE_ALL') || authStore.hasPermission('EVENTO:DELETE_OWN') || authStore.hasPermission('EVENTO:READ_PARTICIPANTS') || authStore.hasPermission('EVENTO:SHOW_QR')" style="text-align: right">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="processedEventos.length === 0">
                <td colspan="4" style="text-align: center; padding: 3rem; color: var(--text-secondary);">
                  <div style="display: flex; flex-direction: column; align-items: center; gap: 1rem;">
                    <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" style="opacity: 0.3"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
                    <p style="font-weight: 600; font-size: 1.1rem;">No se encontraron coincidencias</p>
                    <p style="font-size: 0.9rem; margin-top: -0.5rem;">Prueba con otros términos de búsqueda.</p>
                    <button class="btn-sm-outline" @click="search = ''" style="margin-top: 0.5rem;">Limpiar Búsqueda</button>
                  </div>
                </td>
              </tr>
              <tr v-else v-for="evento in paginatedEventos" :key="evento.idEvento">
                <td data-label="Nombre">
                  <strong>{{ evento.nombre }}</strong>
                  <div style="font-size: 0.75rem; color: #718096; margin-top: 4px;">{{ evento.descripcion }}</div>
                </td>
                <td data-label="Capacidad">
                  <div class="aforo-pill" :class="{ 'aforo-full': (evento.aforoActual || 0) >= (evento.aforoMaximo || 0) && (evento.aforoMaximo || 0) > 0 }">
                    <span class="aforo-current">{{ evento.aforoActual || 0 }}</span>
                    <span class="aforo-divider">/</span>
                    <span class="aforo-max">{{ evento.aforoMaximo || '∞' }}</span>
                  </div>
                  <div class="aforo-bar-mini">
                    <div class="aforo-bar-fill" :style="{ width: calculateAforoPercent(evento) + '%' }"></div>
                  </div>
                </td>
                <td data-label="Presencia">
                   <div class="presence-actions" v-if="authStore.hasPermission('CHECKIN:QR')">
                      <span class="text-sm font-medium text-indigo-600 bg-indigo-50 px-3 py-1 rounded-full border border-indigo-100">
                        Scan QR Requerido
                      </span>
                   </div>
                   <span v-else class="text-muted">N/A</span>
                </td>
                <td data-label="Estado">
                   <span :class="['status-badge', evento.estado.toLowerCase()]">{{ formatEstado(evento.estado) }}</span>
                </td>
                <td v-if="authStore.hasPermission('EVENTO:UPDATE_ALL') || authStore.hasPermission('EVENTO:UPDATE_OWN') || authStore.hasPermission('EVENTO:DELETE_ALL') || authStore.hasPermission('EVENTO:DELETE_OWN') || authStore.hasPermission('EVENTO:READ_PARTICIPANTS') || authStore.hasPermission('EVENTO:SHOW_QR')" data-label="Acciones" style="text-align: right">
                  <div class="action-buttons">
                    <TableActionButton v-if="authStore.hasPermission('EVENTO:READ_PARTICIPANTS')" type="edit" title="Ver Presentes" @click="openParticipantsModal(evento)">
                       <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
                    </TableActionButton>
                    <TableActionButton v-if="authStore.hasPermission('EVENTO:SHOW_QR')" type="edit" title="Acceso Rápido (QR)" @click="showEventQr(evento)">
                       <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><rect x="7" y="7" width="3" height="3"></rect><rect x="14" y="7" width="3" height="3"></rect><rect x="7" y="14" width="3" height="3"></rect><rect x="14" y="14" width="3" height="3"></rect></svg>
                    </TableActionButton>
                    <TableActionButton v-if="authStore.can('UPDATE', 'EVENTO', evento.creadoPorId)" type="edit" title="Editar" @click="editEvento(evento)">
                       <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                    </TableActionButton>
                    <TableActionButton v-if="authStore.can('DELETE', 'EVENTO', evento.creadoPorId)" type="delete" title="Eliminar" @click="deleteEvento(evento.idEvento)">
                       <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                    </TableActionButton>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <TablePagination 
          v-if="eventos.length > 0"
          :totalItems="eventos.length" 
          v-model:itemsPerPage="itemsPerPage" 
          v-model:currentPage="currentPage" 
        />
      </div>
      
      <EmptyState 
        v-else 
        title="No hay eventos" 
        description="No se encuentran eventos en el sistema."
      >
        <template #actions v-if="authStore.hasPermission('EVENTO:CREATE')">
             <button class="btn-primary" @click="openCreateModal">Crear Primer Evento</button>
        </template>
      </EmptyState>
    </div>

    <!-- Modal/Form Overlay -->
    <BaseModal
        :show="showForm"
        :title="isEdit ? 'Editar Evento' : 'Nuevo Evento'"
        @close="showForm = false"
    >
        <form id="eventoForm" @submit.prevent="submitForm">
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
              <input v-model="form.fechaInicio" type="datetime-local" required />
            </div>
            
            <div class="form-group">
              <label>Fecha Fin</label>
              <input v-model="form.fechaFin" type="datetime-local" required />
            </div>

            <div class="form-group">
              <label>Duración (Horas)</label>
              <input v-model="form.duracionHoras" type="number" step="0.1" readonly />
            </div>
            
            <div class="form-group">
              <label>Aforo Máximo</label>
              <input v-model="form.aforoMaximo" type="number" />
            </div>
            
            <div class="form-group">
               <label>Estado</label>
               <select v-model="form.estado">
                 <option value="PROGRAMADO">PROGRAMADO</option>
                 <option value="EN_CURSO">EN CURSO</option>
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
        </form>

        <template #footer>
            <button type="button" class="btn-secondary" @click="showForm = false">Cancelar</button>
            <button type="submit" form="eventoForm" class="btn-primary" :disabled="isEdit && !authStore.can('UPDATE', 'EVENTO', form.creadoPorId) || !isEdit && !authStore.hasPermission('EVENTO:CREATE')">
               {{ isEdit ? 'Actualizar' : 'Guardar Evento' }}
            </button>
        </template>
    </BaseModal>

    <!-- QR Modal -->
    <BaseModal
        :show="showQrModal"
        title="QR de Acceso Rápido"
        maxWidth="400px"
        @close="showQrModal = false"
    >
        <div v-if="selectedEvent" class="qr-container-view">
            <div class="qr-header">
                <h4>{{ selectedEvent.nombre }}</h4>
                <p>Escanee este código para auto-inscripción o check-in rápido.</p>
            </div>
            <div class="qr-image-wrapper">
                <img :src="`https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=EVENTO:${selectedEvent.idEvento}`" alt="QR Evento" />
            </div>
            <div class="qr-footer">
                <p>ID: {{ selectedEvent.idEvento }}</p>
                <button @click="printQr" class="btn-sm-outline">Imprimir QR</button>
            </div>
        </div>
    </BaseModal>

    <!-- Participants List Modal -->
    <ActiveParticipantsModal 
        :show="showParticipantsModal"
        :eventoId="selectedEvent?.idEvento"
        :eventoNombre="selectedEvent?.nombre"
        @close="showParticipantsModal = false"
        @updated="fetchEventos"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from 'vue';
import api from '../services/api';
import { useAuthStore } from '../stores/auth';
import PageHeader from '../components/layout/PageHeader.vue';
import LoadingSpinner from '../components/ui/LoadingSpinner.vue';
import BaseModal from '../components/modals/BaseModal.vue';
import ActiveParticipantsModal from '../components/modals/ActiveParticipantsModal.vue';
import TablePagination from '../components/ui/TablePagination.vue';
import EmptyState from '../components/ui/EmptyState.vue';
import TableActionButton from '../components/ui/TableActionButton.vue';
import { useNotificationStore } from '../stores/notifications';
import { useConfirmStore } from '../stores/confirm';

const authStore = useAuthStore();
const notificationStore = useNotificationStore();
const confirmStore = useConfirmStore();

const eventos = ref([]);
const modalidades = ref([]);
const tipos = ref([]);
const loading = ref(false);
const error = ref('');
const showForm = ref(false);
const isEdit = ref(false);
const showQrModal = ref(false);
const showParticipantsModal = ref(false);
const selectedEvent = ref(null);
const userActiveCheckIns = ref([]); // Store user's active checkins

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

// Pagination & Search State
const currentPage = ref(1);
const itemsPerPage = ref(10);
const search = ref('');

const processedEventos = computed(() => {
  if (!search.value) return eventos.value;
  return eventos.value.filter(e => 
    e.nombre.toLowerCase().includes(search.value.toLowerCase())
  );
});

const totalPages = computed(() => Math.ceil(processedEventos.value.length / itemsPerPage.value));
const paginatedEventos = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value;
    return processedEventos.value.slice(start, start + itemsPerPage.value);
});

watch([() => form.value.fechaInicio, () => form.value.fechaFin], ([start, end]) => {
    if (start && end) {
        const s = new Date(start);
        const e = new Date(end);
        const diff = (e - s) / (1000 * 60 * 60);
        form.value.duracionHoras = diff > 0 ? parseFloat(diff.toFixed(2)) : 0;
    }
});

let pollingInterval = null;

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
    await fetchUserActiveCheckIns();
    currentPage.value = 1; // RESET
  } catch (err) {
    error.value = 'Error cargando datos: ' + err.message;
  } finally {
    loading.value = false;
  }
};

const fetchEventos = async (showLoader = false) => { 
    if (showLoader) loading.value = true;
    try {
        const res = await api.get('/eventos');
        eventos.value = res.data;
        // await fetchUserActiveCheckIns(); // Optional: update user status too?
    } catch(err) { console.error(err); }
    finally {
        if (showLoader) loading.value = false;
    }
};

const fetchUserActiveCheckIns = async () => {
    if (!authStore.user) return;
    try {
        // We'll fetch all check-ins for the user and filter for entries without exit
        // This is a bit inefficient but works for now. 
        // Better: and endpoint for "my-current-presence"
        const res = await api.get('/checkins'); // This might be too much, but we'll filter
        const myCheckIns = res.data.filter(c => c.participanteId === authStore.user.documentoIdentidad);
        
        // Group by event and find last action
        const latestByEvent = {};
        myCheckIns.forEach(c => {
            if (!latestByEvent[c.eventoId] || new Date(c.fechaHoraCheckIn) > new Date(latestByEvent[c.eventoId].fechaHoraCheckIn)) {
                latestByEvent[c.eventoId] = c;
            }
        });

        userActiveCheckIns.value = Object.values(latestByEvent).filter(c => c.tipoAccion === 'ENTRADA');
    } catch (err) {
        console.error("Error fetching user checkins", err);
    }
};

const isUserActiveInEvent = (eventoId) => {
    return userActiveCheckIns.value.some(c => c.eventoId === eventoId);
};

const isEventFull = (evento) => {
    return (evento.aforoMaximo || 0) > 0 && (evento.aforoActual || 0) >= (evento.aforoMaximo || 0);
};

const handleManualCheckIn = async (evento) => {
    if (isEventFull(evento)) {
        notificationStore.showError('El evento está lleno.');
        return;
    }
    try {
        await api.post('/checkins/scan', {
            participanteId: authStore.user.documentoIdentidad,
            eventoId: evento.idEvento
        });
        notificationStore.showSuccess(`Has ingresado a ${evento.nombre}`);
        fetchEventos();
    } catch (err) {
        notificationStore.showError(err.response?.data || 'Error al ingresar al evento');
    }
};

const handleManualCheckOut = async (evento) => {
    try {
        // Find existing check-in to confirm or just hit scan-out (now force-exit)
        // scan-out was renamed to force-exit but it's the same logic. 
        // I'll use the scan-out logic but hit the new endpoint if I changed it.
        // Wait, I renamed it in the controller.
        await api.post('/checkins/force-exit', {
            participanteId: authStore.user.documentoIdentidad,
            eventoId: evento.idEvento
        });
        notificationStore.showSuccess(`Has salido de ${evento.nombre}`);
        fetchEventos();
    } catch (err) {
        notificationStore.showError(err.response?.data || 'Error al salir del evento');
    }
};

const openParticipantsModal = (evento) => {
    selectedEvent.value = evento;
    showParticipantsModal.value = true;
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
  // Formatear fechas para input datetime-local (YYYY-MM-DDTHH:mm)
  const formatForInput = (dateStr) => {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const tzOffset = date.getTimezoneOffset() * 60000; // offset in milliseconds
    const localISOTime = (new Date(date.getTime() - tzOffset)).toISOString().slice(0, 16);
    return localISOTime;
  };

  form.value = { 
    ...evento, 
    fechaInicio: formatForInput(evento.fechaInicio),
    fechaFin: formatForInput(evento.fechaFin)
  };
  isEdit.value = true;
  showForm.value = true;
};

const deleteEvento = async (id) => {
  const confirmed = await confirmStore.ask({
      title: 'Eliminar Evento',
      message: '¿Estás seguro de que deseas eliminar este evento? Esta acción no se puede deshacer.',
      confirmText: 'Eliminar',
      type: 'danger'
  });

  if (!confirmed) return;

  try {
    await api.delete(`/eventos/${id}`);
    notificationStore.showSuccess('Evento eliminado correctamente');
    fetchEventos();
  } catch (err) {
    notificationStore.showError('Error eliminando: ' + (err.response?.data?.message || err.message));
  }
};

const showEventQr = (evento) => {
    selectedEvent.value = evento;
    showQrModal.value = true;
};

const printQr = () => {
    window.print();
};

const submitForm = async () => {
  // Client-side Validations
  const now = new Date();
  const start = new Date(form.value.fechaInicio);
  const end = new Date(form.value.fechaFin);

  if (!isEdit.value && start < now) {
      notificationStore.showWarning('La fecha de inicio no puede ser anterior a la actual.');
      return;
  }

  if (end < start) {
      notificationStore.showWarning('La fecha de fin no puede ser anterior a la fecha de inicio.');
      return;
  }

  try {
    if (isEdit.value) {
      await api.put(`/eventos/${form.value.idEvento}`, form.value);
      notificationStore.showSuccess('Evento actualizado correctamente');
    } else {
      await api.post('/eventos', form.value);
      notificationStore.showSuccess('Evento creado correctamente');
    }
    showForm.value = false;
    fetchEventos();
  } catch (err) {
    notificationStore.showError('Error guardando: ' + (err.response?.data?.message || err.message));
  }
};

onMounted(() => {
  fetchData();
  pollingInterval = setInterval(() => {
      fetchEventos();
  }, 5000);
});

onUnmounted(() => {
    if (pollingInterval) clearInterval(pollingInterval);
});

const formatEstado = (estado) => {
    if (!estado) return '';
    return estado.replace(/_/g, ' ').toUpperCase();
};

const calculateAforoPercent = (evento) => {
    if (!evento.aforoMaximo || evento.aforoMaximo <= 0) return (evento.aforoActual > 0 ? 100 : 0);
    return Math.min(100, (evento.aforoActual / evento.aforoMaximo) * 100);
};
</script>

<style scoped>
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
.status-badge.cancelado { background: #fef2f2; color: #e53e3e; }

/* Presence Actions */
.presence-actions {
    display: flex;
    justify-content: center;
}

.btn-presence {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 6px 14px;
    border-radius: 50px;
    font-size: 0.8rem;
    font-weight: 800;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    text-transform: uppercase;
    letter-spacing: 0.05em;
    border: 2px solid transparent;
}

.btn-presence.ent {
    background: white;
    color: #4f46e5;
    border-color: #4f46e5;
    box-shadow: 0 4px 10px rgba(79, 70, 229, 0.1);
}

.btn-presence.ent:hover:not(:disabled) {
    background: #4f46e5;
    color: white;
    transform: translateY(-2px);
    box-shadow: 0 8px 15px rgba(79, 70, 229, 0.2);
}

.btn-presence.sal {
    background: white;
    color: #ef4444;
    border-color: #ef4444;
    box-shadow: 0 4px 10px rgba(239, 68, 68, 0.1);
}

.btn-presence.sal:hover {
    background: #ef4444;
    color: white;
    transform: translateY(-2px);
    box-shadow: 0 8px 15px rgba(239, 68, 68, 0.2);
}

.btn-presence:disabled {
    opacity: 0.5;
    cursor: not-allowed;
    filter: grayscale(1);
    transform: none !important;
}

/* Aforo Styles */
.aforo-pill {
    display: flex;
    align-items: center;
    gap: 4px;
    background: #f1f5f9;
    padding: 2px 10px;
    border-radius: 50px;
    font-size: 0.85rem;
    font-weight: 700;
    width: fit-content;
    margin-bottom: 6px;
    border: 1px solid #e2e8f0;
}

.aforo-current { color: var(--primary-color); }
.aforo-divider { color: #94a3b8; font-weight: 400; }
.aforo-max { color: #64748b; }

.aforo-full {
    background: #fef2f2;
    border-color: #fecaca;
}
.aforo-full .aforo-current { color: #ef4444; }

.aforo-bar-mini {
    width: 60px;
    height: 4px;
    background: #e2e8f0;
    border-radius: 2px;
    overflow: hidden;
}

.aforo-bar-fill {
    height: 100%;
    background: var(--primary-gradient);
    transition: width 0.3s ease;
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

.btn-secondary {
  background: white;
  color: #4a5568;
  border: 1px solid #cbd5e0;
}

/* QR Styles */
.qr-btn {
    background: #f7fafc;
    border: 1px solid #e2e8f0;
    color: #4a5568;
    padding: 6px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s;
}

.qr-btn:hover {
    background: #ebf4ff;
    color: #6366f1;
    border-color: #6366f1;
    transform: translateY(-2px);
}

.qr-container-view {
    text-align: center;
    padding: 1rem;
}

.qr-header h4 {
    margin: 0;
    color: #2d3748;
}

.qr-header p {
    font-size: 0.8rem;
    color: #a0aec0;
    margin: 4px 0 0 0;
}

.qr-image-wrapper {
    margin: 2rem 0;
    padding: 1rem;
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
    display: inline-block;
}

.qr-footer p {
    font-size: 0.85rem;
    color: #718096;
    margin-bottom: 1.5rem;
}

.btn-sm-outline {
    background: transparent;
    border: 1px solid #667eea;
    color: #667eea;
    padding: 6px 16px;
    font-size: 0.8rem;
    border-radius: 6px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
}

.btn-sm-outline:hover {
    background: #667eea;
    color: white;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
  align-items: center;
}

.filters-container {
    background: rgba(255, 255, 255, 0.5);
    padding: 1.25rem;
    border-radius: 12px;
    border: 1px solid rgba(0,0,0,0.05);
}

.filter-item label {
    display: block;
    font-size: 0.75rem;
    font-weight: 700;
    text-transform: uppercase;
    color: var(--text-secondary);
    margin-bottom: 0.5rem;
}

.search-wrapper {
    position: relative;
    display: flex;
    align-items: center;
}

.search-wrapper svg {
    position: absolute;
    left: 12px;
    color: var(--text-secondary);
    opacity: 0.5;
}

.search-wrapper input {
    width: 100%;
    padding: 0.6rem 1rem 0.6rem 2.5rem;
    border: 1px solid #e2e8f0;
    border-radius: 10px;
    font-size: 0.9rem;
    transition: all 0.2s;
}

.search-wrapper input:focus {
    outline: none;
    border-color: var(--primary-color);
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* Scoped classes for utility-like styles used in the template */
.text-indigo-600 {
    color: #4f46e5;
}
.bg-indigo-50 {
    background-color: #eef2ff;
}
.border-indigo-100 {
    border-color: #e0e7ff;
}
.rounded-full {
    border-radius: 9999px;
}
.border {
    border-width: 1px;
    border-style: solid;
}
.px-3 {
    padding-left: 0.75rem;
    padding-right: 0.75rem;
}
.py-1 {
    padding-top: 0.25rem;
    padding-bottom: 0.25rem;
}
.font-medium {
    font-weight: 500;
}
</style>
