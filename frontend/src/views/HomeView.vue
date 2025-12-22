<template>
  <div class="home animate-fade-in">
    <!-- Hero Section -->
    <div class="hero-section">
      <div class="hero-content">
        <div class="glass-header slide-down">
           <h1 class="hero-title">Gestión de Eventos</h1>
        </div>
        <p class="hero-subtitle slide-up" v-if="!isAuthenticated">
          E-Pa-Ce es la solución integral para administrar tus eventos, 
          controlar asistencias y generar certificaciones de manera automatizada.
        </p>
        <p class="hero-subtitle slide-up" v-else>
          ¡Hola, <span class="user-name">{{ authStore.user?.nombre }}</span>! Bienvenido de nuevo a tu centro de control. 
          Gestiona tus responsabilidades de manera eficiente hoy.
        </p>
        
        <div class="cta-group slide-up-delayed">
          <button 
            v-if="!isAuthenticated" 
            @click="goToLogin"
            class="btn-premium-outline hero-cta"
          >
            Comenzar Ahora
            <svg viewBox="0 0 24 24" width="18" height="18" stroke="currentColor" stroke-width="2.5" fill="none"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline></svg>
          </button>
          
          <button 
            v-else-if="authStore.isAdmin" 
            @click="goToDashboard"
            class="btn-premium-outline hero-cta"
          >
            Ir al Dashboard
            <svg viewBox="0 0 24 24" width="18" height="18" stroke="currentColor" stroke-width="2.5" fill="none"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>
          </button>

          <button 
            class="btn-glass-header hero-btn-secondary"
            @click="scrollToFeatures"
          >
            Saber Más
            <svg class="ml-2" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M7 13l5 5 5-5M7 6l5 5 5-5"/></svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Quick Actions (Only for Authenticated Users) -->
    <div v-if="isAuthenticated" class="quick-actions-section">
      <div class="section-header">
         <h2>Panel de Control</h2>
         <p class="section-subtitle">Accesos directos según tu rol activo: <strong>{{ currentRoleLabel }}</strong></p>
         <div class="divider"></div>
      </div>
      
      <div class="actions-grid">
         <!-- Dashboard only for admins -->
         <div v-if="authStore.isAdmin" class="action-card" @click="router.push('/dashboard')">
            <div class="action-icon bg-dashboard">
              <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2.1" fill="none"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>
            </div>
            <span>Dashboard</span>
         </div>

         <!-- Eventos -->
         <div v-if="authStore.hasPermission('EVENTO:READ')" class="action-card" @click="router.push('/eventos')">
           <div class="action-icon bg-blue">
             <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2.1" fill="none"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
           </div>
           <span>Eventos</span>
         </div>

         <!-- Participantes -->
         <div v-if="authStore.hasPermission('PARTICIPANTE:READ')" class="action-card" @click="router.push('/participantes')">
            <div class="action-icon bg-green">
              <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2.1" fill="none"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="8.5" cy="7" r="4"></circle><line x1="20" y1="8" x2="20" y2="14"></line><line x1="23" y1="11" x2="17" y2="11"></line></svg>
            </div>
           <span>Participantes</span>
         </div>

         <!-- Check-In -->
         <div v-if="authStore.hasPermission('CHECKIN:READ')" class="action-card" @click="router.push('/checkins')">
            <div class="action-icon bg-purple">
              <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2.1" fill="none"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
            </div>
           <span>Check-In</span>
         </div>

         <!-- Certificaciones -->
         <div v-if="authStore.hasPermission('CERTIFICADO:READ')" class="action-card" @click="router.push('/certificaciones')">
            <div class="action-icon bg-orange">
              <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2.1" fill="none"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
            </div>
           <span>Certificados</span>
         </div>

         <!-- Participaciones -->
         <div v-if="authStore.hasPermission('PARTICIPACION:READ')" class="action-card" @click="router.push('/participaciones')">
            <div class="action-icon bg-teal">
              <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2.1" fill="none"><polyline points="9 11 12 14 22 4"></polyline><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"></path></svg>
            </div>
            <span>Participaciones</span>
         </div>

         <!-- Auditoría -->
         <div v-if="authStore.hasPermission('AUDITORIA:READ')" class="action-card" @click="router.push('/auditoria')">
            <div class="action-icon bg-red">
               <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2.1" fill="none"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path></svg>
            </div>
            <span>Auditoría</span>
         </div>

         <!-- Atributos -->
         <div v-if="authStore.hasPermission('CONFIGURACION:READ')" class="action-card" @click="router.push('/atributos')">
            <div class="action-icon bg-indigo">
               <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2.1" fill="none"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
            </div>
            <span>Atributos</span>
         </div>

         <!-- Roles -->
         <div v-if="authStore.hasPermission('ROL:VIEW')" class="action-card" @click="router.push('/roles')">
            <div class="action-icon bg-dark">
               <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2.1" fill="none"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
            </div>
            <span>Roles</span>
         </div>
      </div>
    </div>
    
    <!-- Information Section -->
    <div id="features" class="features-section">
      <div class="section-header">
         <h2>Características</h2>
         <p class="section-subtitle">Todo lo que necesitas para gestionar el ciclo de vida de tu evento</p>
         <div class="divider"></div>
      </div>
      <div class="features-grid">
        <div class="feature-card glass-card">
          <div class="icon-circle">
            <svg viewBox="0 0 24 24" width="32" height="32" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
          </div>
          <h3>Gestión Centralizada</h3>
          <p>Control total sobre múltiples eventos, calendarios y horarios desde un único panel administrativo intuitivo.</p>
        </div>
        
        <div class="feature-card glass-card">
          <div class="icon-circle">
            <svg viewBox="0 0 24 24" width="32" height="32" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
          </div>
          <h3>Directorio de Participantes</h3>
          <p>Administra asistentes, ponentes y staff. Filtra por roles, estados y mantén tu base de datos organizada.</p>
        </div>

        <div class="feature-card glass-card">
          <div class="icon-circle">
             <svg viewBox="0 0 24 24" width="32" height="32" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg>
          </div>
          <h3>Control de Asistencia</h3>
          <p>Sistema ágil de Check-In. Registra la entrada y salida de participantes en tiempo real sin complicaciones.</p>
        </div>

        <div class="feature-card glass-card">
          <div class="icon-circle">
            <svg viewBox="0 0 24 24" width="32" height="32" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
          </div>
          <h3>Certificación Automática</h3>
          <p>Genera y distribuye certificados de participación automáticamente basados en el cumplimiento de asistencia.</p>
        </div>
      </div>
    </div>
    
    <!-- Stats Section -->
    <div class="stats-section fade-in-section">
      <div class="stats-container">
        <div class="stats-header">
           <h2>Métricas del Sistema</h2>
           <p>Estadísticas en tiempo real de la plataforma</p>
        </div>
        <div class="stats-grid">
          <div v-for="stat in stats" :key="stat.label" class="stat-item">
            <div class="stat-value">{{ stat.isLoading ? '...' : stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, watch } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';
import BaseButton from '../components/ui/BaseButton.vue';
import api from '../services/api';

const authStore = useAuthStore();
const router = useRouter();

const isAuthenticated = computed(() => authStore.isAuthenticated);
const currentRoleLabel = computed(() => authStore.currentActiveRole);

const stats = ref([
  { value: '0', label: 'Eventos Creados', isLoading: true },
  { value: '0', label: 'Participantes', isLoading: true },
  { value: '0', label: 'Tipos de Evento', isLoading: true },
  { value: '0', label: 'Certificaciones', isLoading: true }
]);

const goToLogin = () => {
  router.push('/login');
};

const goToDashboard = () => {
    router.push('/dashboard');
};

const scrollToFeatures = () => {
  const element = document.getElementById('features');
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' });
  }
};

