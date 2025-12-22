<template>
  <footer class="app-footer">
    <div class="footer-content">
      <div class="footer-section">
        <h3 class="footer-brand">E-Pa-Ce</h3>
        <p class="footer-desc">Sistema de gestión de eventos y participaciones de alta precisión.</p>
      </div>
      <div class="footer-section">
        <h4 class="footer-title">Plataforma</h4>
        <ul class="footer-links">
          <li><router-link to="/">Inicio</router-link></li>
          <li v-if="authStore.isAuthenticated"><router-link to="/eventos">Eventos</router-link></li>
          <li v-if="authStore.isAdmin"><router-link to="/dashboard">Panel de Control</router-link></li>
          <li><router-link to="/about">Acerca de</router-link></li>
        </ul>
      </div>
      <div class="footer-section">
        <h4 class="footer-title">Contacto</h4>
        <div class="contact-info">
          <p>Email: <span>info@epace.com</span></p>
        </div>
      </div>
    </div>
    <div class="footer-bottom">
      <p>&copy; {{ currentYear }} E-Pa-Ce. Todos los derechos reservados.</p>
    </div>
  </footer>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '../../stores/auth';

const authStore = useAuthStore();
const currentYear = ref(new Date().getFullYear());
</script>

<style scoped>
.app-footer {
  background: #1a1c23;
  color: #a0aec0;
  padding: 4rem 2rem 2rem;
  margin-top: auto;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  position: relative;
}

.footer-content {
  display: grid;
  grid-template-columns: 1.5fr 1fr 1fr;
  gap: 4rem;
  max-width: 1200px;
  margin: 0 auto;
}

.footer-brand {
  color: white;
  font-size: 1.5rem;
  font-weight: 800;
  margin-bottom: 1rem;
  background: var(--primary-gradient);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.footer-desc {
  max-width: 300px;
  line-height: 1.6;
}

.footer-title {
  color: white;
  font-size: 1rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: 0.75rem;
}

.footer-links a {
  color: #a0aec0;
  text-decoration: none;
  transition: all 0.3s ease;
  font-weight: 500;
}

.footer-links a:hover {
  color: #6366f1;
  padding-left: 5px;
}

.contact-info p {
  margin-bottom: 0.5rem;
}

.contact-info span {
  color: white;
}

.footer-bottom {
  text-align: center;
  padding-top: 2rem;
  margin-top: 4rem;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  font-size: 0.9rem;
}

@media screen and (max-width: 768px) {
  .footer-content {
    grid-template-columns: 1fr;
    gap: 2.5rem;
    text-align: center;
  }

  .footer-desc {
    margin: 0 auto;
  }

  .footer-links a:hover {
    padding-left: 0;
  }
}
</style>