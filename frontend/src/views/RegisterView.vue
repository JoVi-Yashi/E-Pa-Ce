<template>
  <div class="register-container">
    <BaseCard class="register-card" elevated>
      <template #header>
        <div class="header-content">
          <div class="register-icon-portal">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" style="color: white;">
              <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
              <circle cx="8.5" cy="7" r="4"></circle>
              <line x1="20" y1="8" x2="20" y2="14"></line>
              <line x1="23" y1="11" x2="17" y2="11"></line>
            </svg>
          </div>
          <h2 class="register-title">Unirse a la Comunidad</h2>
          <p class="register-subtitle">Registra tu cuenta en segundos</p>
        </div>
      </template>

      <form @submit.prevent="handleRegister" class="register-form">
        <BaseInput
          id="documentoIdentidad"
          v-model="form.documentoIdentidad"
          label="Documento Identidad"
          type="number"
          placeholder="C.C. / T.I."
          required
        />
        <div class="form-row">
          <BaseInput
            id="nombre"
            v-model="form.nombre"
            label="Nombre"
            type="text"
            placeholder="Ej: Juan"
            required
          />
          <BaseInput
            id="apellido"
            v-model="form.apellido"
            label="Apellido"
            type="text"
            placeholder="Ej: Pérez"
            required
          />
        </div>
        <BaseInput
          id="email"
          v-model="form.email"
          label="Email"
          type="email"
          placeholder="juan.perez@email.com"
          required
        />
        <BaseInput
          id="password"
          v-model="form.password"
          label="Contraseña"
          type="password"
          placeholder="Min. 8 caracteres"
          required
        />
        <div class="form-group roles-group">
          <label class="form-label">Tipo de Usuario:</label>
          <div class="checkbox-group">
            <div 
              v-for="role in availableRoles" 
              :key="role" 
              class="role-badge"
              :class="{ active: form.roles.includes(role) }"
            >
              <input 
                type="checkbox" 
                :id="role" 
                :value="role" 
                v-model="form.roles"
                class="hidden-checkbox"
              >
              <label :for="role" class="role-label">{{ role }}</label>
            </div>
          </div>
        </div>
        <div class="form-actions">
          <BaseButton 
            type="submit" 
            variant="primary" 
            size="large" 
            :loading="loading" 
            block
            class="register-submit-btn"
          >
            <span class="btn-text">Registrarse</span>
            <span class="btn-icon-wrapper">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M5 12h14"></path>
                <polyline points="12 5 19 12 12 19"></polyline>
              </svg>
            </span>
          </BaseButton>
        </div>
      </form>

      <div class="auth-links">
        <div class="divider">
          <span>O</span>
        </div>
        <p class="login-link">
          ¿Ya tienes cuenta? <router-link to="/login">Inicia Sesión aquí</router-link>
        </p>
      </div>
    </BaseCard>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useAuthStore } from '../stores/auth';
import BaseCard from '../components/ui/BaseCard.vue';
import BaseInput from '../components/ui/BaseInput.vue';
import BaseButton from '../components/ui/BaseButton.vue';

const authStore = useAuthStore();

const form = reactive({
  documentoIdentidad: '',
  nombre: '',
  apellido: '',
  email: '',
  password: '',
  roles: []
});

const availableRoles = ['ADMIN', 'MONITOR', 'INVITADO', 'OPERADOR'];
const loading = ref(false);

const handleRegister = async () => {
  loading.value = true;
  
  try {
    const dataToSend = {
      ...form,
      documentoIdentidad: form.documentoIdentidad ? Number(form.documentoIdentidad) : null
    };

    await authStore.register(dataToSend);
    
    setTimeout(() => {
      window.location.href = '/login';
    }, 1500);

    Object.assign(form, {
      documentoIdentidad: '',
      nombre: '',
      apellido: '',
      email: '',
      password: '',
      roles: []
    });
  } catch (error) {
    console.error('Registration error:', error);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 100px);
  padding: 2rem 1rem;
}

.register-card {
  width: 100%;
  max-width: 550px;
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

.register-icon-portal {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #10b981, #3b82f6);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1.5rem;
  box-shadow: 0 10px 20px rgba(16, 185, 129, 0.2);
  font-size: 1.5rem;
}

.register-title {
  margin: 0;
  color: #1a202c;
  font-weight: 800;
  font-size: 1.75rem;
  letter-spacing: -0.5px;
}

.register-subtitle {
  color: #718096;
  margin-top: 0.5rem;
  font-size: 0.95rem;
}

.register-form {
  margin-top: 1rem;
}

.form-row {
  display: flex;
  gap: 1.5rem;
}

.form-row > * {
  flex: 1;
}

.roles-group {
  margin-top: 1.5rem;
}

.form-label {
  display: block;
  margin-bottom: 0.75rem;
  font-weight: 600;
  color: #4a5568;
  font-size: 0.9rem;
}

.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.role-badge {
  position: relative;
  transition: all 0.2s ease;
}

.hidden-checkbox {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.role-label {
  display: block;
  padding: 0.5rem 1rem;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  border-radius: 50px;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 600;
  color: #64748b;
  transition: all 0.2s ease;
}

.role-badge.active .role-label {
  background: #6366f1;
  color: white;
  border-color: #6366f1;
  box-shadow: 0 4px 6px rgba(99, 102, 241, 0.2);
}

.form-actions {
  margin-top: 2.5rem;
}

.register-submit-btn {
  height: 56px;
  /* border-radius inherited */
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
}

.btn-icon-wrapper {
  display: flex;
  align-items: center;
  transition: transform 0.3s ease;
}

.register-submit-btn:hover .btn-icon-wrapper {
  transform: translateX(4px);
}

.auth-links {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  align-items: center;
  margin-top: 2rem;
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

.login-link {
  text-align: center;
  margin: 0;
  color: #718096;
}

.login-link a {
  color: #6366f1;
  text-decoration: none;
  font-weight: 700;
  transition: color 0.3s ease;
}

.login-link a:hover {
  color: #4f46e5;
  text-decoration: underline;
}

@media (max-width: 640px) {
  .form-row {
    flex-direction: column;
    gap: 0;
  }
}
</style>