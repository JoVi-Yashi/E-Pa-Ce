<template>
  <div class="dashboard container animate-fade-in">
    <PageHeader 
      title="Dashboard Operativo" 
      :subtitle="`Hola, ${user?.nombre} • Actividad reciente y métricas generales del sistema`"
    >
      <template #actions>
        <button class="btn-glass-header" @click="logout" title="Cerrar sesión segura">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
          <span class="desktop-only" style="margin-left: 0.5rem; font-weight: 600;">Cerrar Sesión</span>
        </button>
      </template>
    </PageHeader>

    <div class="dashboard-grid">
      <!-- Quick Stats Row (Modern Cards) -->
      <div class="stats-overview slide-up">
        <div class="premium-stat-card bg-glass-blue">
            <div class="stat-icon-wrapper">
              <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2.5" fill="none"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
            </div>
            <div class="stat-info">
              <span class="stat-label">Eventos Activos</span>
              <span class="stat-value">{{ filteredEvents.length }}</span>
            </div>
            <div class="stat-decoration"></div>
        </div>
        <div class="premium-stat-card bg-glass-purple">
            <div class="stat-icon-wrapper">
              <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2.5" fill="none"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle></svg>
            </div>
            <div class="stat-info">
              <span class="stat-label">Ocupación Total</span>
              <span class="stat-value">{{ totalAforo }}</span>
            </div>
            <div class="stat-decoration"></div>
        </div>
        <div class="premium-stat-card bg-glass-green">
             <div class="stat-icon-wrapper">
              <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2.5" fill="none"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
            </div>
            <div class="stat-info">
              <span class="stat-label">Duración Prom.</span>
              <span class="stat-value">{{ avgDuration }}h</span>
            </div>
            <div class="stat-decoration"></div>
        </div>
      </div>

      <div class="main-layout">
        <!-- Filters Panel -->
        <aside class="sidebar slide-up-delayed">
          <div class="card filters-card">
            <div class="card-header">
              <h3 class="text-indigo-800">
                <svg viewBox="0 0 24 24" width="18" height="18" stroke="currentColor" stroke-width="2" fill="none" class="mr-2 text-indigo-600"><polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"></polygon></svg> 
                Filtros
              </h3>
              <transition name="fade-scale">
                <button v-if="hasActiveFilters" class="btn-reset-premium" @click="resetFilters" title="Limpiar filtros">
                  <svg viewBox="0 0 24 24" width="16" height="16" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="3 6 5 6 21 6"></polyline>
                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6"></path>
                    <path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                  </svg>
                </button>
              </transition>
            </div>
            
            <div class="filters-body">
              <div class="filter-group">
                <label>Rango de Fechas</label>
                <div class="date-inputs">
                   <input type="date" v-model="filters.startDate" class="form-control" />
                   <span class="separator">-</span>
                   <input type="date" v-model="filters.endDate" class="form-control" />
                </div>
              </div>
              
              <div class="filter-group">
                 <label>Modalidad</label>
                 <select v-model="filters.modalidadId" class="form-control custom-select">
                   <option :value="null">Todas las modalidades</option>
                   <option v-for="m in modalidades" :key="m.idModalidadEvento" :value="m.idModalidadEvento">
                     {{ m.nombreModalidadEvento }}
                   </option>
                 </select>
              </div>

              <div class="filter-group">
                 <label>Tipo de Evento</label>
                 <select v-model="filters.tipoId" class="form-control custom-select">
                   <option :value="null">Todos los tipos</option>
                   <option v-for="t in tipos" :key="t.idTipoEvento" :value="t.idTipoEvento">
                     {{ t.nombreTipoEvento }}
                   </option>
                 </select>
              </div>

              <div class="filter-group">
                 <label>Estado</label>
                 <select v-model="filters.estado" class="form-control custom-select">
                   <option :value="null">Cualquier estado</option>
                   <option value="PROGRAMADO">Programado</option>
                   <option value="EN_CURSO">En Curso</option>
                   <option value="FINALIZADO">Finalizado</option>
                   <option value="CANCELADO">Cancelado</option>
                 </select>
              </div>
            </div>
          </div>
        </aside>

        <!-- Main Content Area -->
        <section class="content-area slide-up-delayed">
           <div class="card results-card">
             <div class="card-header-simple">
               <h3>Eventos Registrados</h3>
               <span class="badge">{{ filteredEvents.length }} resultados</span>
             </div>
             
             <div class="table-container">
                <LoadingSpinner v-if="loading" />

               <table v-else class="styled-table">
                 <thead>
                   <tr>
                     <th>Evento</th>
                     <th>Fecha</th>
                     <th>Modalidad</th>
                     <th>Estado</th>
                     <th>Aforo</th>
                     <th></th>
                   </tr>
                 </thead>
                 <tbody>
                   <tr v-for="ev in filteredEvents" :key="ev.idEvento">
                     <td class="font-medium">{{ ev.nombre }}</td>
                     <td>{{ formatDate(ev.fechaInicio) }}</td>
                     <td><span class="tag">{{ ev.modalidadEventoNombre }}</span></td>
                     <td>
                        <span :class="['status-dot', ev.estado?.toLowerCase()]"></span>
                        <span class="status-text">{{ formatEstado(ev.estado) }}</span>
                     </td>
                     <td>
                        <div class="progress-bar-container" :title="`Ocupación: ${calculateOccupancy(ev)}%`">
                           <div class="progress-bar" :style="{ width: calculateOccupancy(ev) + '%' }"></div>
                        </div>
                        <span class="text-xs">{{ ev.aforoActual || 0 }} / {{ ev.aforoMaximo || '∞' }}</span>
                     </td>
                     <td>
                        <button class="action-btn-sm btn-indigo-soft" @click="viewEventLogs(ev)" title="Ver actividad del evento">
                           <svg viewBox="0 0 24 24" width="16" height="16" stroke="currentColor" stroke-width="2" fill="none"><circle cx="12" cy="12" r="1"></circle><circle cx="19" cy="12" r="1"></circle><circle cx="5" cy="12" r="1"></circle></svg>
                        </button>
                     </td>
                   </tr>
                   <tr v-if="filteredEvents.length === 0">
                     <td colspan="6" class="empty-cell">
                        <div class="empty-state">
                           <p>No se encontraron eventos con estos criterios.</p>
                           <button @click="resetFilters" class="clear-btn">Limpiar filtros</button>
                        </div>
                     </td>
                   </tr>
                 </tbody>
               </table>
             </div>
           </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';
