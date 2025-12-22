<template>
  <div class="container fade-in">
    <PageHeader 
      title="Certificaciones" 
      subtitle="Genera y administra los certificados de participación para los asistentes"
    >
      <template #actions v-if="authStore.hasPermission('CERTIFICADO:EMITIR')">
        <button class="btn-premium-header" @click="openEmitModal">
          <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="2.5" fill="none"><path d="M12 5v14M5 12h14"/></svg>
          <span>Emitir Certificación</span>
        </button>
      </template>
    </PageHeader>

    <!-- Content -->
    <div class="card slide-up-delayed">
       <!-- Loading State -->
       <div v-if="loading" class="loading-state">
         <div class="spinner"></div>
         <p>Cargando certificaciones...</p>
       </div>
       
       <template v-else>
         <!-- Search & Filters -->
        <div v-if="certificaciones.length > 0" class="filters-container">
          <div class="filter-item search">
            <label>Búsqueda</label>
            <div class="search-wrapper">
               <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
               <input v-model="search" type="text" placeholder="Código, nombre o evento..." />
            </div>
          </div>
          <div class="filter-item">
            <label>Estado</label>
            <select v-model="filterStatus">
              <option value="">Cualquier estado</option>
              <option value="true">Emitido</option>
              <option value="false">Pendiente</option>
            </select>
          </div>
          <div class="filter-actions" v-if="hasActiveFilters">
             <button class="btn-text" @click="resetFilters">Limpiar</button>
          </div>
        </div>

       <!-- Empty State -->
       <div v-if="certificaciones.length === 0" class="empty-state">
         <div class="empty-icon">
            <svg viewBox="0 0 24 24" width="48" height="48" stroke="currentColor" stroke-width="1.5" fill="none"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
         </div>
         <h3>No hay certificaciones</h3>
         <p>Comienza emitiendo una nueva certificación para un participante.</p>
         <BaseButton variant="outline" @click="openEmitModal">Emitir Ahora</BaseButton>
       </div>

       <!-- Table -->
       <div v-else class="table-responsive">
        <table class="styled-table">
          <thead>
            <tr>
              <th @click="toggleSort('codigoUnicoAPI')" class="sortable">
                  Código
                  <span class="sort-indicator" v-if="sortBy === 'codigoUnicoAPI'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
              </th>
              <th @click="toggleSort('fechaEmision')" class="sortable">
                  Fecha Emisión
                  <span class="sort-indicator" v-if="sortBy === 'fechaEmision'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
              </th>
              <th @click="toggleSort('participanteNombre')" class="sortable">
                  Participante
                  <span class="sort-indicator" v-if="sortBy === 'participanteNombre'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
              </th>
              <th @click="toggleSort('eventoNombre')" class="sortable">
                  Evento
                  <span class="sort-indicator" v-if="sortBy === 'eventoNombre'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
              </th>
              <th @click="toggleSort('emitido')" class="sortable">
                  Estado
                  <span class="sort-indicator" v-if="sortBy === 'emitido'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
              </th>
              <th v-if="authStore.hasPermission('CERTIFICADO:DELETE_ALL') || certificaciones.some(c => c.rutaPDF)" style="text-align: right">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="processedCertificaciones.length === 0">
              <td colspan="6" style="text-align: center; padding: 3rem; color: var(--text-secondary);">
                <div style="display: flex; flex-direction: column; align-items: center; gap: 1rem;">
                  <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" style="opacity: 0.3"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
                  <p style="font-weight: 600; font-size: 1.1rem;">No se encontraron coincidencias</p>
                  <p style="font-size: 0.9rem; margin-top: -0.5rem;">Intenta con otros criterios o limpia los filtros.</p>
                  <button class="btn-sm-outline" @click="resetFilters" style="margin-top: 0.5rem;">Limpiar Filtros</button>
                </div>
              </td>
            </tr>
            <tr v-else v-for="c in paginatedCertificaciones" :key="c.idCertificacion">
              <td><span class="code-badge">{{ c.codigoUnicoAPI }}</span></td>
              <td>{{ formatDate(c.fechaEmision) }}</td>
              <td>
                <div class="user-cell">
                   <div class="avatar-sm" v-if="!c.fotoPerfil">{{ getInitials(c.participanteNombre) }}</div>
                   <img v-else :src="c.fotoPerfil" :alt="c.participanteNombre" class="avatar-sm avatar-img" @error="handleImageError($event)" />
                   <span>{{ c.participanteNombre || 'Desconocido' }}</span>
                </div>
              </td>
              <td>{{ c.eventoNombre }}</td>
              <td>
                <span :class="['status-badge', c.emitido ? 'status-success' : 'status-pending']">
                  {{ c.emitido ? 'Emitido' : 'Pendiente' }}
                </span>
              </td>
              <td v-if="authStore.hasPermission('CERTIFICADO:DELETE_ALL') || certificaciones.some(c => c.rutaPDF)" class="actions-cell">
                 <TableActionButton 
                  v-if="c.rutaPDF" 
                  type="edit"
                  title="Descargar PDF"
                  @click="downloadPDF(c.rutaPDF, c.codigoUnicoAPI)"
                >
                  <svg viewBox="0 0 24 24" width="18" height="18" stroke="currentColor" stroke-width="2" fill="none"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
                </TableActionButton>
                
                <TableActionButton 
                  v-if="authStore.hasPermission('CERTIFICADO:DELETE_ALL')"
                  type="delete"
                  title="Eliminar"
                  @click="deleteCert(c.idCertificacion)"
                >
                  <svg viewBox="0 0 24 24" width="18" height="18" stroke="currentColor" stroke-width="2" fill="none"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg>
                </TableActionButton>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination Controls -->
      <div v-if="certificaciones.length > itemsPerPage" class="pagination">
        <button 
          class="pagination-btn" 
          :disabled="currentPage === 1" 
          @click="currentPage--"
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"></polyline></svg>
          Anterior
        </button>
        
        <div class="pagination-info">
          Página {{ currentPage }} de {{ totalPages }}
        </div>

        <button 
          class="pagination-btn" 
          :disabled="currentPage === totalPages" 
          @click="currentPage++"
        >
          Siguiente
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"></polyline></svg>
        </button>
      </div>
      </template>
    </div>

    <!-- Modal Form -->
    <div v-if="showForm" class="modal-overlay fade-in">
      <div class="modal-content slide-up">
        <div class="modal-header">
           <h3>Emitir Nueva Certificación</h3>
           <button class="close-btn" @click="showForm = false">&times;</button>
        </div>
        
        <form @submit.prevent="submitForm" class="modal-body">
           <div style="margin-bottom: 1rem;">
             <label style="display:block; font-weight:600; margin-bottom:0.5rem; color:#4a5568;">Evento</label>
             <input 
                v-model="form.eventoNombre" 
                list="list-eventos-cert" 
                class="base-input"
                placeholder="Busque el evento..." 
                required
                :class="{
                    'input-success': matchedEvento, 
                    'input-warning': !matchedEvento && partialEventos.length > 0 && form.eventoNombre,
                    'input-error': form.eventoNombre && partialEventos.length === 0
                }"
             />
             <datalist id="list-eventos-cert">
                 <option v-for="e in eventos" :key="e.idEvento" :value="e.nombre">{{ e.lugar }}</option>
             </datalist>
             <small v-if="matchedEvento" class="match-feedback success">
               Seleccionado: {{ matchedEvento.nombre }}
             </small>
             <small v-else-if="form.eventoNombre && partialEventos.length > 0" class="match-feedback warning">
               {{ partialEventos.length }} coincidencias: {{ partialEventos.slice(0, 3).map(e => e.nombre).join(', ') }}...
             </small>
             <small v-else-if="form.eventoNombre" class="match-feedback error">
               Evento no encontrado
             </small>
           </div>

           <div style="margin-bottom: 1rem;">
             <label style="display:block; font-weight:600; margin-bottom:0.5rem; color:#4a5568;">Participante</label>
             <input 
                v-model="form.participanteNombre" 
                list="list-participantes-cert" 
                class="base-input"
                placeholder="Busque el participante..." 
                required
                :class="{
                    'input-success': matchedParticipante, 
                    'input-warning': !matchedParticipante && partialParticipantes.length > 0 && form.participanteNombre,
                    'input-error': form.participanteNombre && partialParticipantes.length === 0
                }"
             />
             <datalist id="list-participantes-cert">
                <option v-for="p in participantesDB" :key="p.documentoIdentidad" :value="p.nombre">{{ p.nombre }} {{ p.apellido }} - {{ p.documentoIdentidad }}</option>
            </datalist>
            
            <small v-if="matchedParticipante" class="match-feedback success">
               Seleccionado: {{ matchedParticipante.nombre }} {{ matchedParticipante.apellido }} (Doc: {{ matchedParticipante.documentoIdentidad }})
            </small>
            <small v-else-if="form.participanteNombre && partialParticipantes.length > 0" class="match-feedback warning">
               {{ partialParticipantes.length }} coincidencias: {{ partialParticipantes.slice(0, 3).map(p => p.nombre + ' ' + p.apellido).join(', ') }}{{ partialParticipantes.length > 3 ? '...' : '' }}
            </small>
            <small v-else-if="form.participanteNombre" class="match-feedback error">
               Participante no encontrado.
            </small>
           </div>

           <div class="info-box" v-if="matchedEvento && matchedParticipante">
               <div v-if="checkParticipationMatch" class="success-msg">
                   Participación confirmada. Listo para emitir.
               </div>
               <div v-else class="error-msg">
                   Este usuario NO tiene una participación registrada en este evento.
               </div>
           </div>
           
           <!-- Error Alert -->
           <div v-if="error" class="error-alert">
             <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="2" fill="none"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
             {{ error }}
           </div>

           <div class="modal-actions">
              <BaseButton type="button" variant="outline" @click="showForm = false">Cancelar</BaseButton>
              <BaseButton type="submit" variant="primary" :disabled="submitting">
                 {{ submitting ? 'Emitiendo...' : 'Emitir Certificado' }}
              </BaseButton>
           </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import api from '../services/api';
