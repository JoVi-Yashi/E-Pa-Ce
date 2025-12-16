<template>
  <div class="dashboard container animate-fade-in">
    <div class="header-section">
      <div class="glass-header">
        <h1>Dashboard Operativo</h1>
        <p class="subtitle">Bienvenido, {{ user?.nombre }} {{ user?.apellido }}</p>
      </div>
      <div class="top-actions">
         <BaseButton @click="logout" variant="secondary" size="small">Cerrar Sesión</BaseButton>
      </div>
    </div>

    <!-- Analytics Section -->
    <div class="dashboard-grid">
      <!-- Filters Panel -->
      <div class="card filters-panel">
        <div class="panel-header">
          <h3><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mr-2"><polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"></polygon></svg> Filtros Avanzados</h3>
          <button class="btn-link" @click="resetFilters">Limpiar</button>
        </div>
        
        <div class="filters-form">
          <div class="form-group">
            <label>Rango de Fechas</label>
            <div class="date-range">
               <input type="date" v-model="filters.startDate" class="form-control" placeholder="Desde" />
               <input type="date" v-model="filters.endDate" class="form-control" placeholder="Hasta" />
            </div>
          </div>
          
          <div class="form-group">
             <label>Modalidad</label>
             <select v-model="filters.modalidadId" class="form-control">
               <option :value="null">Todas</option>
               <option v-for="m in modalidades" :key="m.idModalidadEvento" :value="m.idModalidadEvento">
                 {{ m.nombreModalidadEvento }}
               </option>
             </select>
          </div>

          <div class="form-group">
             <label>Tipo de Evento</label>
             <select v-model="filters.tipoId" class="form-control">
               <option :value="null">Todos</option>
               <option v-for="t in tipos" :key="t.idTipoEvento" :value="t.idTipoEvento">
                 {{ t.nombreTipoEvento }}
               </option>
             </select>
          </div>

          <div class="form-group">
             <label>Estado</label>
             <select v-model="filters.estado" class="form-control">
               <option :value="null">Todos</option>
               <option value="PROGRAMADO">Programado</option>
               <option value="EN_CURSO">En Curso</option>
               <option value="FINALIZADO">Finalizado</option>
               <option value="CANCELADO">Cancelado</option>
             </select>
          </div>
        </div>
      </div>

      <!-- Main Content -->
      <div class="main-content">
         <!-- Quick Stats -->
         <div class="stats-overview">
            <div class="stat-card">
               <span class="stat-label">Eventos Filtrados</span>
               <span class="stat-value">{{ filteredEvents.length }}</span>
            </div>
            <div class="stat-card">
               <span class="stat-label">Aforo Total Planificado</span>
               <span class="stat-value">{{ totalAforo }}</span>
            </div>
            <div class="stat-card">
               <span class="stat-label">Duración Promedio</span>
               <span class="stat-value">{{ avgDuration }} h</span>
            </div>
         </div>

         <!-- Results Table -->
         <div class="card results-card">
           <h3>Resultados del Reporte</h3>
           <div class="table-responsive">
             <table class="styled-table">
               <thead>
                 <tr>
                   <th>Evento</th>
                   <th>Fecha Inicio</th>
                   <th>Modalidad</th>
                   <th>Estado</th>
                   <th>Aforo</th>
                 </tr>
               </thead>
               <tbody>
                 <tr v-for="ev in filteredEvents" :key="ev.idEvento">
                   <td>{{ ev.nombre }}</td>
                   <td>{{ formatDate(ev.fechaInicio) }}</td>
                   <td>{{ ev.modalidadEventoNombre }}</td>
                   <td><span :class="['status-badge', ev.estado.toLowerCase()]">{{ ev.estado }}</span></td>
                   <td>{{ ev.aforoMaximo }}</td>
                 </tr>
                 <tr v-if="filteredEvents.length === 0">
                   <td colspan="5" style="text-align: center; color: #718096; padding: 2rem;">
                     No se encontraron eventos con estos filtros.
                   </td>
                 </tr>
               </tbody>
             </table>
           </div>
         </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';
import { storeToRefs } from 'pinia';
import BaseButton from '../components/BaseButton.vue';
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

const logout = () => {
  authStore.logout();
  router.push('/');
};

const fetchData = async () => {
  loading.value = true;
  try {
    const [evRes, modRes, tipRes] = await Promise.all([
      api.get('/eventos'),
      api.get('/modalidades'),
      api.get('/tipos-evento')
    ]);
    eventos.value = evRes.data.map(e => ({
        ...e,
        // Backend might return object or ID, ensure mapping if needed. 
        // Based on previous EventoResponse: modalidadEventoNombre is present.
    }));
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
   return filteredEvents.value.reduce((sum, ev) => sum + (ev.aforoMaximo || 0), 0);
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

const formatDate = (dateString) => {
  if (!dateString) return '-';
  return new Date(dateString).toLocaleDateString();
};

onMounted(fetchData);
</script>

<style scoped>
.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.subtitle {
  color: #718096;
  margin-top: 0.5rem;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
}

@media (min-width: 992px) {
  .dashboard-grid {
    grid-template-columns: 300px 1fr;
  }
}

/* Filters Panel */
.filters-panel {
  padding: 1.5rem;
  background: white;
  height: fit-content;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e2e8f0;
}

.filters-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.form-group label {
  display: block;
  font-size: 0.85rem;
  font-weight: 600;
  color: #4a5568;
  margin-bottom: 0.5rem;
}

.form-control {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 0.9rem;
}

.date-range {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.5rem;
}

.btn-link {
  background: none;
  border: none;
  color: #667eea;
  cursor: pointer;
  font-size: 0.85rem;
  text-decoration: underline;
}

/* Stats Overview */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.stat-label {
  color: #718096;
  font-size: 0.9rem;
  font-weight: 500;
}

.stat-value {
  font-size: 2rem;
  font-weight: 800;
  color: #2d3748;
}

.results-card {
  padding: 0;
  overflow: hidden;
}

.results-card h3 {
  padding: 1.5rem;
  margin: 0;
  border-bottom: 1px solid #e2e8f0;
  background: #f8fafc;
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
.status-badge.cancelado { background: #fff5f5; color: #e53e3e; }
</style>