import { storeToRefs } from 'pinia';
import PageHeader from '../components/layout/PageHeader.vue';
import BaseButton from '../components/ui/BaseButton.vue';
import LoadingSpinner from '../components/ui/LoadingSpinner.vue';
import api from '../services/api';

const authStore = useAuthStore();
const router = useRouter();
const { user } = storeToRefs(authStore);

const eventos = ref([]);
const modalidades = ref([]);
const tipos = ref([]);
const loading = ref(false);

const filters = ref({
  startDate: '',
  endDate: '',
  modalidadId: null,
  tipoId: null,
  estado: null
});

// Pagination State
const currentPage = ref(1);
const itemsPerPage = ref(10);

const totalPages = computed(() => Math.ceil(filteredEvents.value.length / itemsPerPage.value));
const paginatedFilteredEvents = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value;
    return filteredEvents.value.slice(start, start + itemsPerPage.value);
});

const logout = () => {
  authStore.logout();
  router.push('/');
};

let pollingInterval = null;

const fetchDashboardData = async (showLoader = false) => {
    if (showLoader) loading.value = true;
    try {
        const evRes = await api.get('/eventos');
        eventos.value = evRes.data;
    } catch (err) {
        console.error(err);
    } finally {
        if (showLoader) loading.value = false;
    }
};

