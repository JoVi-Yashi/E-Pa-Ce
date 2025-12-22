<template>
  <div class="container animate-fade-in">
    <PageHeader 
      title="Participaciones" 
      subtitle="Registra y controla la asistencia de los participantes a los diferentes eventos"
    >
      <template #actions v-if="authStore.hasPermission('PARTICIPACION:CREATE')">
        <button v-if="authStore.hasPermission('CHECKIN:QR')" class="btn-glass-header" @click="openScannerModal">
           <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path><circle cx="12" cy="13" r="4"></circle></svg>
           <span>Escanear Evento</span>
        </button>
        <button v-if="authStore.hasPermission('PARTICIPACION:CREATE')" class="btn-premium-header" @click="openCreateModal">
           <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="8.5" cy="7" r="4"></circle><line x1="20" y1="8" x2="20" y2="14"></line><line x1="23" y1="11" x2="17" y2="11"></line></svg>
           <span>Registro Manual</span>
        </button>
      </template>
    </PageHeader>

    <LoadingSpinner v-if="loading" />
    <div v-if="error" class="error-msg">{{ error }}</div>

    <div class="card" v-if="!loading">
      
      <!-- Filters -->
      <div v-if="participaciones.length > 0" class="filters-container">
        <div class="filter-item search">
          <label>Búsqueda</label>
            <div class="search-wrapper">
               <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
               <input v-model="search" type="text" placeholder="Evento, participante o documento..." />
            </div>
        </div>
        <div class="filter-group">
          <div class="filter-item">
              <label>Método</label>
              <div class="custom-select-wrapper">
                <select v-model="filterMethod">
                  <option value="">Todos</option>
                  <option value="MANUAL">Manual</option>
                  <option value="QR">QR / Escáner</option>
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

      <TablePagination 
        v-if="processedParticipaciones.length > 0"
        :totalItems="processedParticipaciones.length" 
        v-model:itemsPerPage="itemsPerPage" 
        v-model:currentPage="currentPage" 
      />
      <div v-if="participaciones.length > 0" class="table-responsive mobile-cards">
        <table class="styled-table">
          <thead>
            <tr>
              <th @click="toggleSort('eventoNombre')" class="sortable">
                  Evento
                  <span class="sort-indicator" v-if="sortBy === 'eventoNombre'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
              </th>
              <th @click="toggleSort('participanteNombre')" class="sortable">
                  Participante
                  <span class="sort-indicator" v-if="sortBy === 'participanteNombre'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
              </th>
              <th @click="toggleSort('participanteDocumento')" class="sortable">
                  Documento
                  <span class="sort-indicator" v-if="sortBy === 'participanteDocumento'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
              </th>
              <th @click="toggleSort('fechaInscripcion')" class="sortable">
                  Fecha Inscripción
                  <span class="sort-indicator" v-if="sortBy === 'fechaInscripcion'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
              </th>
              <th @click="toggleSort('metodoInscripcion')" class="sortable">
                  Método
                  <span class="sort-indicator" v-if="sortBy === 'metodoInscripcion'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
              </th>
              <th v-if="authStore.hasPermission('PARTICIPACION:DELETE_ALL') || authStore.hasPermission('PARTICIPACION:DELETE_OWN')" style="text-align: right">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="processedParticipaciones.length === 0">
              <td :colspan="authStore.hasPermission('PARTICIPACION:DELETE_ALL') || authStore.hasPermission('PARTICIPACION:DELETE_OWN') ? 6 : 5" style="text-align: center; padding: 3rem; color: var(--text-secondary);">
                <div style="display: flex; flex-direction: column; align-items: center; gap: 1rem;">
                  <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" style="opacity: 0.3"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
                  <p style="font-weight: 600; font-size: 1.1rem;">No se encontraron coincidencias</p>
                  <p style="font-size: 0.9rem; margin-top: -0.5rem;">Intenta ajustando el término de búsqueda o el filtro de método.</p>
                  <button class="btn-sm-outline" @click="resetFilters" style="margin-top: 0.5rem;">Limpiar Filtros</button>
                </div>
              </td>
            </tr>
            <tr v-else v-for="p in paginatedParticipaciones" :key="p.idParticipacion">
              <td data-label="Evento">
                 <strong>{{ p.eventoNombre }}</strong>
                 <br><small class="text-muted">ID: {{ p.eventoId }}</small>
              </td>
              <td data-label="Participante">{{ p.participanteNombre }}</td>
              <td data-label="Documento">{{ p.participanteDocumento }}</td>
              <td data-label="Fecha">{{ formatDateTime(p.fechaInscripcion) }}</td>
              <td data-label="Método">
                <div class="method-container">
                    <span :class="['method-badge', p.metodoInscripcion?.toLowerCase()]">
                      {{ p.metodoInscripcion || 'MANUAL' }}
                    </span>
                    <div v-if="p.metodoInscripcion === 'QR'" class="qr-preview-tooltip">
                        <div class="tooltip-arrow"></div>
                        <div class="tooltip-content">
                            <p class="tooltip-title">QR Evento</p>
                            <img :src="`https://api.qrserver.com/v1/create-qr-code/?size=65x65&data=EVENTO:${p.eventoId}`" alt="QR Evento" />
                            <small>ID: {{ p.eventoId }}</small>
                        </div>
                    </div>
                </div>
              </td>
              <td v-if="authStore.hasPermission('PARTICIPACION:DELETE_ALL') || authStore.hasPermission('PARTICIPACION:DELETE_OWN')" data-label="Acciones" style="text-align: right">
                <div class="action-buttons">
                  <button v-if="authStore.can('DELETE', 'PARTICIPACION', p.registradoPorId)" class="icon-btn delete" @click="deleteParticipacion(p.idParticipacion)" title="Eliminar">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <TablePagination 
        v-if="processedParticipaciones.length > 0"
        :totalItems="processedParticipaciones.length" 
        v-model:itemsPerPage="itemsPerPage" 
        v-model:currentPage="currentPage" 
      />

      <div v-if="participaciones.length === 0" class="empty-state">
        No hay participaciones registradas.
      </div>
    </div>

    <!-- Form Modal -->
    <BaseModal 
        :show="showForm" 
        title="Nueva Participación"
        @close="showForm = false"
    >
        <form id="participationForm" @submit.prevent="submitForm" class="form-grid">
           <div class="form-group full-width">
             <label>Nombre del Evento</label>
             <input 
                 v-model="form.eventoNombre" 
                 list="list-eventos" 
                 type="text" 
                 required 
                 placeholder="Escriba el nombre del evento..." 
                 autocomplete="off"
                 class="modern-input"
                 :class="{
                     'input-success': matchedEvento, 
                     'input-warning': !matchedEvento && partialEventos.length > 0 && form.eventoNombre,
                     'input-error': form.eventoNombre && partialEventos.length === 0
                 }"
             />
             <datalist id="list-eventos">
                 <option v-for="e in eventos" :key="e.idEvento" :value="e.nombre">
                    {{ e.lugar ? e.lugar + ' - ' : '' }}{{ e.fecha ? formatSimpleDate(e.fecha) : '' }}
                 </option>
             </datalist>
             
             <div class="match-indicator" v-if="form.eventoNombre">
                <small v-if="matchedEvento" class="match-feedback success">
                   ✓ Evento encontrado: {{ matchedEvento.nombre }}
                </small>
                <small v-else-if="partialEventos.length > 0" class="match-feedback warning">
                   ⚠ {{ partialEventos.length }} coincidencias posibles
                </small>
                <small v-else class="match-feedback error">
                   ✗ No se encontró ningún evento
                </small>
             </div>
           </div>
  
           <div class="form-group full-width">
             <label>Nombre del Participante</label>
             <input 
                 v-model="form.participanteNombre" 
                 list="list-participantes" 
                 type="text" 
                 placeholder="Escriba el nombre del participante..." 
                 autocomplete="off"
                 class="modern-input"
                 :class="{
                     'input-success': matchedParticipante, 
                     'input-warning': !matchedParticipante && partialParticipantes.length > 0 && form.participanteNombre,
                     'input-error': form.participanteNombre && partialParticipantes.length === 0
                 }"
             />
              <datalist id="list-participantes">
                 <option v-for="p in participantesDB" :key="p.documentoIdentidad" :value="p.nombre">
                    {{ p.apellido }} - {{ p.documentoIdentidad }}
                 </option>
             </datalist>
             
             <div class="match-indicator" v-if="form.participanteNombre">
                <small v-if="matchedParticipante" class="match-feedback success">
                   ✓ Seleccionado: {{ matchedParticipante.nombre }} {{ matchedParticipante.apellido }}
                </small>
                <small v-else-if="partialParticipantes.length > 0" class="match-feedback warning">
                   ⚠ {{ partialParticipantes.length }} coincidencias
                </small>
                <small v-else class="match-feedback error">
                   ✗ Participante no encontrado
                </small>
             </div>
           </div>
  
           <div class="form-group full-width" v-if="!matchedParticipante">
             <label>Documento de Identidad (Opcional si usó nombre)</label>
             <input 
                 v-model="form.participanteDocumento" 
                 type="text" 
                 class="modern-input"
                 placeholder="O ingrese el documento si no está en la lista" 
             />
           </div>
        </form>
        <template #footer>
            <button type="button" class="btn-secondary" @click="showForm = false">Cancelar</button>
            <button type="submit" form="participationForm" class="btn-primary" :disabled="!matchedEvento || (!matchedParticipante && !form.participanteDocumento)">
               Confirmar Registro
            </button>
        </template>
    </BaseModal>
  
    <!-- QR Scanner Modal -->
    <BaseModal
        :show="showScanner"
        title="Escanear QR de Evento"
        maxWidth="600px"
        @close="closeScanner"
    >
        <div class="scanner-container-modal">
            <div class="qr-preview-wrapper animate-scale-in">
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
                  {{ isProcessingFile ? 'Procesando archivo...' : 'Posicione el código QR del evento para unirse' }}
               </p>
            </div>
            
            <div class="file-upload-section">
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
                   Subir QR desde galería/archivo
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
import { ref, onMounted, computed, nextTick, onBeforeUnmount, watch } from 'vue';
import api from '../services/api';
import { Html5Qrcode } from "html5-qrcode";
import LoadingSpinner from '../components/ui/LoadingSpinner.vue';
import PageHeader from '../components/layout/PageHeader.vue';
import TablePagination from '../components/ui/TablePagination.vue';
import BaseModal from '../components/modals/BaseModal.vue';
import { useAuthStore } from '../stores/auth';
import { useNotificationStore } from '../stores/notifications';
import { useConfirmStore } from '../stores/confirm';

