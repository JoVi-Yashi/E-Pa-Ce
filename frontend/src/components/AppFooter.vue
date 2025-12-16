<template>
  <footer class="app-footer">
    <div class="footer-content">
      <div class="footer-section">
        <h3>E-Pa-Ce</h3>
        <p>Sistema de gestión de eventos y participaciones</p>
      </div>
      <div class="footer-section">
        <h4>Enlaces Rápidos</h4>
        <ul>
          <li><router-link to="/">Inicio</router-link></li>
          <li><router-link to="/eventos">Eventos</router-link></li>
          <li><router-link to="/about">Acerca de</router-link></li>
        </ul>
      </div>
      <div class="footer-section">
        <h4>Contacto</h4>
        <p>Email: info@epace.com</p>
        <p>Teléfono: +1 (555) 123-4567</p>
      </div>
    </div>
    <div class="footer-bottom">
      <p>&copy; {{ currentYear }} E-Pa-Ce. Todos los derechos reservados.</p>
    </div>
  </footer>
</template>

<script setup>
import { ref } from 'vue';

const currentYear = ref(new Date().getFullYear());
</script>

<style scoped>
.app-footer {
  background: linear-gradient(135deg, #2c3e50, #34495e);
  color: white;
  padding: 2rem 1rem 1rem;
  margin-top: 2rem;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  box-shadow: 0 -5px 15px rgba(0, 0, 0, 0.1);
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.footer-section h3,
.footer-section h4 {
  color: white;
  margin-top: 0;
}

.footer-section ul {
  list-style: none;
  padding: 0;
}

.footer-section li {
  margin-bottom: 0.5rem;
}

.footer-section a {
  color: #ecf0f1;
  text-decoration: none;
  transition: color 0.3s ease;
}

.footer-section a:hover {
  color: #667eea;
  text-decoration: underline;
}

.footer-bottom {
  text-align: center;
  padding-top: 1rem;
  margin-top: 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

@media screen and (max-width: 768px) {
  .app-footer {
    padding: 1.5rem 1rem;
  }
  
  .footer-content {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
}
</style>