const fetchInitialData = async () => {
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
        console.error(err);
    } finally {
        loading.value = false;
    }
};

const filteredEvents = computed(() => {
  return eventos.value.filter(ev => {
    // Date Filter
    if (filters.value.startDate && new Date(ev.fechaInicio) < new Date(filters.value.startDate)) return false;
    if (filters.value.endDate && new Date(ev.fechaInicio) > new Date(filters.value.endDate)) return false;
    
    // Select Filters
    if (filters.value.modalidadId && ev.modalidadEventoId !== filters.value.modalidadId) return false;
    if (filters.value.tipoId && ev.tipoEventoId !== filters.value.tipoId) return false;
    if (filters.value.estado && ev.estado !== filters.value.estado) return false;

    return true;
  });
});

const totalAforo = computed(() => {
   return filteredEvents.value.reduce((sum, ev) => sum + (ev.aforoActual || 0), 0);
});

const avgDuration = computed(() => {
   if (filteredEvents.value.length === 0) return 0;
   const sum = filteredEvents.value.reduce((s, ev) => s + (ev.duracionHoras || 0), 0);
   return (sum / filteredEvents.value.length).toFixed(1);
});

const resetFilters = () => {
  filters.value = {
    startDate: '',
    endDate: '',
    modalidadId: null,
    tipoId: null,
    estado: null
  };
};

const hasActiveFilters = computed(() => {
    const f = filters.value;
    return !!(f.startDate || f.endDate || f.modalidadId || f.tipoId || f.estado);
});

const formatDate = (dateString) => {
  if (!dateString) return '-';
  return new Date(dateString).toLocaleDateString('es-ES', {
      day: '2-digit', month: 'short', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
  });
};

const formatEstado = (estado) => {
    if (!estado) return '';
    return estado.replace(/_/g, ' ').toUpperCase();
};

const calculateOccupancy = (ev) => {
    if (!ev.aforoMaximo || ev.aforoMaximo <= 0) return (ev.aforoActual > 0 ? 100 : 0);
    return Math.min(100, ((ev.aforoActual || 0) / ev.aforoMaximo) * 100).toFixed(0);
};

const viewEventLogs = (ev) => {
    // Redirigir a auditoría con filtro de detalle (nombre del evento)
    router.push({ 
        path: '/auditoria', 
        query: { search: ev.nombre } 
    });
};

onMounted(() => {
    fetchInitialData();
    pollingInterval = setInterval(() => {
        fetchDashboardData();
    }, 5000);
});

onUnmounted(() => {
    if (pollingInterval) clearInterval(pollingInterval);
});
</script>

<style scoped>
/* Dashboard Layout */
.dashboard-grid {
  margin-top: 1rem;
}

/* Premium Stat Cards */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2.5rem;
}

.premium-stat-card {
  position: relative;
  border-radius: 24px;
  padding: 2rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.premium-stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 35px -10px rgba(0, 0, 0, 0.15);
}

.stat-icon-wrapper {
  width: 64px;
  height: 64px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(4px);
  z-index: 1;
  box-shadow: inset 0 0 10px rgba(255,255,255,0.2);
}

.stat-info {
  display: flex;
  flex-direction: column;
  z-index: 1;
}

.stat-label {
  font-size: 0.9rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  opacity: 0.9;
  margin-bottom: 0.25rem;
}

.stat-value {
  font-size: 2.5rem;
  font-weight: 800;
  line-height: 1;
}