const authStore = useAuthStore();
const notificationStore = useNotificationStore();
const confirmStore = useConfirmStore();

const participaciones = ref([]);
const eventos = ref([]);
const participantesDB = ref([]); 
const loading = ref(false);
const error = ref('');
const showForm = ref(false);
const showScanner = ref(false);
const isProcessingFile = ref(false);
let scanner = null;

const form = ref({
  eventoNombre: '',
  participanteNombre: '',
  participanteDocumento: '',
  metodoInscripcion: 'MANUAL'
});

// Search and Filter State
const search = ref('');
const filterMethod = ref('');
const sortBy = ref('fechaInscripcion');
const sortDir = ref('desc');

// Pagination State
const currentPage = ref(1);
const itemsPerPage = ref(10);

const hasActiveFilters = computed(() => {
    return search.value !== '' || filterMethod.value !== '';
});

const resetFilters = () => {
    search.value = '';
    filterMethod.value = '';
};

// Reset to first page when filters change
watch([search, filterMethod], () => {
  currentPage.value = 1;
});

const processedParticipaciones = computed(() => {
    let result = [...participaciones.value];

    // Search filter
    if (search.value) {
        const query = search.value.toLowerCase();
        result = result.filter(p => 
            (p.eventoNombre && p.eventoNombre.toLowerCase().includes(query)) || 
            (p.participanteNombre && p.participanteNombre.toLowerCase().includes(query)) || 
            (p.participanteDocumento && p.participanteDocumento.toString().includes(query))
        );
    }

    // Method filter
    if (filterMethod.value) {
        result = result.filter(p => p.metodoInscripcion === filterMethod.value);
    }

    // Sorting
    result.sort((a, b) => {
        let valA = a[sortBy.value];
        let valB = b[sortBy.value];

        // Handle string sorting explicitly
        if (typeof valA === 'string') valA = valA.toLowerCase();
        if (typeof valB === 'string') valB = valB.toLowerCase();

        // Handle nulls
        if (valA === null || valA === undefined) valA = '';
        if (valB === null || valB === undefined) valB = '';

        if (valA < valB) return sortDir.value === 'asc' ? -1 : 1;
        if (valA > valB) return sortDir.value === 'asc' ? 1 : -1;
        return 0;
    });

    return result;
});

