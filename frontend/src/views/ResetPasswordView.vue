<template>
  <div class="auth-page-container animate-fade-in">
    <BaseCard class="auth-card" elevated>
      <template #header>
        <div class="header-content" v-if="!success">
          <div class="icon-circle">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
          </div>
          <h1 class="auth-title">Nueva Contraseña</h1>
          <p class="auth-subtitle">Define una clave segura para tu cuenta</p>
        </div>
      </template>

      <form v-if="!success" @submit.prevent="handleResetPassword" class="auth-form">
        <BaseInput 
          id="email"
          v-model="email" 
          type="email" 
          label="Correo Electrónico"
          placeholder="ejemplo@correo.com" 
          required
          :disabled="loading || !!route.query.email"
          helpText="El correo donde recibiste el código"
        />

        <BaseOtpInput 
          v-model="token" 
          :length="6"
          label="Código de Verificación"
          :disabled="loading"
          helpText="Digita los 6 números que enviamos a tu correo"
        />

        <BaseInput 
          id="password"
          v-model="newPassword" 
          type="password" 
          label="Nueva Contraseña"
          placeholder="Mínimo 6 caracteres" 
          required
          :disabled="loading"
          :hasError="!!error && !passwordsMatch"
          helpText="Usa letras, números y símbolos para mayor seguridad"
        />

        <BaseInput 
          id="confirm"
          v-model="confirmPassword" 
          type="password" 
          label="Confirmar Contraseña"
          placeholder="Repite tu contraseña" 
          required
          :disabled="loading"
          :hasError="!!confirmPassword && !passwordsMatch"
          :errorMessage="!passwordsMatch ? 'Las contraseñas no coinciden' : ''"
        />

        <div class="password-strength" v-if="newPassword">
          <div class="strength-bar">
            <div class="strength-fill" :style="{ width: strengthWidth, backgroundColor: strengthColor }"></div>
          </div>
          <span class="strength-text">{{ strengthLabel }}</span>
        </div>

        <div class="form-actions">
          <BaseButton 
            type="submit" 
            variant="primary" 
            size="large" 
            :loading="loading" 
            :disabled="!isFormValid"
            block
          >
            Actualizar Contraseña
          </BaseButton>
        </div>
      </form>

      <div v-else class="success-feedback animate-fade-in">
        <div class="success-icon-wrap">
          <div class="confetti-placeholder">
             <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#48bb78" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
          </div>
        </div>
        <h3>¡Todo listo!</h3>
        <p>Tu contraseña ha sido actualizada con éxito. Ya puedes volver a entrar al sistema.</p>
        
        <div class="success-actions">
          <BaseButton variant="primary" size="large" @click="$router.push('/login')" block>Ir al Inicio de Sesión</BaseButton>
        </div>
      </div>
    </BaseCard>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import api from '../services/api';
import BaseCard from '../components/ui/BaseCard.vue';
import BaseInput from '../components/ui/BaseInput.vue';
import BaseOtpInput from '../components/ui/BaseOtpInput.vue';
import BaseButton from '../components/ui/BaseButton.vue';
import { useNotificationStore } from '../stores/notifications';

const notificationStore = useNotificationStore();

const route = useRoute();
const token = ref('');
const email = ref('');
const newPassword = ref('');
const confirmPassword = ref('');
const loading = ref(false);
const error = ref('');
const success = ref(false);

onMounted(() => {
  token.value = route.query.token || '';
  email.value = route.query.email || '';
});

const passwordsMatch = computed(() => {
  return newPassword.value === confirmPassword.value;
});

const isFormValid = computed(() => {
  return newPassword.value.length >= 6 && passwordsMatch.value && !loading.value;
});

const passwordStrength = computed(() => {
  const p = newPassword.value;
  if (!p) return 0;
  let s = 0;
  if (p.length >= 6) s += 1;
  if (p.length >= 10) s += 1;
  if (/[A-Z]/.test(p)) s += 1;
  if (/[0-9]/.test(p)) s += 1;
  if (/[^A-Za-z0-9]/.test(p)) s += 1;
  return s;
});

const strengthLabel = computed(() => {
  const labels = ['Muy débil', 'Débil', 'Regular', 'Buena', 'Fuerte', 'Excelente'];
  return labels[passwordStrength.value] || 'Muy débil';
});

const strengthColor = computed(() => {
  const colors = ['#e53e3e', '#e53e3e', '#dd6b20', '#d69e2e', '#38a169', '#2f855a'];
  return colors[passwordStrength.value] || '#e53e3e';
});

const strengthWidth = computed(() => {
  return (passwordStrength.value / 5 * 100) + '%';
});

const handleResetPassword = async () => {
  if (!isFormValid.value) return;

  loading.value = true;
  error.value = '';
  try {
    await api.post('/auth/reset-password', { 
      token: token.value,
      email: email.value,
      newPassword: newPassword.value 
    });
    success.value = true;
    notificationStore.showSuccess('Contraseña actualizada correctamente');
  } catch (err) {
    error.value = err.response?.data?.message || 'Error al restablecer';
    notificationStore.showError(error.value);
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

.password-strength {
  margin-bottom: 1.5rem;
}

.strength-bar {
  height: 6px;
  background: #edf2f7;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 0.5rem;
}

.strength-fill {
  height: 100%;
  transition: all 0.3s ease;
}

.strength-text {
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  color: #718096;
}

.form-actions {
  margin-top: 1rem;
}

.success-feedback {
  text-align: center;
  padding: 2rem 0;
}

.success-icon-wrap {
  margin-bottom: 2rem;
  display: flex;
  justify-content: center;
}

.confetti-placeholder {
  animation: bounce 2s infinite ease-in-out;
}

@keyframes bounce {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.success-feedback h3 {
  font-size: 1.8rem;
  color: #2d3748;
  margin-bottom: 1rem;
  font-weight: 800;
}

.success-feedback p {
  color: #718096;
  margin-bottom: 2.5rem;
  line-height: 1.6;
}
</style>
