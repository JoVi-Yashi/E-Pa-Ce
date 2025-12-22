<template>
  <div class="container animate-fade-in">
    <PageHeader 
      title="Control de Asistencia" 
      subtitle="Gestiona el ingreso y salida de participantes mediante escaneo QR o registro manual"
    >
      <template #actions>
        <button v-if="authStore.hasPermission('CHECKIN:QR')" class="btn-glass-header" @click="openScannerModal">
           <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path><circle cx="12" cy="13" r="4"></circle></svg>
           <span>Escáner Inteligente</span>
        </button>
        <button v-if="authStore.hasPermission('CHECKIN:MANUAL')" class="btn-premium-header" @click="openCreateModal">
           <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
           <span>Asistencia Manual</span>
        </button>
      </template>
    </PageHeader>

    <LoadingSpinner v-if="loading" />
    <div v-if="error" class="error-msg">{{ error }}</div>

    <div class="card" v-if="!loading">
      <!-- Filters -->
      <div v-if="checkins.length > 0" class="filters-container">
          <div class="filter-item search">
            <label>Búsqueda de Asistencia</label>
            <div class="search-wrapper">
               <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
               <input v-model="search" type="text" placeholder="Nombre de participante o evento..." />
            </div>
          </div>
          <div class="filter-group">
            <div class="filter-item">
                <label>Tipo Acción</label>
                <div class="custom-select-wrapper">
                  <select v-model="filterTipo">
                    <option value="">Todos</option>
                    <option value="ENTRADA">Entrada</option>
                    <option value="SALIDA">Salida</option>
                  </select>
                  <svg class="select-icon" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="6 9 12 15 18 9"></polyline></svg>
                </div>
            </div>
            <div class="filter-actions" v-if="search || filterTipo">
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
        v-if="processedCheckins.length > 0"
        :totalItems="processedCheckins.length" 
        v-model:itemsPerPage="itemsPerPage" 
        v-model:currentPage="currentPage" 
      />
      <div v-if="checkins.length > 0" class="table-responsive mobile-cards">
        <table class="styled-table">
          <thead>
            <tr>
              <th>Participante</th>
              <th>Evento</th>
              <th>Fecha/Hora</th>
              <th>Tipo</th>
              <th>Método</th>
              <th v-if="authStore.hasPermission('CHECKIN:DELETE_ALL')" style="text-align: right">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="processedCheckins.length === 0">
              <td colspan="6" style="text-align: center; padding: 3rem; color: var(--text-secondary);">
                <div style="display: flex; flex-direction: column; align-items: center; gap: 1rem;">
                  <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" style="opacity: 0.3"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
                  <p style="font-weight: 600; font-size: 1.1rem;">No se encontraron coincidencias</p>
                  <p style="font-size: 0.9rem; margin-top: -0.5rem;">Prueba con otros términos de búsqueda.</p>
                  <button class="btn-sm-outline" @click="search = ''" style="margin-top: 0.5rem;">Limpiar Búsqueda</button>
                </div>
              </td>
            </tr>
            <tr v-else v-for="c in paginatedCheckins" :key="c.idCheckIn">
              <td data-label="Participante">{{ c.participanteNombre }}</td>
              <td data-label="Evento">{{ c.eventoNombre }}</td>
              <td data-label="Fecha">{{ formatDateTime(c.fechaHoraCheckIn) }}</td>
              <td data-label="Tipo">
                <span :class="['action-pill', c.tipoAccion?.toLowerCase()]">{{ c.tipoAccion || 'ENTRADA' }}</span>
              </td>
               <td data-label="Método">
                 <div class="method-container">
                    <span class="method-badge">{{ c.metodoCheckIn }}</span>
                    <div v-if="c.metodoCheckIn === 'QR'" class="qr-preview-tooltip">
                        <div class="tooltip-arrow"></div>
                        <div class="tooltip-content">
                            <p class="tooltip-title">QR Evento</p>
                            <img :src="`https://api.qrserver.com/v1/create-qr-code/?size=65x65&data=EVENTO:${c.eventoId}`" alt="QR Evento" />
                            <small>ID: {{ c.eventoId }}</small>
                        </div>
                    </div>
                </div>
               </td>
              <td v-if="authStore.hasPermission('CHECKIN:DELETE_ALL')" data-label="Acciones" style="text-align: right">
                <button v-if="authStore.hasPermission('CHECKIN:DELETE_ALL')" class="icon-btn delete" @click="deleteCheckIn(c.idCheckIn)" title="Eliminar">
                   <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <TablePagination 
        v-if="checkins.length > 0"
        :totalItems="checkins.length" 
        v-model:itemsPerPage="itemsPerPage" 
        v-model:currentPage="currentPage" 
      />

       <EmptyState 
         v-if="checkins.length === 0" 
         title="No hay asistencias registradas" 
         description="Aún no se han registrado ingresos ni salidas de participantes."
       >
         <template #actions>
            <button class="btn-premium-outline" @click="openScannerModal">Escanear QR</button>
         </template>
       </EmptyState>
     </div>

    <!-- Manual Form Modal -->
    <BaseModal 
        :show="showForm" 
        title="Registro de Asistencia Manual"
        @close="showForm = false"
    >
        <div class="manual-checkin-wrapper">
          <div class="mode-selector-manual">
             <button :class="{ active: form.tipoAccion === 'ENTRADA' }" @click="form.tipoAccion = 'ENTRADA'">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"></path><polyline points="10 17 15 12 10 7"></polyline><line x1="15" y1="12" x2="3" y2="12"></line></svg>
                Entrada
             </button>
             <button :class="{ active: form.tipoAccion === 'SALIDA' }" @click="form.tipoAccion = 'SALIDA'">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
                Salida
             </button>
          </div>

          <form id="checkInForm" @submit.prevent="submitForm" class="form-grid">
             <div class="form-group full-width">
               <label>Evento Activo</label>
               <div class="custom-select-wrapper modern">
                 <select v-model="selectedEventoId" @change="onEventoSelect" required>
                   <option :value="null" disabled>Seleccione un evento</option>
                   <option v-for="e in eventos" :key="e.idEvento" :value="e.idEvento">
                     {{ e.nombre }}
                   </option>
                 </select>
                 <svg class="select-icon" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="6 9 12 15 18 9"></polyline></svg>
               </div>
             </div>
  
             <div class="form-group full-width animate-fade-in" v-if="selectedEventoId">
               <label>Participante Registrado</label>
               <div class="custom-select-wrapper modern">
                 <select v-model="form.participacionId" required>
                   <option :value="null" disabled>
                      {{ participacionesEvento.length === 0 ? 'No hay participantes pendientes' : 'Seleccione participante' }}
                   </option>
                   <option v-for="p in participacionesEvento" :key="p.idParticipacion" :value="p.idParticipacion">
                     {{ p.participanteNombre }} ({{ p.participanteDocumento }})
                   </option>
                 </select>
                 <svg class="select-icon" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="6 9 12 15 18 9"></polyline></svg>
               </div>
               <p v-if="participacionesEvento.length === 0" class="helper-text error">
                  Todos los inscritos ya están registrados en este evento.
               </p>
             </div>
          </form>
        </div>
        
        <template #footer>
            <button type="button" class="btn-secondary" @click="showForm = false">Cancelar</button>
            <button type="submit" form="checkInForm" class="btn-primary" :disabled="!form.participacionId">
               Registrar {{ form.tipoAccion === 'ENTRADA' ? 'Entrada' : 'Salida' }}
            </button>
        </template>
    </BaseModal>


    <!-- QR Scanner Modal -->
    <BaseModal
        :show="showScanner"
        title="Escáner Inteligente"
        maxWidth="600px"
        @close="closeScanner"
    >
        <div class="scanner-container-modal">
            <div class="scanner-config">
                <div class="form-group">
                   <label>Evento de Control</label>
                   <div class="custom-select-wrapper modern">
                     <select v-model="selectedEventoId" @change="onScannerEventSelect">
                       <option :value="null" disabled>Seleccione evento para control</option>
                       <option v-for="e in eventos" :key="e.idEvento" :value="e.idEvento">
                         {{ e.nombre }}
                       </option>
                     </select>
                     <svg class="select-icon" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="6 9 12 15 18 9"></polyline></svg>
                   </div>
                </div>

                <div class="mode-selector-tabs" v-if="selectedEventoId">
                   <button :class="{ active: scanMode === 'ENTRADA' }" @click="scanMode = 'ENTRADA'">
                      <div class="mode-dot entrada"></div>
                      Check-In (Entrada)
                   </button>
                   <button :class="{ active: scanMode === 'SALIDA' }" @click="scanMode = 'SALIDA'">
                      <div class="mode-dot salida"></div>
                      Check-Out (Salida)
                   </button>
                </div>
            </div>

            <div v-if="selectedEventoId" class="qr-preview-wrapper animate-scale-in">
               <div id="qr-reader" class="modern-qr-reader"></div>
               <div class="scanner-overlay-aim" v-if="!isProcessingFile">
                  <div class="aim-corner tl"></div>
                  <div class="aim-corner tr"></div>
                  <div class="aim-corner bl"></div>
                  <div class="aim-corner br"></div>
               </div>
               
               <div v-if="isProcessingFile" class="file-processing-overlay">
                  <LoadingSpinner message="Analizando imagen..." size="small" />
               </div>

               <p class="scanner-instruction">
                  {{ isProcessingFile ? 'Procesando archivo...' : `Posicione el código QR dentro del recuadro para registrar ${scanMode}` }}
               </p>
            </div>
            
            <div v-else class="scanner-empty">
                <div class="empty-icon-wrapper">
                   <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M3 7V5a2 2 0 0 1 2-2h2"></path><path d="M17 3h2a2 2 0 0 1 2 2v2"></path><path d="M21 17v2a2 2 0 0 1-2 2h-2"></path><path d="M7 21H5a2 2 0 0 1-2-2v-2"></path><polyline points="9 9 15 15"></polyline><polyline points="15 9 9 15"></polyline></svg>
                </div>
                <p>Seleccione un evento para activar el control</p>
            </div>

            <!-- File Upload Option -->
            <div class="file-upload-section" v-if="selectedEventoId">
                <input 
                    type="file" 
                    id="qr-file-input" 
                    accept="image/*" 
                    @change="handleFileUpload" 
                    style="display: none"
                    :disabled="isProcessingFile"
                />
                <button class="btn-file-upload" @click="triggerFileInput" :disabled="isProcessingFile">
                   <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:8px"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="17 8 12 3 7 8"></polyline><line x1="12" y1="3" x2="12" y2="15"></line></svg>
                   Subir código desde galería/archivo
                </button>
            </div>
        </div>
        
        <template #footer>
            <button class="btn-secondary" @click="closeScanner">Cerrar</button>
        </template>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onBeforeUnmount, computed, watch } from 'vue';