const totalPages = computed(() => Math.ceil(processedParticipaciones.value.length / itemsPerPage.value));
const paginatedParticipaciones = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value;
    return processedParticipaciones.value.slice(start, start + itemsPerPage.value);
});

const toggleSort = (field) => {
    if (sortBy.value === field) {
        sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc';
    } else {
        sortBy.value = field;
        sortDir.value = 'asc';
    }
};

const matchedEvento = computed(() => {
    return eventos.value.find(e => e.nombre.toLowerCase() === form.value.eventoNombre.toLowerCase());
});

const partialEventos = computed(() => {
    if (!form.value.eventoNombre) return [];
    return eventos.value.filter(e => e.nombre.toLowerCase().includes(form.value.eventoNombre.toLowerCase()));
});

const matchedParticipante = computed(() => {
    return participantesDB.value.find(u => u.nombre.toLowerCase() === form.value.participanteNombre.toLowerCase());
});

const partialParticipantes = computed(() => {
    if (!form.value.participanteNombre) return [];
    return participantesDB.value.filter(u => u.nombre.toLowerCase().includes(form.value.participanteNombre.toLowerCase()));
});

const findEventoIdByName = (name) => {
    const ev = eventos.value.find(e => e.nombre.toLowerCase() === name.toLowerCase());
    return ev ? ev.idEvento : null;
};

