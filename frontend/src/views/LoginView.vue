<template>
  <div class="login-container">
    <BaseCard class="login-card" elevated>
      <template #header>
        <div class="header-content">
          <div class="login-icon-portal">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" style="color: white;">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
            </svg>
          </div>
          <h2 class="login-title">Iniciar Sesión</h2>
          <p class="login-subtitle">Bienvenido de nuevo a E-Pa-Ce</p>
        </div>
      </template>

      <form @submit.prevent="handleLogin" class="login-form">
        <BaseInput
          id="email"
          v-model="email"
          label="Email"
          type="email"
          placeholder="nombre@ejemplo.com"
          required
        />
        <BaseInput
          id="password"
          v-model="password"
          label="Contraseña"
          type="password"
          placeholder="••••••••"
          required
        />
        <div class="form-actions">
          <BaseButton 
            type="submit" 
            variant="primary" 
            size="large" 
            :loading="loading" 
            block
            class="login-submit-btn"
          >
            <span class="btn-text">Ingresar</span>
            <span class="btn-arrow">→</span>
          </BaseButton>
        </div>
      </form>

      <div class="auth-links">
        <router-link to="/forgot-password" class="forgot-link">¿Olvidaste tu contraseña?</router-link>
        <div class="divider">
          <span>O</span>
        </div>
        <p class="register-link">
          ¿No tienes cuenta? <router-link to="/register">Regístrate aquí</router-link>
        </p>
      </div>
    </BaseCard>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';
import BaseCard from '../components/ui/BaseCard.vue';
import BaseInput from '../components/ui/BaseInput.vue';
import BaseButton from '../components/ui/BaseButton.vue';

const authStore = useAuthStore();
const router = useRouter();

const email = ref('');
const password = ref('');
const loading = ref(false);

const handleLogin = async () => {
  if (!email.value || !password.value) return;
  
  loading.value = true;
  
  try {
    await authStore.login(email.value, password.value);
    router.push('/');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 100px);
  padding: 1rem;
}

.login-card {
  width: 100%;
  max-width: 450px;
  animation: slideUp 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.header-content {
  text-align: center;
  padding: 1rem 0;
}

.login-icon-portal {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #6366f1, #a855f7);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1.5rem;
  box-shadow: 0 10px 20px rgba(99, 102, 241, 0.3);
  font-size: 1.5rem;
}

.login-title {
  margin: 0;
  color: #1a202c;
  font-weight: 800;
  font-size: 1.75rem;
  letter-spacing: -0.5px;
}

.login-subtitle {
  color: #718096;
  margin-top: 0.5rem;
  font-size: 0.95rem;
}

.login-form {
  margin-top: 0.5rem;
}

.form-actions {
  margin-top: 2rem;
}

.login-submit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  height: 56px;
  /* border-radius inherited from global button */
}

.btn-text {
  font-weight: 700;
}

.btn-arrow {
  font-size: 1.25rem;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.login-submit-btn:hover .btn-arrow {
  transform: translateX(6px);
}

.auth-links {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  align-items: center;
  margin-top: 2rem;
}

.forgot-link {
  font-size: 0.9rem;
  color: #718096;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.forgot-link:hover {
  color: #6366f1;
}

.divider {
  width: 100%;
  display: flex;
  align-items: center;
  text-align: center;
  color: #cbd5e0;
  font-size: 0.8rem;
  font-weight: 600;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  border-bottom: 1px solid #e2e8f0;
}

.divider span {
  padding: 0 1rem;
}

.register-link {
  text-align: center;
  margin: 0;
  color: #718096;
  font-size: 0.95rem;
}

.register-link a {
  color: #6366f1;
  text-decoration: none;
  font-weight: 700;
  transition: all 0.3s ease;
}

.register-link a:hover {
  color: #4f46e5;
  text-decoration: underline;
}

@media (max-width: 480px) {
  .login-card {
    border-radius: 0;
    box-shadow: none;
  }
}
</style>