import api from '../services/api';
import { Html5Qrcode } from "html5-qrcode";
import LoadingSpinner from '../components/ui/LoadingSpinner.vue';
import PageHeader from '../components/layout/PageHeader.vue';
import TablePagination from '../components/ui/TablePagination.vue';
import EmptyState from '../components/ui/EmptyState.vue';
import BaseModal from '../components/modals/BaseModal.vue';
import { useAuthStore } from '../stores/auth';
import { useNotificationStore } from '../stores/notifications';
import { useConfirmStore } from '../stores/confirm';

const authStore = useAuthStore();
const notificationStore = useNotificationStore();
const confirmStore = useConfirmStore();

const checkins = ref([]);
const eventos = ref([]);
const participacionesEvento = ref([]);
const loading = ref(false);
const error = ref('');
const showForm = ref(false);

const form = ref({ participacionId: null, metodoCheckIn: 'MANUAL', ipCheckIn: '127.0.0.1', tipoAccion: 'ENTRADA' });
const selectedEventoId = ref(null);

const openCreateModal = () => {
    resetForm();
    showForm.value = true;
};

// Pagination & Search State
const currentPage = ref(1);
const itemsPerPage = ref(10);
const search = ref('');
const filterTipo = ref('');

const resetFilters = () => {
    search.value = '';
    filterTipo.value = '';
};

