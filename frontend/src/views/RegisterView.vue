<template>
  <div class="register-container">
    <BaseCard class="register-card" elevated>
      <template #header>
        <h2 class="register-title">Registrarse</h2>
      </template>
      <form @submit.prevent="handleRegister" class="register-form">
        <BaseInput
          id="documentoIdentidad"
          v-model="form.documentoIdentidad"
          label="Documento Identidad"
          type="number"
          placeholder="Ingresa tu documento"
          required
        />
        <div class="form-row">
          <BaseInput
            id="nombre"
            v-model="form.nombre"
            label="Nombre"
            type="text"
            placeholder="Tu nombre"
            required
          />
          <BaseInput
            id="apellido"
            v-model="form.apellido"
            label="Apellido"
            type="text"
            placeholder="Tu apellido"
            required
          />
        </div>
        <BaseInput
          id="email"
          v-model="form.email"
          label="Email"
          type="email"
          placeholder="tu@email.com"
          required
        />
        <BaseInput
          id="password"
          v-model="form.password"
          label="Contraseña"
          type="password"
          placeholder="Crea una contraseña segura"
          required
        />
        <div class="form-group">
          <label class="form-label">Roles:</label>
          <div class="checkbox-group">
            <div 
              v-for="role in availableRoles" 
              :key="role" 
              class="checkbox-item"
            >
              <input 
                type="checkbox" 
                :id="role" 
                :value="role" 
                v-model="form.roles"
                class="form-checkbox"
              >
              <label :for="role" class="checkbox-label">{{ role }}</label>
            </div>
          </div>
        </div>
        <BaseButton 
          type="submit" 
          variant="primary" 
          size="large" 
          :loading="loading" 
          block
        >
          Registrarse
        </BaseButton>
      </form>
      <p class="login-link">
        ¿Ya tienes cuenta? <router-link to="/login">Inicia Sesión aquí</router-link>
      </p>
    </BaseCard>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useAuthStore } from '../stores/auth';
import BaseCard from '../components/BaseCard.vue';
import BaseInput from '../components/BaseInput.vue';
import BaseButton from '../components/BaseButton.vue';

const authStore = useAuthStore();

const form = reactive({
  documentoIdentidad: '',
  nombre: '',
  apellido: '',
  email: '',
  password: '',
  roles: []
});

const availableRoles = ['ADMIN', 'USER', 'INVITADO', 'ORGANIZADOR'];
const loading = ref(false);

const handleRegister = async () => {
  // Basic validation
  if (form.roles.length === 0) {
    // Optional: warn or default to INVITADO. Backend defaults to INVITADO if empty.
  }

  loading.value = true;
  
  try {
    await authStore.register(form);
    // Reset form
    Object.assign(form, {
      documentoIdentidad: '',
      nombre: '',
      apellido: '',
      email: '',
      password: '',
      roles: []
    });
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
  padding: 1rem;
}

.register-title {
  text-align: center;
  margin: 0;
  color: #333;
  font-weight: 600;
}

.form-row {
  display: flex;
  gap: 1rem;
}

.form-row .form-group {
  flex: 1;
}

.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.checkbox-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.form-checkbox {
  width: 18px;
  height: 18px;
  accent-color: #667eea;
  border-radius: 4px;
}

.checkbox-label {
  margin: 0;
  font-weight: 500;
  color: #333;
}

.login-link {
  text-align: center;
  margin: 0;
  color: #666;
}

.login-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.login-link a:hover {
  color: #764ba2;
  text-decoration: underline;
}

@media screen and (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 0;
  }
}
</style>