const fetchStats = async () => {
    stats.value.forEach(s => s.isLoading = true);
    
    try {
        // We use the new public stats endpoint to get real data regardless of auth state
        const response = await api.get('/public/stats');
        const data = response.data;
        
        stats.value = [
            { value: data.eventos !== undefined ? data.eventos : '0', label: 'Eventos Creados', isLoading: false },
            { value: data.participantes !== undefined ? data.participantes : '0', label: 'Participantes', isLoading: false },
            { value: data.tiposEvento !== undefined ? data.tiposEvento : '0', label: 'Tipos de Evento', isLoading: false },
            { value: data.certificaciones !== undefined ? data.certificaciones : '0', label: 'Certificaciones', isLoading: false }
        ];
    } catch (err) {
        console.error("Error fetching stats", err);
        // Fallback to 0 instead of fake "150+" carton stats
        stats.value = stats.value.map(s => ({ ...s, value: '0', isLoading: false }));
    }
};

onMounted(() => {
    fetchStats();
});

watch(isAuthenticated, (newVal) => {
  if (newVal) fetchStats();
});
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem 4rem 2rem;
  overflow-x: hidden;
}

/* Hero Section */
.hero-section {
  text-align: center;
  padding: 6rem 1rem 4rem 1rem;
  margin-bottom: 2rem;
  position: relative;
  border-bottom: 1px solid rgba(0,0,0,0.05);
}

.hero-title {
  font-size: 3.5rem;
  margin: 0;
  font-weight: 800;
  letter-spacing: -1.5px;
  line-height: 1.1;
  color: #ffffff;
  text-shadow: 0 2px 10px rgba(0,0,0,0.2);
}