const processedCheckins = computed(() => {
  if (!checkins.value) return [];
  let result = [...checkins.value];

  if (search.value) {
    const q = search.value.toLowerCase();
    result = result.filter(c => 
      (c.participanteNombre && c.participanteNombre.toLowerCase().includes(q)) ||
      (c.eventoNombre && c.eventoNombre.toLowerCase().includes(q))
    );
  }

  if (filterTipo.value) {
    result = result.filter(c => c.tipoAccion === filterTipo.value);
  }

  return result;
});

const totalPages = computed(() => Math.ceil(processedCheckins.value.length / itemsPerPage.value));
const paginatedCheckins = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value;
    return processedCheckins.value.slice(start, start + itemsPerPage.value);
});

// Scanner State
const showScanner = ref(false);
const scanMode = ref('ENTRADA'); // 'ENTRADA' or 'SALIDA'
const isProcessingFile = ref(false);
let scanner = null;
let pollingInterval = null;

const fetchCheckIns = async (showLoader = false) => {
  if (showLoader) loading.value = true;
  try {
    // Para polling, quizás solo necesitamos los checkins, no los eventos siempre
    // Pero fetchCheckIns actualiza ambos. Optimización: separar si es pesado.
    const [checkRes, eventRes] = await Promise.all([
        api.get('/checkins'),
        api.get('/eventos')
    ]);
    
    // Map CheckInResponse to View format
    checkins.value = checkRes.data.map(c => ({
        idCheckIn: c.idCheckIn,
        participanteNombre: c.participanteNombre,
        eventoNombre: c.eventoNombre,
        eventoId: c.eventoId,
        fechaHoraCheckIn: c.fechaHoraCheckIn,
        tipoAccion: c.tipoAccion,
        metodoCheckIn: c.metodoCheckIn
    }));
    eventos.value = eventRes.data;
    // Don't reset page on polling
    if (showLoader) currentPage.value = 1; 
  } catch (err) {
    if (showLoader) notificationStore.showError('Error al cargar datos: ' + (err.response?.data?.message || err.message));
    console.error(err);
  } finally {
    if (showLoader) loading.value = false;
  }
};