import PageHeader from '../components/layout/PageHeader.vue';
import BaseButton from '../components/ui/BaseButton.vue';
import TableActionButton from '../components/ui/TableActionButton.vue';
import BaseInput from '../components/ui/BaseInput.vue';
import { useAuthStore } from '../stores/auth';
import { useNotificationStore } from '../stores/notifications';
import { useConfirmStore } from '../stores/confirm';

const authStore = useAuthStore();
const notificationStore = useNotificationStore();
const confirmStore = useConfirmStore();

const certificaciones = ref([]);
const eventos = ref([]);
const participantesDB = ref([]);
const participacionesDB = ref([]);

// Pagination State
const currentPage = ref(1);
const itemsPerPage = ref(10);

// Search and Filter State
const search = ref('');
const filterStatus = ref('');
const sortBy = ref('fechaEmision');
const sortDir = ref('desc');

const hasActiveFilters = computed(() => {
    return search.value !== '' || filterStatus.value !== '';
});

const resetFilters = () => {
    search.value = '';
    filterStatus.value = '';
};

const processedCertificaciones = computed(() => {
    let result = [...certificaciones.value];

    // Search filter
    if (search.value) {
        const query = search.value.toLowerCase();
        result = result.filter(c => 
            (c.codigoUnicoAPI && c.codigoUnicoAPI.toLowerCase().includes(query)) || 
            (c.participanteNombre && c.participanteNombre.toLowerCase().includes(query)) || 
            (c.eventoNombre && c.eventoNombre.toLowerCase().includes(query))
        );
    }

    // Status filter
    if (filterStatus.value !== '') {
        const isEmitido = filterStatus.value === 'true';
        result = result.filter(c => c.emitido === isEmitido);
    }

    // Sorting
    result.sort((a, b) => {
        let valA = a[sortBy.value];
        let valB = b[sortBy.value];

        if (typeof valA === 'string') valA = valA.toLowerCase();
        if (typeof valB === 'string') valB = valB.toLowerCase();

        if (valA === null || valA === undefined) valA = '';
        if (valB === null || valB === undefined) valB = '';

        if (valA < valB) return sortDir.value === 'asc' ? -1 : 1;
        if (valA > valB) return sortDir.value === 'asc' ? 1 : -1;
        return 0;
    });

    return result;
});