const findParticipanteDocByName = (name) => {
    const p = participantesDB.value.find(u => u.nombre.toLowerCase() === name.toLowerCase());
    return p ? p.documentoIdentidad : null;
};

const fetchParticipaciones = async () => {
  loading.value = true;
  try {
    const [resPart, resEvt, resUsers] = await Promise.all([
        api.get('/participaciones'),
        api.get('/eventos'),
        api.get('/participantes')
    ]);
    participaciones.value = resPart.data;
    eventos.value = resEvt.data;
    participantesDB.value = resUsers.data;
    currentPage.value = 1; // RESET
  } catch (err) {
    notificationStore.showError('Error cargando datos: ' + (err.response?.data?.message || err.message));
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  form.value = { eventoNombre: '', participanteNombre: '', participanteDocumento: '', metodoInscripcion: 'MANUAL' };
};

const openCreateModal = () => {
  resetForm();
  showForm.value = true;
};

const openScannerModal = () => {
    showScanner.value = true;
    nextTick(() => { initScanner(); });
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

    isProcessingFile.value = true;
    
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
        event.target.value = '';
    }
};

const onScanSuccess = async (decodedText) => {
    if (decodedText.startsWith('EVENTO:')) {
        const evId = decodedText.split(':')[1];
        const ev = eventos.value.find(e => e.idEvento == evId);
        
        if (ev) {
            await closeScanner();
            form.value.eventoNombre = ev.nombre;
            const userStr = localStorage.getItem('user');
            if (userStr) {
                const user = JSON.parse(userStr);
                const p = participantesDB.value.find(u => u.email === user.email);
                if (p) {
                    const confirmed = await confirmStore.ask({
                        title: 'Auto-Inscripción QR',
                        message: `¿Deseas registrarte en el evento "${ev.nombre}"?`,
                        confirmText: 'Registrarme'
                    });
                    
                    if (confirmed) {
                        form.value.participanteNombre = p.nombre + ' ' + p.apellido;
                        form.value.participanteDocumento = p.documentoIdentidad.toString();
                        form.value.metodoInscripcion = 'QR';
                        await submitForm();
                        return;
                    }
                }
            }
        }
        notificationStore.showInfo(`Evento detectado: "${ev.nombre}". Complete el registro.`);
        form.value.metodoInscripcion = 'QR';
        showForm.value = true;
    }
}

const deleteParticipacion = async (id) => {
  const confirmed = await confirmStore.ask({
      title: 'Eliminar Participación',
      message: '¿Estás seguro de eliminar esta participación?',
      type: 'danger'
  });
  if (!confirmed) return;
  try {
    await api.delete(`/participaciones/${id}`);
    notificationStore.showSuccess('Participación eliminada correctamente');
    fetchParticipaciones();
  } catch (err) {
    notificationStore.showError('Error eliminando: ' + (err.response?.data?.message || err.message));
  }
};