const onEventoSelect = async () => {
    if (!selectedEventoId.value) return;
    try {
        const res = await api.get(`/participaciones/evento/${selectedEventoId.value}`);
        
        const isOut = form.value.tipoAccion === 'SALIDA';

        participacionesEvento.value = res.data.map(p => ({
            idParticipacion: p.idParticipacion, 
            participanteId: p.participanteDocumento,
            participanteNombre: p.participanteNombre,
            participanteDocumento: p.participanteDocumento,
            checkedIn: p.hasActiveCheckIn || false
        })).filter(p => isOut ? p.checkedIn : !p.checkedIn);
    } catch (err) {
        console.error(err);
        participacionesEvento.value = [];
    }
}

// Watch for mode changes in manual form to refetch participations
watch(() => form.value.tipoAccion, () => {
    if (selectedEventoId.value) onEventoSelect();
});

const resetForm = () => {
  form.value = { participacionId: null, tipoAccion: 'ENTRADA', metodoCheckIn: 'MANUAL', ipCheckIn: '127.0.0.1' };
  selectedEventoId.value = null;
  participacionesEvento.value = [];
};

const resetQrForm = () => {
    qrCode.value = '';
}

const deleteCheckIn = async (id) => {
  const confirmed = await confirmStore.ask({
      title: 'Eliminar Registro',
      message: '¿Estás seguro de eliminar este registro de asistencia?',
      confirmText: 'Eliminar',
      type: 'danger'
  });

  if (!confirmed) return;

  try {
    await api.delete(`/checkins/${id}`);
    notificationStore.showSuccess('Registro eliminado correctamente');
    fetchCheckIns();
  } catch (err) {
    notificationStore.showError('Error al eliminar: ' + (err.response?.data?.message || err.message));
  }
};