const totalPages = computed(() => Math.ceil(processedCertificaciones.value.length / itemsPerPage.value));
const paginatedCertificaciones = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  return processedCertificaciones.value.slice(start, start + itemsPerPage.value);
});

const toggleSort = (field) => {
    if (sortBy.value === field) {
        sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc';
    } else {
        sortBy.value = field;
        sortDir.value = 'asc';
    }
};

const loading = ref(false);
const submitting = ref(false);
const error = ref('');
const showForm = ref(false);
const form = ref({ eventoNombre: '', participanteNombre: '' });

const matchedEvento = computed(() => eventos.value.find(e => e.nombre.toLowerCase() === form.value.eventoNombre.toLowerCase()));
const partialEventos = computed(() => {
    if (!form.value.eventoNombre) return [];
    return eventos.value.filter(e => e.nombre.toLowerCase().includes(form.value.eventoNombre.toLowerCase()));
});

const matchedParticipante = computed(() => participantesDB.value.find(p => p.nombre.toLowerCase() === form.value.participanteNombre.toLowerCase()));
const partialParticipantes = computed(() => {
    if (!form.value.participanteNombre) return [];
    return participantesDB.value.filter(p => p.nombre.toLowerCase().includes(form.value.participanteNombre.toLowerCase()));
});