const submitForm = async () => {
  const eventoId = findEventoIdByName(form.value.eventoNombre);
  if (!eventoId) {
      alert(`No se encontró el evento "${form.value.eventoNombre}"`);
      return;
  }

  let doc = form.value.participanteDocumento;
  if (!doc && form.value.participanteNombre) {
      doc = findParticipanteDocByName(form.value.participanteNombre);
  }

  if (!doc) {
      alert('Especifique un participante válido.');
      return;
  }

  try {
    await api.post('/participaciones', { 
        eventoId, 
        participanteDocumento: doc,
        metodoInscripcion: form.value.metodoInscripcion
    });
    notificationStore.showSuccess('Participación registrada correctamente');
    showForm.value = false;
    fetchParticipaciones();
  } catch (err) {
    notificationStore.showError('Error guardando: ' + (err.response?.data?.message || err.message));
  }
};

const formatDateTime = (dateStr) => {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleString('es-ES', {
        day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit'
    });
};

const formatSimpleDate = (dateStr) => {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleDateString('es-ES', {
        day: '2-digit', month: '2-digit'
    });
};


onMounted(fetchParticipaciones);
onBeforeUnmount(async () => {
    if (scanner && scanner.isScanning) {
        await scanner.stop();
    }
});
</script>

<style scoped>
.header-actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; }
.actions { display: flex; gap: 1rem; align-items: center; }
.back-link { color: var(--text-secondary); font-weight: 500; }
.back-link:hover { color: var(--text-primary); }
.text-muted { color: #718096; font-size: 0.85rem; }
.icon-btn { background: transparent; color: #718096; padding: 0.5rem; margin-left: 0.5rem; box-shadow: none; }
.icon-btn:hover { background: #f7fafc; transform: translateY(-2px); }
.icon-btn.delete:hover { color: #e53e3e; }

.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; backdrop-filter: blur(4px); }
.modal-content { background: white; border-radius: 16px; width: 90%; max-width: 500px; box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1); }
.modal-header { padding: 1.5rem; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center; }
.modal-body { padding: 1.5rem; }
.form-group label { display: block; font-size: 0.875rem; font-weight: 600; margin-bottom: 0.5rem; color: #4a5568; }
.modal-footer { padding: 1.5rem; border-top: 1px solid #e2e8f0; display: flex; justify-content: flex-end; gap: 1rem; }
.btn-secondary { background: white; color: #4a5568; border: 1px solid #cbd5e0; }
.close-btn { background: transparent; color: #a0aec0; font-size: 1.5rem; padding: 0; box-shadow: none; }
.close-btn:hover { color: #4a5568; }

.match-feedback { display: block; font-size: 0.8rem; margin-top: 4px; font-weight: 500; }
.match-feedback.success { color: #16a34a; }
.match-feedback.warning { color: #d97706; }
.match-feedback.error { color: #dc2626; }

.input-success { border-color: #86efac !important; background: #f0fdf4 !important; }
.input-warning { border-color: #fcd34d !important; background: #fffbeb !important; }
.input-error { border-color: #fca5a5 !important; background: #fef2f2 !important; }

.method-badge {
    padding: 0.25rem 0.6rem;
    border-radius: 6px;
    font-size: 0.75rem;
    font-weight: 700;
    text-transform: uppercase;
    background: #edf2f7;
    color: #4a5568;
}

.method-badge.qr {
    background: #f0fdf4;
    color: #16a34a;
    border: 1px solid #bbf7d0;
}

.action-pill.salida {
    background: #fee2e2;
    color: #991b1b;
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

.scanner-container-modal {
    display: flex;
    flex-direction: column;
}

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

.match-indicator {
    height: 20px;
    margin-top: 5px;
}

.modern-input {
    width: 100%;
    padding: 0.75rem 1rem;
    border: 1px solid #e2e8f0;
    border-radius: 10px;
    font-size: 0.95rem;
    transition: all 0.2s;
}

.modern-input:focus {
    border-color: var(--primary-color);
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    outline: none;
}
.method-badge.manual {
    background: #fffbeb;
    color: #d97706;
    border: 1px solid #fef3c7;
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
    border-radius: 8px;
    margin-bottom: 6px;
    border: 1px solid #f1f5f9;
}

.qr-preview-tooltip small {
    font-size: 0.65rem;
    color: #64748b;
    display: block;
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
    margin: 0;
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
