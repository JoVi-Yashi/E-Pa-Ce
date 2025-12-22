<template>
  <div class="container fade-in">
    <div class="glass-header">
      <h1>Acerca de E-Pa-Ce</h1>
    </div>

    <div class="about-content">
      <div class="card about-card">
        <div class="logo-section">
             <img src="../assets/image/EPaCe_logo.png" alt="E-Pa-Ce Logo" class="about-logo" />
        </div>
        
        <div class="info-section">
          <h2>Información del Proyecto</h2>
          <p class="description">
            E-Pa-Ce (Eventos - Participantes - Certificaciones) es una plataforma integral diseñada para la gestión eficiente de eventos corporativos, educativos y sociales. Nuestra misión es simplificar la administración de asistentes y certificaciones.
          </p>

          <div class="details-grid">
            <div class="detail-item">
              <span class="label">Versión</span>
              <span class="value">{{ appInfo.version }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Fecha de Creación</span>
              <span class="value">{{ appInfo.releaseDate }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Creador/Desarrollador</span>
              <span class="value">{{ appInfo.creator }}</span>
            </div>
             <div class="detail-item">
              <span class="label">Tecnologías Principales</span>
              <span class="value">{{ appInfo.technologies }}</span>
            </div>
             <div class="detail-item">
              <span class="label">Licencia</span>
              <span class="value">{{ appInfo.license }}</span>
            </div>
             <div class="detail-item">
              <span class="label">Estado</span>
              <span class="value highlight">En Desarrollo Activo</span>
            </div>
          </div>
          
          <div class="divider"></div>
          
          <div class="contact-section">
            <h3>Contacto y Soporte</h3>
            <p>
              Para reportar errores, solicitar nuevas funcionalidades o contactar con el desarrollador, 
              por favor envíe un correo a los administradores del sistema.
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue';

// CONFIGURACIÓN: Edite aquí los datos de la aplicación
const appInfo = reactive({
  creator: 'Johao - SENA Fabrica', // <--- CAMBIE SU NOMBRE AQUÍ
  version: '1.0.0 - Beta',
  releaseDate: 'Diciembre 2025',
  technologies: 'Vue.js 3, Spring Boot 3, PostgreSQL',
  license: 'Propiedad SENA'
});
</script>

<style scoped>
.about-content {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
  padding-bottom: 2rem;
}

.about-card {
  max-width: 900px;
  width: 100%;
  padding: 3rem;
  position: relative;
  overflow: hidden;
  /* Premium glass feel over white card */
  background: linear-gradient(135deg, rgba(255,255,255,0.95) 0%, rgba(255,255,255,0.9) 100%);
}

.about-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 6px;
  background: var(--primary-gradient);
}

.about-logo {
  max-width: 180px;
  height: auto;
  margin-bottom: 2rem;
  filter: drop-shadow(0 10px 15px rgba(0,0,0,0.1));
  transition: transform 0.3s ease;
}

.about-logo:hover {
  transform: scale(1.05) rotate(2deg);
}

.info-section {
  text-align: center;
}

.description {
  font-size: 1.1rem;
  color: #2d3748; /* Darker for better visibility */
  max-width: 700px;
  margin: 0 auto 3rem;
  line-height: 1.8;
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.detail-item {
  display: flex;
  flex-direction: column;
  padding: 1.5rem;
  background: white;
  border-radius: 12px;
  border: 1px solid #edf2f7;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.detail-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
  border-color: #cbd5e0;
}

.label {
  font-size: 0.85rem;
  color: #4a5568; /* Fixed: Darker color (was #718096) */
  margin-bottom: 0.5rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-weight: 600;
}

.value {
  font-size: 1.1rem;
  font-weight: 700;
  color: #2d3748;
}

.value.highlight {
  color: #667eea;
}

.divider {
  height: 1px;
  background: linear-gradient(to right, transparent, #e2e8f0, transparent);
  width: 100%;
  margin: 3rem 0;
}

.contact-section h3 {
  color: #2d3748;
  margin-bottom: 1rem;
}

.contact-section p {
  color: #718096;
  margin-bottom: 1.5rem;
}

/* Animation */
.fade-in {
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 768px) {
  .about-card {
    padding: 1.5rem;
  }
  
  .details-grid {
    grid-template-columns: 1fr;
  }
}
</style>