const checkParticipationMatch = computed(() => {
    if(!matchedEvento.value || !matchedParticipante.value) return false;
    return participacionesDB.value.some(p => 
        p.eventoId === matchedEvento.value.idEvento && 
        (Number(p.participanteDocumento) === Number(matchedParticipante.value.documentoIdentidad))
    );
});


const fetchCertificaciones = async () => {
  loading.value = true;
  try {
    const [resCerts, resEvt, resParts, resParticipaciones] = await Promise.all([
        api.get('/certificaciones'),
        api.get('/eventos'),
        api.get('/participantes'),
        api.get('/participaciones') // Need this mapping to find the ID
    ]);
    certificaciones.value = resCerts.data;
    eventos.value = resEvt.data;
    participantesDB.value = resParts.data; // Users
    participacionesDB.value = resParticipaciones.data; // The link table
    currentPage.value = 1; // RESET
  } catch (err) {
    notificationStore.showError('Error al cargar datos: ' + (err.response?.data?.message || err.message));
  } finally {
    loading.value = false;
  }
};

const openEmitModal = () => {
    form.value = { eventoNombre: '', participanteNombre: '' }; 
    error.value = '';
    showForm.value = true;
};

const deleteCert = async (id) => {
  const confirmed = await confirmStore.ask({
      title: 'Eliminar Certificación',
      message: '¿Estás seguro de eliminar esta certificación? Esta acción no se puede deshacer.',
      confirmText: 'Eliminar',
      type: 'danger'
  });

  if (!confirmed) return;

  try {
    await api.delete(`/certificaciones/${id}`);
    notificationStore.showSuccess('Certificación eliminada correctamente');
    fetchCertificaciones();
  } catch (err) {
    notificationStore.showError('Error eliminando: ' + (err.response?.data?.message || err.message));
  }
};

const submitForm = async () => {
  submitting.value = true;
  error.value = '';
  
  try {
    if (!matchedEvento.value || !matchedParticipante.value) {
        throw new Error("Debe seleccionar un evento y un participante válidos.");
    }

    const match = participacionesDB.value.find(p => 
        p.eventoId === matchedEvento.value.idEvento && 
        (Number(p.participanteDocumento) === Number(matchedParticipante.value.documentoIdentidad))
    );

    if (!match) {
        throw new Error(`No se encontró participación para ${matchedParticipante.value.nombre} en el evento ${matchedEvento.value.nombre}. Asegúrese de que esté inscrito.`);
    }
    
    await api.post('/certificaciones/emitir', { participacionId: match.idParticipacion });
    showForm.value = false;
    notificationStore.showSuccess('Certificación emitida con éxito');
    fetchCertificaciones();
  } catch (err) {
     const resData = err.response?.data;
     if (typeof resData === 'string') {
         error.value = resData;
     } else if (resData?.message) {
         error.value = resData.message;
     } else {
         error.value = err.message || 'Error al emitir certificación';
     }
  } finally {
      submitting.value = false;
  }
};

// Utils
const formatDate = (dateString) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
};

const getInitials = (name) => {
    if (!name) return '?';
    return name.substring(0, 2).toUpperCase();
};

const downloadPDF = async (rutaPDF, codigo) => {
    try {
        // La ruta del PDF viene del backend, construimos la URL completa
        const baseURL = api.defaults.baseURL || 'http://localhost:8080/api';
        const pdfURL = `${baseURL}/certificaciones/descargar/${codigo}`;
        
        // Crear un elemento <a> temporal para forzar la descarga
        const link = document.createElement('a');
        link.href = pdfURL;
        link.download = `Certificado_${codigo}.pdf`;
        link.target = '_blank';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    } catch (err) {
        notificationStore.showError('Error al descargar el certificado. Por favor, intente nuevamente.');
    }
};

const handleImageError = (event) => {
  // Replace broken image with initials fallback
  event.target.style.display = 'none';
  const parent = event.target.parentElement;
  if (parent && !parent.querySelector('.avatar-sm:not(img)')) {
    const initialsDiv = document.createElement('div');
    initialsDiv.className = 'avatar-sm';
    initialsDiv.textContent = getInitials(event.target.alt);
    parent.insertBefore(initialsDiv, event.target);
  }
};

