<template>
  <div class="container animate-fade-in config-layout">
    <PageHeader 
      title="Atributos del Sistema" 
      subtitle="Configura y personaliza las opciones globales de eventos y comportamientos del sistema"
    />

    <div class="config-container slide-up">
      <!-- Standardized Sidebar -->
      <aside class="config-sidebar">
        <nav class="sidebar-nav">
          <button 
            :class="['nav-item', { active: activeTab === 'modalidades' }]"
            @click="activeTab = 'modalidades'"
          >
            <div class="nav-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle></svg>
            </div>
            <span>Modalidades</span>
          </button>
          
          <button 
            :class="['nav-item', { active: activeTab === 'tipos' }]"
            @click="activeTab = 'tipos'"
          >
            <div class="nav-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="3" y1="10" x2="21" y2="10"></line></svg>
            </div>
            <span>Tipos de Evento</span>
          </button>
          
          <button 
            :class="['nav-item', { active: activeTab === 'estados' }]"
            @click="activeTab = 'estados'"
          >
            <div class="nav-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>
            </div>
            <span>Estados del Sistema</span>
          </button>
        </nav>
      </aside>

      <!-- Content Area Premium -->
      <main class="config-content">
        <div v-if="activeTab === 'modalidades'" class="card premium-card">
            <div class="content-header">
                <h2>Gestión de Modalidades</h2>
                <p>Define si los eventos son presenciales, virtuales o híbridos.</p>
            </div>
            <ModalidadesManager />
        </div>

        <div v-if="activeTab === 'tipos'" class="card premium-card">
            <div class="content-header">
                <h2>Tipos de Evento</h2>
                <p>Categoriza tus eventos para una mejor organización y filtrado.</p>
            </div>
            <TiposManager />
        </div>
        
        <div v-if="activeTab === 'estados'" class="card premium-card">
          <div class="content-header">
            <h2>Estados de Eventos</h2>
            <p>Ciclo de vida de los eventos dentro de la plataforma (Sólo lectura).</p>
          </div>
           <div class="states-grid">
              <div class="state-info-item programado">
                  <span class="state-badge">PROGRAMADO</span>
                  <p>El evento está listo para recibir inscripciones.</p>
              </div>
              <div class="state-info-item en_curso">
                  <span class="state-badge">EN CURSO</span>
                  <p>Evento activo monitorizando asistencias en tiempo real.</p>
              </div>
              <div class="state-info-item finalizado">
                  <span class="state-badge">FINALIZADO</span>
                  <p>Concluido, listo para certificar a los asistentes.</p>
              </div>
              <div class="state-info-item cancelado">
                  <span class="state-badge">CANCELADO</span>
                  <p>Inhabilitado temporalmente o suspendido definitivamente.</p>
              </div>
           </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import PageHeader from '../components/layout/PageHeader.vue';
import ModalidadesManager from '../components/config/ModalidadesManager.vue';
import TiposManager from '../components/config/TiposManager.vue';

const activeTab = ref('modalidades');
</script>

<style scoped>
/* Configuration Layout */
.config-container {
  display: grid;
  grid-template-columns: 1fr;
  gap: 2rem;
  margin-top: 1rem;
}

@media (min-width: 1024px) {
  .config-container {
    grid-template-columns: 320px 1fr;
  }
}

/* Sidebar Styling Premium */
.config-sidebar {
  background: white;
  border-radius: 24px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);
  height: fit-content;
  position: sticky;
  top: 100px;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 1rem 1.25rem;
  border-radius: 16px;
  background: transparent;
  border: 1px solid transparent;
  color: #64748b;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  text-align: left;
}

.nav-item:hover {
  background: #f8fafc;
  color: #1e293b;
  transform: translateX(4px);
}

.nav-item.active {
  background: #f0f7ff;
  color: #6366f1;
  border-color: rgba(99, 102, 241, 0.1);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.08);
}

.nav-icon {
    width: 36px;
    height: 36px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f1f5f9;
    transition: all 0.3s;
}

.nav-item.active .nav-icon {
    background: #6366f1;
    color: white;
    box-shadow: 0 4px 10px rgba(99, 102, 241, 0.3);
}

/* Content Area Premium */
.premium-card {
    background: white;
    border-radius: 24px !important;
    padding: 2rem !important;
    box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05) !important;
}

.content-header {
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #f1f5f9;
}

.content-header h2 {
  font-size: 1.5rem;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.content-header p {
  color: #64748b;
  font-size: 1rem;
}

/* States Grid Premium */
.states-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 1.5rem;
}

.state-info-item {
  padding: 1.5rem;
  border-radius: 20px;
  border: 1px solid #f1f5f9;
  background: #fcfdfe;
  transition: all 0.3s;
}

.state-info-item:hover {
    transform: translateY(-5px);
    border-color: #e2e8f0;
    box-shadow: 0 10px 15px -3px rgba(0,0,0,0.05);
}

.state-badge {
  display: inline-block;
  padding: 0.4rem 1rem;
  border-radius: 50px;
  font-size: 0.75rem;
  font-weight: 800;
  margin-bottom: 1rem;
}

.state-info-item p {
    color: #475569;
    font-size: 0.9rem;
    line-height: 1.5;
}

.state-info-item.programado .state-badge { background: #e0f2fe; color: #0369a1; }
.state-info-item.en_curso .state-badge { background: #dcfce7; color: #15803d; }
.state-info-item.finalizado .state-badge { background: #f3e8ff; color: #7e22ce; }
.state-info-item.cancelado .state-badge { background: #fee2e2; color: #b91c1c; }

/* Animations */
.slide-up { animation: slideUp 0.6s ease-out; }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

@media (max-width: 768px) {
    .config-sidebar {
        position: static;
        width: 100%;
        margin-bottom: 1rem;
    }
    .states-grid {
        grid-template-columns: 1fr;
    }
}
</style>
