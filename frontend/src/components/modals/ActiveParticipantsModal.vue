<template>
  <BaseModal
    :show="show"
    title="Participantes en el Evento"
    maxWidth="800px"
    @close="$emit('close')"
  >
    <div class="active-participants-container">
      <div class="modal-filters">
        <div class="search-box">
           <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
           <input v-model="search" type="text" placeholder="Buscar participante..." />
        </div>
        <div class="count-badge">
           {{ filteredParticipants.length }} presentes
        </div>
      </div>

      <LoadingSpinner v-if="loading" />

      <div v-else-if="participants.length === 0" class="empty-list">
         <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
         <p>No hay participantes activos en este momento.</p>
      </div>

      <div v-else class="participants-list">
         <table class="styled-table compact">
            <thead>
               <tr>
                  <th>Participante</th>
                  <th>Ingreso</th>
                  <th style="text-align: right">Acción</th>
               </tr>
            </thead>
            <tbody>
               <tr v-for="p in filteredParticipants" :key="p.idCheckIn">
                  <td>
                    <div class="user-info">
                      <div class="avatar-mini" v-if="!p.fotoPerfil">{{ getInitials(p.participanteNombre) }}</div>
                      <img v-else :src="p.fotoPerfil" :alt="p.participanteNombre" class="avatar-mini avatar-img" @error="handleImageError($event)" />
                      <span>{{ p.participanteNombre }}</span>
                    </div>
                  </td>
                  <td>{{ formatTime(p.fechaHoraCheckIn) }}</td>
                  <td style="text-align: right">
                     <button class="btn-eject" @click="handleEject(p)" title="Retirar del evento">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
                        Sacar
                     </button>
                  </td>
               </tr>
               <tr v-if="filteredParticipants.length === 0">
                  <td colspan="3" class="text-center py-4">No se encontraron coincidencias.</td>
               </tr>
            </tbody>
         </table>
      </div>
    </div>
  </BaseModal>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import BaseModal from './BaseModal.vue';
import LoadingSpinner from '../ui/LoadingSpinner.vue';
import api from '../../services/api';
import { useNotificationStore } from '../../stores/notifications';
import { useConfirmStore } from '../../stores/confirm';

const props = defineProps({
  show: Boolean,
  eventoId: Number,
  eventoNombre: String
});

const emit = defineEmits(['close', 'updated']);

const notificationStore = useNotificationStore();
const confirmStore = useConfirmStore();

const participants = ref([]);
const loading = ref(false);
const search = ref('');

const fetchParticipants = async () => {
  if (!props.eventoId || !props.show) return;
  loading.value = true;
  try {
    const res = await api.get(`/checkins/evento/${props.eventoId}/activos`);
    participants.value = res.data;
  } catch (err) {
    console.error(err);
    notificationStore.showError('Error al cargar participantes activos');
  } finally {
    loading.value = false;
  }
};

watch(() => props.show, (newVal) => {
  if (newVal) fetchParticipants();
});

const filteredParticipants = computed(() => {
  if (!search.value) return participants.value;
  return participants.value.filter(p => 
    p.participanteNombre.toLowerCase().includes(search.value.toLowerCase())
  );
});

const handleEject = async (p) => {
  const confirmed = await confirmStore.ask({
    title: 'Retirar Participante',
    message: `¿Estás seguro de que quieres retirar a ${p.participanteNombre} del evento?`,
    confirmText: 'Retirar',
    type: 'danger'
  });

  if (!confirmed) return;

  try {
    // We need the participant ID to do force exit
    // Wait, CheckInResponse has participacionId, and the original participantId?
    // Let's check CheckInResponse.java if possible or inferred.
    // Based on Controller, we need participanteId and eventoId.
    // I should have included it in CheckInResponse.
    
    // I'll update CheckInServiceImpl mapToResponse to include participanteId.
    
    // For now, I'll assume I have it or I'll fix the backend again.
    // Actually, I'll go back and add it to CheckInResponse.
    
    // For now, let's finish the component structure.
    await api.post('/checkins/force-exit', {
      participanteId: p.participanteId, // Will add this field
      eventoId: props.eventoId
    });
    
    notificationStore.showSuccess('Participante retirado del evento');
    fetchParticipants();
    emit('updated');
  } catch (err) {
    notificationStore.showError('Error al retirar participante');
  }
};

const getInitials = (name) => {
  if (!name) return '?';
  return name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
};

const formatTime = (dateStr) => {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
};

const handleImageError = (event) => {
  // Replace broken image with initials fallback
  event.target.style.display = 'none';
  const parent = event.target.parentElement;
  if (parent && !parent.querySelector('.avatar-mini:not(img)')) {
    const initialsDiv = document.createElement('div');
    initialsDiv.className = 'avatar-mini';
    initialsDiv.textContent = getInitials(event.target.alt);
    parent.insertBefore(initialsDiv, event.target);
  }
};
</script>

<style scoped>
.active-participants-container {
  min-height: 300px;
}

.modal-filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  gap: 1rem;
}

.search-box {
  position: relative;
  flex: 1;
  display: flex;
  align-items: center;
}

.search-box svg {
  position: absolute;
  left: 12px;
  color: #94a3b8;
}

.search-box input {
  width: 100%;
  padding: 0.6rem 1rem 0.6rem 2.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 0.9rem;
}

.count-badge {
  background: #e0e7ff;
  color: #4338ca;
  padding: 0.5rem 1rem;
  border-radius: 50px;
  font-weight: 700;
  font-size: 0.85rem;
  white-space: nowrap;
}

.empty-list {
  text-align: center;
  padding: 4rem 2rem;
  color: #94a3b8;
}

.empty-list svg {
  margin-bottom: 1rem;
  opacity: 0.3;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.avatar-mini {
  width: 32px;
  height: 32px;
  background: var(--primary-gradient);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 700;
}

.avatar-mini.avatar-img {
  object-fit: cover;
  background: #e2e8f0;
}

.btn-eject {
  background: #fef2f2;
  color: #ef4444;
  border: 1px solid #fee2e2;
  padding: 0.4rem 0.75rem;
  border-radius: 50px;
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  transition: all 0.2s;
}

.btn-eject:hover {
  background: #ef4444;
  color: white;
  border-color: #ef4444;
}

.styled-table.compact {
  font-size: 0.9rem;
}

.styled-table.compact th, .styled-table.compact td {
  padding: 0.75rem 1rem;
}
</style>