onMounted(fetchCertificaciones);
</script>

<style scoped>
/* Page Layout */
.actions-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 2rem;
}

/* Table Enhancements */
.code-badge {
    font-family: monospace;
    background: #f1f5f9;
    padding: 2px 6px;
    border-radius: 4px;
    color: #475569;
    border: 1px solid #e2e8f0;
}

.user-cell {
    display: flex;
    align-items: center;
    gap: 10px;
}

.avatar-sm {
    width: 32px;
    height: 32px;
    background: var(--primary-gradient);
    color: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.8rem;
    font-weight: bold;
}

.avatar-sm.avatar-img {
    object-fit: cover;
    background: #e2e8f0;
}

.status-badge {
    padding: 4px 10px;
    border-radius: 20px;
    font-size: 0.8rem;
    font-weight: 600;
}

.status-success { background: #dcfce7; color: #166534; }
.status-pending { background: #fee2e2; color: #991b1b; }

.actions-cell {
    display: flex;
    gap: 0.5rem;
    justify-content: flex-end;
}

/* Empty State */
.loading-state, .empty-state {
    text-align: center;
    padding: 4rem 2rem;
    color: #64748b;
}

.spinner {
    border: 3px solid #f3f3f3;
    border-top: 3px solid #667eea;
    border-radius: 50%;
    width: 30px;
    height: 30px;
    animation: spin 1s linear infinite;
    margin: 0 auto 1rem;
}

@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }

.empty-icon {
    color: #cbd5e1;
    margin-bottom: 1.5rem;
}

.empty-state h3 {
    margin-bottom: 0.5rem;
    color: #334155;
}

/* Modal Styling */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.modal-header {
    padding: 1.5rem;
    border-bottom: 1px solid #e2e8f0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #f8fafc;
}

.modal-header h3 { margin: 0; color: #1e293b; }

.close-btn {
    background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #64748b;
}

.modal-body {
    padding: 2rem;
}

.modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    margin-top: 2rem;
}

.error-alert {
    background: #fee2e2;
    color: #991b1b;
    padding: 0.75rem;
    border-radius: 8px;
    margin-top: 1rem;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.9rem;
}

/* Animations */
.slide-down { animation: slideDown 0.5s ease-out; }
.slide-up { animation: slideUp 0.5s ease-out; }
.slide-up-delayed { animation: slideUp 0.5s ease-out 0.1s backwards; }
.fade-in { animation: fadeIn 0.3s ease-out; }

@keyframes slideDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

.match-feedback {
    display: block;
    font-size: 0.8rem;
    margin-top: 4px;
    font-weight: 500;
}
.match-feedback.success { color: #16a34a; }
.match-feedback.warning { color: #d97706; }
.match-feedback.error { color: #dc2626; }

.input-success { border-color: #86efac !important; background: #f0fdf4 !important; }
.input-warning { border-color: #fcd34d !important; background: #fffbeb !important; }
.input-error { border-color: #fca5a5 !important; background: #fef2f2 !important; }

.info-box {
    margin-bottom: 1rem;
    padding: 0.5rem;
    border-radius: 8px;
    font-size: 0.9rem;
}
.success-msg { color: #166534; background: #dcfce7; padding: 0.5rem; border-radius: 4px;}
.error-msg { color: #991b1b; background: #fee2e2; padding: 0.5rem; border-radius: 4px; }

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

.filter-item label {
    font-size: 0.8rem;
    font-weight: 600;
    color: #718096;
    text-transform: uppercase;
}

.filter-item.search {
    flex: 1;
    min-width: 200px;
}

.search-wrapper {
    position: relative;
    display: flex;
    align-items: center;
}

.search-wrapper svg {
    position: absolute;
    left: 10px;
    color: #a0aec0;
}

.search-wrapper input {
    width: 100%;
    padding: 0.5rem 0.5rem 0.5rem 2.2rem;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    font-size: 0.9rem;
    transition: all 0.2s;
}

.search-wrapper input:focus {
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    outline: none;
}

.filter-item select {
    padding: 0.5rem;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    background: white;
    min-width: 150px;
    font-size: 0.9rem;
}

.btn-text {
    background: none;
    border: none;
    color: #667eea;
    font-weight: 600;
    cursor: pointer;
    text-decoration: underline;
    margin-bottom: 0.5rem; /* Align with inputs */
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