const submitForm = async () => {
  try {
     const selectedP = participacionesEvento.value.find(p => p.idParticipacion === form.value.participacionId);
     if (!selectedP) return;

    await api.post('/checkins', { 
        participacionId: form.value.participacionId,
        metodoCheckIn: 'MANUAL',
        ipCheckIn: '127.0.0.1',
        tipoAccion: form.value.tipoAccion
    });
    
    showForm.value = false;
    notificationStore.showSuccess('Asistencia manual registrada correctamente');
    fetchCheckIns();
  } catch (err) {
    const errorMsg = err.response?.data || err.message;
    notificationStore.showError('Error: ' + errorMsg);
  }
};

// Scanner Logic
const openScannerModal = () => {
    showScanner.value = true;
    selectedEventoId.value = null; 
};

const initScanner = async () => {
    if (scanner) return;
    
    await nextTick();
    
    if (!document.getElementById("qr-reader")) return;

    try {
        scanner = new Html5Qrcode("qr-reader");
        const config = { fps: 15, qrbox: { width: 250, height: 250 } };
        
        await scanner.start(
            { facingMode: "environment" },
            config,
            onScanSuccess
        );
    } catch (e) {
        console.error("Scanner init error", e);
        notificationStore.showError("No se pudo acceder a la cámara.");
    }
};

const onScannerEventSelect = async () => {
     if (selectedEventoId.value) {
         await nextTick();
         initScanner();
     }
};

const closeScanner = async () => {
    isProcessingFile.value = false;
    if (scanner) {
        try {
            if (scanner.isScanning) {
                await scanner.stop();
            }
        } catch (e) {
            console.warn("Error stopping scanner", e);
        }
        scanner = null;
    }
    showScanner.value = false;
};

// File Upload Scanning
const triggerFileInput = () => {
    document.getElementById('qr-file-input').click();
};

const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    if (!selectedEventoId.value) {
        notificationStore.showWarning("Primero selecciona un evento.");
        return;
    }

    isProcessingFile.value = true;
    
    // Create temporary scanner if not exists for file processing
    let tempScanner = scanner;
    let createdTemp = false;
    
    if (!tempScanner) {
        tempScanner = new Html5Qrcode("qr-reader");
        createdTemp = true;
    }

    try {
        const decodedText = await tempScanner.scanFile(file, true);
        await onScanSuccess(decodedText);
    } catch (err) {
        console.error("File scanning error:", err);
        notificationStore.showError("No se encontró ningún código QR válido en la imagen.");
    } finally {
        isProcessingFile.value = false;
        event.target.value = ''; // Reset input
        if (createdTemp) {
            // No need to clear if we want to keep the element for future camera use, 
            // but Html5Qrcode might need cleanup.
        }
    }
};