.text-gradient {
  background: var(--primary-gradient);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hero-subtitle {
  font-size: 1.25rem;
  margin-top: 1.5rem;
  margin-bottom: 2.5rem;
  color: #cbd5e0;
  max-width: 700px;
  margin-left: auto;
  margin-right: auto;
  line-height: 1.6;
}

.user-name {
    font-weight: 700;
    color: #6366f1;
}

.glass-header {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(12px);
    display: inline-block;
    padding: 1.5rem 3rem;
    border-radius: 30px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
    margin-bottom: 1.5rem;
}

.cta-group {
  display: flex;
  gap: 1rem;
  justify-content: center;
  align-items: center;
}

.icon-right {
  margin-left: 8px;
}

.hero-cta {
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
  padding: 0.8rem 1.8rem;
}

/* Animations */
.slide-down { animation: slideDown 0.8s ease-out; }
.slide-up { animation: slideUp 0.8s ease-out; }
.slide-up-delayed { animation: slideUp 0.8s ease-out 0.2s backwards; }

@keyframes slideDown {
  from { opacity: 0; transform: translateY(-30px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Quick Actions */
.quick-actions-section {
    margin-bottom: 4rem;
}

.actions-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 1.5rem;
    margin-top: 2rem;
    justify-content: center;
}

.action-card {
    background: white;
    padding: 1.5rem;
    border-radius: 16px;
    box-shadow: 0 4px 6px rgba(0,0,0,0.05);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 1rem;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid transparent;
    width: 160px; /* Fixed width for uniform look */
    min-height: 140px;
    text-align: center;
}

.action-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 15px 30px rgba(0,0,0,0.1);
    border-color: #e2e8f0;
}

.action-icon {
    width: 50px;
    height: 50px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
}

.action-card span {
    font-weight: 700;
    color: #1e293b;
    font-size: 0.95rem;
}

.bg-dashboard { background: linear-gradient(135deg, #38bdf8, #2563eb); }
.bg-blue { background: linear-gradient(135deg, #6366f1, #4338ca); }
.bg-green { background: linear-gradient(135deg, #10b981, #047857); }
.bg-purple { background: linear-gradient(135deg, #8b5cf6, #6d28d9); }
.bg-orange { background: linear-gradient(135deg, #f59e0b, #d97706); }
.bg-red { background: linear-gradient(135deg, #ef4444, #b91d1d); }
.bg-teal { background: linear-gradient(135deg, #14b8a6, #0f766e); }
.bg-indigo { background: linear-gradient(135deg, #6366f1, #4338ca); }
.bg-dark { background: linear-gradient(135deg, #475569, #1e293b); }

/* Features Section */
.section-header {
  text-align: center;
  margin-bottom: 4rem;
}

.section-header h2 {
  font-size: 2.5rem;
  color: #ffffff;
  margin-bottom: 0.5rem;
  letter-spacing: -1px;
}

.section-subtitle {
     color: #a0aec0;
     font-size: 1.1rem;
     margin-bottom: 1.5rem;
}

.divider {
  height: 4px;
  width: 100px;
  background: var(--primary-gradient);
  margin: 0 auto;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(99, 102, 241, 0.4);
}

.features-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 2.5rem;
  margin-bottom: 5rem;
  justify-content: center;
}

.glass-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  padding: 2.5rem;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.6);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  text-align: center;
  box-shadow: 0 4px 20px rgba(0,0,0,0.03);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  width: 300px; /* Fixed width for uniform look */
  min-height: 320px;
}

.glass-card:hover {
  transform: translateY(-8px);
  background: white;
  box-shadow: 0 20px 40px rgba(0,0,0,0.1);
}

.icon-circle {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #f0f4ff 0%, #f6f9fc 100%);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1.5rem;
  color: #667eea;
  box-shadow: 0 4px 10px rgba(0,0,0,0.05);
  transition: transform 0.3s;
}

.glass-card:hover .icon-circle {
    transform: rotate(5deg) scale(1.1);
    background: var(--primary-gradient);
    color: white;
}

.glass-card h3 {
  font-size: 1.35rem;
  margin-bottom: 1rem;
  color: #2d3748;
}

.glass-card p {
  color: #718096;
  font-size: 1rem;
  line-height: 1.7;
}

/* Stats Section */
.stats-section {
  background: white;
  border-radius: 24px;
  padding: 4rem;
  box-shadow: var(--shadow-lg);
  position: relative;
  overflow: hidden;
}

.stats-section::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 6px;
    height: 100%;
    background: var(--primary-gradient);
}

.stats-header {
    text-align: center;
    margin-bottom: 3rem;
}

.stats-header h2 {
    margin-bottom: 0.5rem;
    font-size: 2rem;
}
.stats-header p {
    color: #718096;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 3rem;
  text-align: center;
}

.stat-item {
    padding: 1rem;
}

.stat-value {
  font-size: 3rem;
  font-weight: 800;
  background: var(--primary-gradient);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 0.5rem;
  line-height: 1;
}

.stat-label {
  font-weight: 600;
  color: #718096;
  text-transform: uppercase;
  font-size: 0.85rem;
  letter-spacing: 0.05em;
}

.hero-cta {
  padding: 1rem 2.5rem !important;
  font-weight: 800 !important;
  font-size: 1.1rem !important;
  letter-spacing: -0.5px;
}

.hero-btn-secondary {
    padding: 0.8rem 2rem;
    font-weight: 700;
    font-size: 1.05rem;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
}

.ml-2 {
    margin-left: 0.5rem;
}

@media screen and (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 2rem;
  }
  
  .stats-section {
    padding: 2rem;
  }
  
  .cta-group {
    flex-direction: column;
    width: 100%;
  }
  
  .hero-cta, .hero-btn-secondary {
    width: 100%;
    justify-content: center;
  }
}
</style>