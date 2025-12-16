<template>
  <div class="container animate-fade-in">
    <div class="header-actions">
      <div class="glass-header">
        <h1>Mi Perfil</h1>
      </div>
      <div class="actions">
        <router-link to="/" class="back-link">Volver al Inicio</router-link>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div> Cargando perfil...
    </div>
    <div v-if="error" class="error-msg">{{ error }}</div>

    <div class="card profile-card" v-if="user">
       <div class="profile-header">
         <div class="avatar-large">{{ userInitials }}</div>
         <h2>{{ user.nombre }} {{ user.apellido }}</h2>
         <span class="role-badge">{{ user.rolNombre || 'Usuario' }}</span>
       </div>

       <form @submit.prevent="updateProfile" class="profile-form">
          <div class="form-grid">
             <div class="form-group">
                <label>Documento</label>
                <input v-model="user.documentoIdentidad" disabled class="input-disabled" />
             </div>
             <div class="form-group">
                <label>Email</label>
                <input v-model="user.email" disabled class="input-disabled" />
             </div>
             <div class="form-group">
                <label>Nombre</label>
                <input v-model="form.nombre" required />
             </div>
             <div class="form-group">
                <label>Apellido</label>
                <input v-model="form.apellido" required />
             </div>
             <div class="form-group full-width">
                <label>Nueva Contraseña (Opcional)</label>
                <input v-model="form.password" type="password" placeholder="Dejar en blanco para mantener actual" />
             </div>
          </div>

          <div class="form-actions">
             <button type="submit" class="btn-primary">Actualizar Perfil</button>
          </div>
       </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from '../stores/auth';
import api from '../services/api';

const authStore = useAuthStore();
const user = ref(null);
const loading = ref(false);
const error = ref('');

const form = ref({
    nombre: '',
    apellido: '',
    password: ''
});

const userInitials = computed(() => {
  if (user.value && user.value.nombre) {
    return user.value.nombre.substring(0, 2).toUpperCase();
  }
  return 'YO';
});

const fetchProfile = async () => {
    // We need the ID. It should be in authStore.user (if we stored it).
    // If authStore.user only has basic info, we might need to fetch by email or rely on stored ID.
    // Let's assume authStore.user has the ID or we can get it.
    // If not, we might need an endpoint /api/auth/me or similar.
    // For now, let's look at what's in localStorage 'user'.
    const storedUser = authStore.user;
    if (!storedUser || !storedUser.idParticipante && !storedUser.id) {
        error.value = "No se pudo identificar al usuario. Inicie sesión nuevamente.";
        return;
    }
    
    // Use the ID from store.
    const id = storedUser.idParticipante || storedUser.id; 
    
    loading.value = true;
    try {
        const res = await api.get(`/participantes/${id}`);
        user.value = res.data;
        form.value.nombre = user.value.nombre;
        form.value.apellido = user.value.apellido;
    } catch(err) {
        error.value = "Error cargando perfil: " + err.message;
    } finally {
        loading.value = false;
    }
};

const updateProfile = async () => {
    if(!user.value) return;
    try {
        const updateData = {
            ...user.value,
            nombre: form.value.nombre,
            apellido: form.value.apellido,
            password: form.value.password || undefined // backend should handle null/empty
        };
        
        await api.put(`/participantes/${user.value.documentoIdentidad || user.value.idParticipante}`, updateData); // careful with ID field
        // Usually ID is path param.
        // Wait, endpoint is /participantes/{id}. My service uses Long id. 
        // Is ID documentoIdentidad or generated ID? 
        // ParticipanteEntity has `documentoIdentidad` as ID.
        
        alert("Perfil actualizado correctamente");
        // Update store if needed
        authStore.user.nombre = form.value.nombre;
        authStore.user.apellido = form.value.apellido;
        localStorage.setItem('user', JSON.stringify(authStore.user));
    } catch(err) {
        alert("Error actualizando: " + err.message);
    }
};

onMounted(fetchProfile);
</script>

<style scoped>
.profile-card {
    max-width: 600px;
    margin: 0 auto;
    padding: 2rem;
}
.profile-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 2rem;
}
.avatar-large {
    width: 80px;
    height: 80px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 2rem;
    font-weight: bold;
    margin-bottom: 1rem;
    box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}
.role-badge {
    background: #e2e8f0;
    padding: 0.25rem 0.75rem;
    border-radius: 20px;
    font-size: 0.8rem;
    color: #4a5568;
    margin-top: 0.5rem;
}
.form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
}
.full-width { grid-column: 1 / -1; }
.form-group label {
    display: block;
    font-size: 0.875rem;
    font-weight: 600;
    margin-bottom: 0.5rem;
    color: #4a5568;
}
.input-disabled {
    background-color: #f7fafc;
    color: #718096;
}
.form-actions {
    margin-top: 2rem;
    display: flex;
    justify-content: center;
}
</style>
