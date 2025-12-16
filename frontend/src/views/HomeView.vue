<template>
  <div class="home animate-fade-in">
    <div class="hero-section">
      <div class="hero-content">
        <div class="glass-header">
           <h1 class="hero-title">Bienvenido a E-Pa-Ce</h1>
        </div>
        <p class="hero-subtitle">Sistema integral de gestión de eventos y participaciones</p>
        <BaseButton 
          v-if="!isAuthenticated" 
          variant="primary" 
          size="large" 
          @click="goToLogin"
          class="hero-cta"
        >
          Iniciar Sesión
        </BaseButton>
      </div>
    </div>
    
    <div class="features-section">
      <div class="section-header">
         <h2>Características Principales</h2>
         <div class="divider"></div>
      </div>
      <div class="features-grid">
        <div class="feature-card glass-card">
          <div class="icon-circle">
            <svg viewBox="0 0 24 24" width="32" height="32" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
          </div>
          <h3>Gestión de Eventos</h3>
          <p>Crea, organiza y gestiona eventos de todo tipo con facilidad y control total.</p>
        </div>
        
        <div class="feature-card glass-card">
          <div class="icon-circle">
            <svg viewBox="0 0 24 24" width="32" height="32" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
          </div>
          <h3>Control de Participantes</h3>
          <p>Base de datos centralizada para registrar y seguir a todos los asistentes.</p>
        </div>

        <div class="feature-card glass-card">
          <div class="icon-circle">
             <svg viewBox="0 0 24 24" width="32" height="32" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg>
          </div>
          <h3>Check-in Rápido</h3>
          <p>Verifica la asistencia mediante códigos QR o registro manual eficiente.</p>
        </div>

        <div class="feature-card glass-card">
          <div class="icon-circle">
            <svg viewBox="0 0 24 24" width="32" height="32" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
          </div>
          <h3>Certificados</h3>
          <p>Generación automática de certificados para los asistentes validados.</p>
        </div>
      </div>
    </div>
    
    <div class="stats-section">
      <div class="stats-grid">
        <div v-for="stat in stats" :key="stat.label" class="stat-item">
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, watch } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';
import BaseButton from '../components/BaseButton.vue';
import api from '../services/api';

const authStore = useAuthStore();
const router = useRouter();

const isAuthenticated = computed(() => authStore.isAuthenticated);
const stats = ref([
  { value: '...', label: 'Eventos Creados' },
  { value: '...', label: 'Participantes' },
  { value: '...', label: 'Tipos de Evento' },
  { value: '...', label: 'Certificaciones' }
]);

const goToLogin = () => {
  router.push('/login');
};

const fetchStats = async () => {
  try {
     // Fetch data in parallel
     const [eventsRes, usersRes, typesRes, certsRes] = await Promise.allSettled([
        api.get('/eventos'),
        api.get('/participantes'),
        api.get('/tipos-evento'),
        api.get('/certificaciones')
     ]);

     stats.value = [
       { value: eventsRes.status === 'fulfilled' ? eventsRes.value.data.length : '-', label: 'Eventos Creados' },
       { value: usersRes.status === 'fulfilled' ? usersRes.value.data.length : '-', label: 'Participantes' },
       { value: typesRes.status === 'fulfilled' ? typesRes.value.data.length : '-', label: 'Tipos de Evento' },
       { value: certsRes.status === 'fulfilled' ? certsRes.value.data.length : '-', label: 'Certificaciones' }
     ];
  } catch (err) {
     console.error("Error fetching stats", err);
  }
};

onMounted(() => {
   if (isAuthenticated.value) {
     fetchStats();
   }
});

watch(isAuthenticated, (newVal) => {
  if (newVal) fetchStats();
});
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.hero-section {
  text-align: center;
  padding: 4rem 1rem;
  margin-bottom: 4rem;
  position: relative;
}

.hero-title {
  font-size: 3rem;
  margin: 0;
  font-weight: 800;
  letter-spacing: -1px;
}

.hero-subtitle {
  font-size: 1.25rem;
  margin-top: 1.5rem;
  margin-bottom: 2rem;
  color: #4a5568;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.hero-cta {
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

.section-header {
  text-align: center;
  margin-bottom: 3rem;
}

.section-header h2 {
  font-size: 2rem;
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.divider {
  height: 4px;
  width: 60px;
  background: var(--primary-gradient);
  margin: 0 auto;
  border-radius: 2px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  margin-bottom: 5rem;
}

.glass-card {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  padding: 2rem;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0,0,0,0.05);
}

.glass-card:hover {
  transform: translateY(-5px);
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1), 0 10px 10px -5px rgba(0,0,0,0.04);
}

.icon-circle {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #e0e7ff 0%, #f3e8ff 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1.5rem auto;
  color: #667eea;
}

.glass-card h3 {
  margin-bottom: 0.5rem;
  color: #2d3748;
}

.glass-card p {
  color: #718096;
  font-size: 0.95rem;
  line-height: 1.6;
}

.stats-section {
  background: white;
  border-radius: 20px;
  padding: 3rem;
  box-shadow: 0 4px 6px rgba(0,0,0,0.05);
  border: 1px solid rgba(0,0,0,0.05);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
  text-align: center;
}

.stat-value {
  font-size: 2.5rem;
  font-weight: 800;
  background: var(--primary-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 0.5rem;
}

.stat-label {
  font-weight: 600;
  color: #718096;
  text-transform: uppercase;
  font-size: 0.85rem;
  letter-spacing: 0.05em;
}

@media screen and (max-width: 768px) {
  .hero-title {
    font-size: 2rem;
  }
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>