/* Background Variations */
.bg-glass-blue {
    background: linear-gradient(135deg, #6366f1 0%, #4338ca 100%);
    color: white;
}
.bg-glass-purple {
    background: linear-gradient(135deg, #a855f7 0%, #7e22ce 100%);
    color: white;
}
.bg-glass-green {
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    color: white;
}

/* Decorative element for cards */
.stat-decoration {
    position: absolute;
    top: -20px;
    right: -20px;
    width: 120px;
    height: 120px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    pointer-events: none;
}

/* Sidebar & Results Main Area */
.main-layout {
  display: grid;
  grid-template-columns: 1fr;
  gap: 2rem;
}

@media (min-width: 1024px) {
  .main-layout {
    grid-template-columns: 320px 1fr;
  }
}

/* Filters Sidebar Premium */
.filters-card {
    background: white;
    border-radius: 20px !important;
    overflow: hidden;
    position: sticky;
    top: 100px;
    box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05) !important;
}

.card-header {
    background: #f8fafc;
    padding: 1.5rem;
    border-bottom: 1px solid #edf2f7;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.card-header h3 {
    font-size: 1.1rem;
    color: #1a202c;
    margin: 0;
    gap: 10px;
}

.filters-body {
    padding: 1.5rem;
}

.filter-group {
    margin-bottom: 1.5rem;
}

.filter-group label {
    display: block;
    font-size: 0.8rem;
    font-weight: 700;
    color: #64748b;
    text-transform: uppercase;
    margin-bottom: 0.75rem;
}

.form-control {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    background: #f9fbff;
    transition: all 0.2s;
}

.form-control:focus {
    background: white;
    border-color: #6366f1;
    box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
    outline: none;
}

.date-inputs {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
}

/* Results Content Area */
.results-card {
    background: white;
    border-radius: 24px !important;
    box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05) !important;
    overflow: hidden;
}

.card-header-simple {
    padding: 1.5rem 2rem;
    background: #f8fafc;
    border-bottom: 1px solid #edf2f7;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.badge {
    background: #e0e7ff;
    color: #4338ca;
    padding: 0.4rem 1rem;
    border-radius: 50px;
    font-weight: 700;
    font-size: 0.8rem;
    border: 1px solid #c7d2fe;
}

/* Updated Action Button Style */
.action-btn-sm.btn-indigo-soft {
    background: transparent;
    border: 1px solid #c7d2fe;
    color: #4f46e5;
    padding: 6px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s;
    display: flex;
    align-items: center;
    justify-content: center;
}

.action-btn-sm.btn-indigo-soft:hover {
    background: #e0e7ff;
    color: #4338ca;
    border-color: #a5b4fc;
    transform: translateY(-1px);
}

/* Table Enhancements */
.table-container {
    padding: 1rem;
}

.tag {
    background: #f0f4ff;
    color: #5a67d8;
    padding: 4px 10px;
    border-radius: 8px;
    font-size: 0.8rem;
    font-weight: 600;
    border: 1px solid #e0e7ff;
}

.status-dot {
    width: 10px;
    height: 10px;
}

/* Mobile Adjustments */
@media (max-width: 768px) {
    .premium-stat-card {
        padding: 1.5rem;
    }
    .stat-value {
        font-size: 2rem;
    }
    .stats-overview {
        grid-template-columns: 1fr;
    }
}

/* Standard Premium Base Styles */
.styled-table tr:hover {
    background-color: #f8faff;
}

.desktop-only {
    display: inline;
}

@media (max-width: 1100px) {
    .desktop-only {
        display: none;
    }
}

.btn-reset-premium {
    background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
    color: white;
    border: none;
    width: 42px;
    height: 32px;
    border-radius: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    box-shadow: 0 4px 10px rgba(99, 102, 241, 0.3);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-reset-premium:hover {
    transform: translateY(-2px) scale(1.05);
    box-shadow: 0 6px 15px rgba(99, 102, 241, 0.4);
    background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
}

.fade-scale-enter-active,
.fade-scale-leave-active {
  transition: all 0.3s ease;
}

.fade-scale-enter-from,
.fade-scale-leave-to {
  opacity: 0;
  transform: scale(0.8);
}
</style>