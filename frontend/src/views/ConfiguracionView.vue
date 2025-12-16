<template>
  <div class="container animate-fade-in config-layout">
    <div class="header-actions mobile-only">
      <div class="glass-header">
        <h1>Configuración</h1>
      </div>
      <router-link to="/" class="back-link">Volver</router-link>
    </div>

    <!-- Sidebar / Tabs Navigation -->
    <aside class="config-sidebar card">
        <div class="sidebar-header desktop-only">
            <h2>Ajustes</h2>
            <router-link to="/" class="back-link-sm">← Volver</router-link>
        </div>
        <nav class="sidebar-nav">
          <button 
            :class="['nav-item', { active: activeTab === 'modalidades' }]"
            @click="activeTab = 'modalidades'"
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
            Modalidades
          </button>
          <button 
            :class="['nav-item', { active: activeTab === 'tipos' }]"
            @click="activeTab = 'tipos'"
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
            Tipos de Evento
          </button>
          <button 
            :class="['nav-item', { active: activeTab === 'estados' }]"
            @click="activeTab = 'estados'"
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>
            Estados del Sistema
          </button>
        </nav>
    </aside>

    <!-- Content Area -->
    <main class="config-content">
      <div v-if="activeTab === 'modalidades'" class="card">
          <div class="content-header">
              <h2>Gestión de Modalidades</h2>
              <p class="text-secondary">Define si los eventos son presenciales, virtuales, etc.</p>
          </div>
          <ModalidadesManager />
      </div>

      <div v-if="activeTab === 'tipos'" class="card">
          <div class="content-header">
              <h2>Tipos de Evento</h2>
              <p class="text-secondary">Categoriza tus eventos (Conferencia, Taller, Seminario).</p>
          </div>
          <TiposManager />
      </div>
      
      <div v-if="activeTab === 'estados'" class="card state-card">
        <div class="content-header">
          <h2>Estados de Eventos</h2>
          <p class="text-secondary">Ciclo de vida de los eventos en el sistema (Informativo).</p>
        </div>
        <div class="states-grid">
           <div class="state-item programado">
               <span class="badge">PROGRAMADO</span>
               <p>El evento ha sido creado y está próximo a realizarse. Acepta inscripciones.</p>
           </div>
           <div class="state-item en_curso">
               <span class="badge">EN_CURSO</span>
               <p>El evento está sucediendo ahora mismo. Se permiten Check-Ins.</p>
           </div>
           <div class="state-item finalizado">
               <span class="badge">FINALIZADO</span>
               <p>El evento ha concluido. Se pueden generar certificados.</p>
           </div>
           <div class="state-item cancelado">
               <span class="badge">CANCELADO</span>
               <p>El evento fue suspendido y no se llevará a cabo.</p>
           </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import ModalidadesManager from '../components/config/ModalidadesManager.vue';
import TiposManager from '../components/config/TiposManager.vue';

const activeTab = ref('modalidades');
</script>

<style scoped>
.config-layout {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

@media (min-width: 768px) {
  .config-layout {
    flex-direction: row;
    align-items: flex-start;
  }
}

.mobile-only { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
.desktop-only { display: none; }

@media (min-width: 768px) {
  .mobile-only { display: none; }
  .desktop-only { display: block; }
}

/* Sidebar */
.config-sidebar {
  width: 100%;
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);
}

@media (min-width: 768px) {
  .config-sidebar {
    width: 280px;
    position: sticky;
    top: 20px;
    height: calc(100vh - 40px);
  }
}

.sidebar-header {
  margin-bottom: 2rem;
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 1rem;
}

.back-link-sm {
  font-size: 0.85rem;
  color: #718096;
  text-decoration: none;
  display: block;
  margin-top: 0.5rem;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  background: transparent;
  border: none;
  color: #4a5568;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
  width: 100%;
}

.nav-item:hover {
  background: #f7fafc;
  color: #2d3748;
}

.nav-item.active {
  background: #ebf4ff;
  color: #5a67d8;
  font-weight: 600;
}

/* Content */
.config-content {
  flex: 1;
  width: 100%;
}

.content-header {
  margin-bottom: 1.5rem;
}

.text-secondary {
  color: #718096;
  font-size: 0.95rem;
}

/* States Grid */
.states-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1.5rem;
}

.state-item {
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: #fff;
}

.state-item .badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 50px;
  font-size: 0.75rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}

.state-item.programado .badge { background: #ebf8ff; color: #3182ce; }
.state-item.en_curso .badge { background: #f0fff4; color: #38a169; }
.state-item.finalizado .badge { background: #faf5ff; color: #805ad5; }
.state-item.cancelado .badge { background: #fff5f5; color: #e53e3e; }
</style>
