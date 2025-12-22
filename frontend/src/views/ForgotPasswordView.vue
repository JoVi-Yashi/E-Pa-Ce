<template>
  <div class="auth-page-container animate-fade-in">
    <BaseCard class="auth-card" elevated>
      <template #header>
        <div class="header-content">
          <div class="icon-circle">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3m-3-3l2.25-2.25"></path></svg>
          </div>
          <h1 class="auth-title">Recuperar Contraseña</h1>
          <p class="auth-subtitle">Te enviaremos las instrucciones a tu correo</p>
        </div>
      </template>

      <form v-if="!submitted" @submit.prevent="handleForgotPassword" class="auth-form">
        <BaseInput 
          id="email"
          v-model="email" 
          type="email" 
          label="Correo Electrónico"
          placeholder="ejemplo@correo.com" 
          required
          :disabled="loading"
          helpText="Usa el correo con el que te registraste"
        />

        <div class="form-actions">
          <BaseButton 
            type="submit" 
            variant="primary" 
            size="large" 
            :loading="loading" 
            block
          >
            Enviar Enlace de Recuperación
          </BaseButton>
          
          <router-link to="/login" class="back-link">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="19" y1="12" x2="5" y2="12"></line><polyline points="12 19 5 12 12 5"></polyline></svg>
            Volver al inicio de sesión
          </router-link>
        </div>
      </form>

      <div v-else class="success-feedback animate-fade-in">
        <div class="success-icon-wrap">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#48bb78" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
        </div>
        <h3>¡Código enviado!</h3>
        <p>Hemos enviado un código de verificación a <strong>{{ email }}</strong>. Por favor, ingrésalo en la siguiente pantalla para restablecer tu contraseña.</p>
        
        <div class="success-actions">
          <BaseButton variant="primary" size="large" @click="$router.push({ name: 'reset-password', query: { email: email } })" block>Ingresar Código</BaseButton>
          <BaseButton variant="outline" @click="submitted = false">Reintentar con otro correo</BaseButton>
          <router-link to="/login" class="btn-text">Ir al Login</router-link>
        </div>
      </div>
    </BaseCard>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import api from '../services/api';
import BaseCard from '../components/ui/BaseCard.vue';
import BaseInput from '../components/ui/BaseInput.vue';
import BaseButton from '../components/ui/BaseButton.vue';
import { useNotificationStore } from '../stores/notifications';

const notificationStore = useNotificationStore();

const email = ref('');
const loading = ref(false);
const submitted = ref(false);

const handleForgotPassword = async () => {
  if (!email.value) return;
  
  loading.value = true;
  try {
    await api.post('/auth/forgot-password', { email: email.value });
    submitted.value = true;
  } catch (err) {
    notificationStore.showError(err.response?.data?.message || 'Error al enviar el correo de recuperación');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.auth-page-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 200px);
  padding: 1rem;
}

.auth-card {
  width: 100%;
  max-width: 480px;
}

.header-content {
  text-align: center;
  padding: 1rem 0;
}

.icon-circle {
  width: 60px;
  height: 60px;
  background: var(--primary-gradient);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1.5rem;
  box-shadow: 0 10px 15px rgba(102, 126, 234, 0.3);
}

.auth-title {
  font-size: 1.75rem;
  font-weight: 800;
  margin-bottom: 0.5rem;
  background: var(--primary-gradient);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.auth-subtitle {
  color: #718096;
  font-size: 1rem;
}

.form-actions {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin-top: 1rem;
}

.back-link {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  color: #718096;
  text-decoration: none;
  font-size: 0.9rem;
  font-weight: 600;
  transition: all 0.2s;
}

.back-link:hover {
  color: var(--primary-color);
  transform: translateX(-3px);
}

.success-feedback {
  text-align: center;
  padding: 2rem 0;
}

.success-icon-wrap {
  margin-bottom: 1.5rem;
}

.success-feedback h3 {
  font-size: 1.5rem;
  color: #2d3748;
  margin-bottom: 1rem;
}

.success-feedback p {
  color: #718096;
  margin-bottom: 2rem;
  line-height: 1.6;
}

.success-actions {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.btn-text {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 600;
  font-size: 0.9rem;
}
</style>
