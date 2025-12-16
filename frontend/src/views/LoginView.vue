<template>
  <div class="login-container">
    <BaseCard class="login-card" elevated>
      <template #header>
        <h2 class="login-title">Iniciar Sesión</h2>
      </template>
      <form @submit.prevent="handleLogin" class="login-form">
        <BaseInput
          id="email"
          v-model="email"
          label="Email"
          type="email"
          placeholder="Ingresa tu email"
          required
        />
        <BaseInput
          id="password"
          v-model="password"
          label="Contraseña"
          type="password"
          placeholder="Ingresa tu contraseña"
          required
        />
        <BaseButton 
          type="submit" 
          variant="primary" 
          size="large" 
          :loading="loading" 
          block
        >
          Ingresar
        </BaseButton>
      </form>
      <p class="register-link">
        ¿No tienes cuenta? <router-link to="/register">Regístrate aquí</router-link>
      </p>
    </BaseCard>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';
import BaseCard from '../components/BaseCard.vue';
import BaseInput from '../components/BaseInput.vue';
import BaseButton from '../components/BaseButton.vue';

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

.login-title {
  text-align: center;
  margin: 0;
  color: #333;
  font-weight: 600;
}

.login-form {
  margin-bottom: 1.5rem;
}

.register-link {
  text-align: center;
  margin: 0;
  color: #666;
}

.register-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.register-link a:hover {
  color: #764ba2;
  text-decoration: underline;
}
</style>