const onScanSuccess = async (decodedText, decodedResult) => {
    /* Removing event QR handling from check-in as requested */
    /* if (decodedText.startsWith('EVENTO:')) { ... } */


    if (!selectedEventoId.value) {
         notificationStore.showWarning("Primero selecciona un evento.");
         return;
    }

    let pId = decodedText;
    if (decodedText.startsWith('PARTICIPANTE:')) {
        pId = decodedText.split(':')[1];
    } 

    console.log('Procesando check-in:', { pId, eventoId: selectedEventoId.value });

    try {
        // Use the correct endpoint based on mode
        const endpoint = scanMode.value === 'ENTRADA' ? '/checkins/scan' : '/checkins/scan-out';
        const response = await api.post(endpoint, {
            participanteId: pId,
            eventoId: selectedEventoId.value
        });
        
        console.log(`${scanMode.value} exitoso:`, response.data);
        notificationStore.showSuccess(`${scanMode.value} exitoso: ${pId}`);
        fetchCheckIns();
        
        // Try to pause/resume scanner (only works if actively scanning)
        if(html5QrcodeScanner) {
            try {
                html5QrcodeScanner.pause();
                setTimeout(() => {
                    try {
                        html5QrcodeScanner.resume();
                    } catch (e) {
                        // Scanner might have been closed or stopped
                        console.log('Could not resume scanner:', e.message);
                    }
                }, 1500);
            } catch (e) {
                // Scanner is not in scanning state (e.g., using file upload)
                console.log('Scanner not actively scanning, skipping pause');
            }
        }
        
    } catch (err) {
        console.error('Error completo:', err);
        console.error('Error response:', err.response);
        
        let errorMsg = 'Error desconocido';
        if (err.response) {
             // Backend sometimes returns plain string in body for bad requests
            if (typeof err.response.data === 'string') {
                errorMsg = err.response.data || 'Respuesta vacía del servidor';
            } else if (err.response.data?.message) {
                errorMsg = err.response.data.message;
            } else {
                errorMsg = `Status ${err.response.status} - ${JSON.stringify(err.response.data)}`;
            }
        } else if (err.message) {
            errorMsg = err.message;
        }
        notificationStore.showError(`Error: ${errorMsg}`);
    }
};

const formatDateTime = (dateStr) => {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleString('es-ES', {
        day: '2-digit', 
        month: 'short', 
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
};

const onScanFailure = (error) => {
    // Silent
};

onMounted(() => {
    fetchCheckIns(true);
    pollingInterval = setInterval(() => {
        fetchCheckIns(false);
    }, 5000);
});

onBeforeUnmount(async () => {
    if (pollingInterval) clearInterval(pollingInterval);
    if (scanner && scanner.isScanning) {
        await scanner.stop();
    }
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

.btn-scan {
    background: var(--primary-gradient);
    color: white;
    padding: 0.75rem 1.5rem;
    border-radius: 12px;
    font-weight: 700;
    display: flex;
    align-items: center;
    border: none;
    cursor: pointer;
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
    transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.btn-scan:hover {
    transform: translateY(-3px) scale(1.02);
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.btn-scan:active {
    transform: translateY(-1px);
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

.scanner-container-modal {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
}

.scanner-config {
    background: #f8fafc;
    padding: 1.25rem;
    border-radius: 16px;
    border: 1px solid #e2e8f0;
}

.mode-selector-tabs {
    display: flex;
    background: #e2e8f0;
    padding: 4px;
    border-radius: 12px;
    margin-top: 1rem;
}

.mode-selector-tabs button {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 0.75rem;
    border: none;
    background: transparent;
    color: #64748b;
    font-weight: 700;
    font-size: 0.85rem;
    cursor: pointer;
    border-radius: 10px;
    transition: all 0.2s;
}

.mode-selector-tabs button.active {
    background: white;
    color: #1a202c;
    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.mode-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
}

.mode-dot.entrada { background: #22c55e; }
.mode-dot.salida { background: #ef4444; }

.qr-preview-wrapper {
    position: relative;
    border-radius: 20px;
    overflow: hidden;
    background: #000;
    box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}

.modern-qr-reader {
    width: 100% !important;
    min-height: 350px;
    background: #000;
}

.modern-qr-reader video {
    width: 100% !important;
    height: 100% !important;
    object-fit: cover !important;
}

.scanner-overlay-aim {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 250px;
    height: 250px;
    pointer-events: none;
}

.aim-corner {
    position: absolute;
    width: 30px;
    height: 30px;
    border: 4px solid var(--primary-color);
}

.aim-corner.tl { top: 0; left: 0; border-right: none; border-bottom: none; border-radius: 8px 0 0 0; }
.aim-corner.tr { top: 0; right: 0; border-left: none; border-bottom: none; border-radius: 0 8px 0 0; }
.aim-corner.bl { bottom: 0; left: 0; border-right: none; border-top: none; border-radius: 0 0 0 8px; }
.aim-corner.br { bottom: 0; right: 0; border-left: none; border-top: none; border-radius: 0 0 8px 0; }

.scanner-instruction {
    position: absolute;
    bottom: 20px;
    left: 0;
    right: 0;
    text-align: center;
    color: white;
    font-size: 0.85rem;
    font-weight: 600;
    text-shadow: 0 2px 4px rgba(0,0,0,0.5);
    background: rgba(0,0,0,0.4);
    padding: 8px;
    margin: 0;
}

.scanner-empty {
    padding: 4rem 2rem;
    text-align: center;
    background: #f8fafc;
    border-radius: 20px;
    border: 2px dashed #cbd5e1;
    color: #64748b;
}

.empty-icon-wrapper {
    margin-bottom: 1rem;
    color: #94a3b8;
}

.manual-checkin-wrapper {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
}

.mode-selector-manual {
    display: flex;
    gap: 1rem;
}

.mode-selector-manual button {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    padding: 1rem;
    border: 2px solid #e2e8f0;
    border-radius: 16px;
    background: white;
    color: #64748b;
    font-weight: 700;
    cursor: pointer;
    transition: all 0.3s;
}

.mode-selector-manual button.active {
    border-color: var(--primary-color);
    background: #f0f4ff;
    color: var(--primary-color);
}

.custom-select-wrapper.modern select {
    padding-right: 2.5rem;
    font-weight: 600;
}

.helper-text.error {
    color: #ef4444;
    font-weight: 700;
    margin-top: 0.5rem;
}

.action-pill {
    padding: 4px 10px;
    border-radius: 6px;
    font-size: 0.7rem;
    font-weight: 800;
    letter-spacing: 0.025em;
}

.action-pill.entrada {
    background: #dcfce7;
    color: #166534;
}

.action-pill.salida {
    background: #fee2e2;
    color: #991b1b;
}

.method-container {
    position: relative;
    display: inline-block;
}

.qr-preview-tooltip {
    position: absolute;
    right: calc(100% + 15px);
    top: 50%;
    transform: translateY(-50%) translateX(10px);
    background: white;
    padding: 0.45rem;
    border-radius: 8px;
    box-shadow: -4px 4px 12px rgba(0,0,0,0.12);
    z-index: 100;
    opacity: 0;
    visibility: hidden;
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    width: 90px;
    text-align: center;
    border: 1px solid #e2e8f0;
}

.method-container:hover .qr-preview-tooltip {
    opacity: 1;
    visibility: visible;
    transform: translateY(-50%) translateX(0);
}

.tooltip-arrow {
    position: absolute;
    right: -6px;
    top: 50%;
    transform: translateY(-50%);
    width: 0;
    height: 0;
    border-top: 6px solid transparent;
    border-bottom: 6px solid transparent;
    border-left: 6px solid white;
}

.tooltip-title {
    font-size: 0.6rem;
    font-weight: 800;
    margin: 0 0 4px 0;
    color: #94a3b8;
    text-transform: uppercase;
}

.qr-preview-tooltip img {
    width: 100%;
    height: auto;
    border-radius: 6px;
    margin-bottom: 4px;
    border: 1px solid #f1f5f9;
}

.qr-preview-tooltip small {
    font-size: 0.6rem;
    color: #64748b;
    display: block;
}

.file-upload-section {
    margin-top: 1.5rem;
    padding-top: 1.5rem;
    border-top: 1px solid #e2e8f0;
    display: flex;
    justify-content: center;
}

.btn-file-upload {
    background: white;
    color: #64748b;
    border: 2px dashed #cbd5e1;
    padding: 0.75rem 1.5rem;
    border-radius: 12px;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    transition: all 0.2s;
    width: 100%;
    justify-content: center;
}

.btn-file-upload:hover:not(:disabled) {
    background: #f8fafc;
    border-color: var(--primary-color);
    color: var(--primary-color);
}

.btn-file-upload:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.file-processing-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.9);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 5;
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
</style>
