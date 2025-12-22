<template>
  <div class="profile-container animate-fade-in">
    <PageHeader title="Configuración de Perfil" />

    <LoadingSpinner v-if="loading" />
    
    <div v-else-if="error" class="error-notification slide-up">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
        <span>{{ error }}</span>
        <button class="btn-retry" @click="fetchProfile">Reintentar</button>
    </div>

    <div v-else-if="user" class="profile-grid">
        <!-- Sidebar: Image & Stats -->
        <div class="profile-sidebar-card glass-card">
             <div class="avatar-uploader">
                <div class="avatar-preview-container">
                     <img v-if="user.fotoPerfil || form.fotoPerfil" :src="form.fotoPerfil || user.fotoPerfil" class="avatar-image" alt="Perfil" />
                     <div v-else class="avatar-initials">{{ userInitials }}</div>
                     
                     <div class="avatar-overlay" @click="triggerFileInput">
                         <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path><circle cx="12" cy="13" r="4"></circle></svg>
                         <span>Cambiar Foto</span>
                     </div>
                     <input type="file" ref="fileInput" @change="handleFileChange" accept="image/*" style="display: none" />
                 </div>
                 <div class="uploader-info">
                    <h3>{{ user.nombre }} {{ user.apellido }}</h3>
                    <div class="active-badge">{{ authStore.currentActiveRole }}</div>
                </div>
             </div>

             <div class="stats-mini-grid">
                <div class="stat-item">
                    <span class="stat-label">Estado</span>
                    <span class="stat-value pulse-green">{{ user.estado }}</span>
                </div>
                <div class="stat-item">
                    <span class="stat-label">ID Participante</span>
                    <span class="stat-value">#{{ user.documentoIdentidad }}</span>
                </div>
             </div>
             
             <div class="qr-preview-card">
                <p>Tu Identificador Único</p>
                <div class="qr-case">
                    <img :src="`https://api.qrserver.com/v1/create-qr-code/?size=160x160&data=${user.documentoIdentidad}`" alt="Mi QR" />
                </div>
                <small>Úsalo para check-ins rápidos</small>
             </div>
        </div>

        <!-- content Card: Forms -->
        <div class="profile-main-card glass-card">
           <div class="card-tabs">
              <button :class="['tab-btn', { active: activeTab === 'personal' }]" @click="activeTab = 'personal'">Información Personal</button>
              <button :class="['tab-btn', { active: activeTab === 'security' }]" @click="activeTab = 'security'">Seguridad</button>
           </div>

           <form @submit.prevent="updateProfile" class="profile-form-premium">
              <!-- Personal Tab -->
              <div v-if="activeTab === 'personal'" class="tab-content animate-fade-in">
                  <div class="form-grid-premium">
                      <div class="form-group-premium">
                         <label>Nombre(s)</label>
                         <input v-model="form.nombre" required placeholder="Ingresa tus nombres" class="input-premium" />
                      </div>
                      <div class="form-group-premium">
                         <label>Apellido(s)</label>
                         <input v-model="form.apellido" required placeholder="Ingresa tus apellidos" class="input-premium" />
                      </div>
                      <div class="form-group-premium full-width">
                         <label>Correo Institucional</label>
                         <div class="input-with-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>
                            <input v-model="user.email" disabled class="input-premium disabled" />
                         </div>
                         <p class="helper-text-premium"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg> El correo electrónico es gestionado por el administrador.</p>
                      </div>
                  </div>
              </div>

              <!-- Security Tab -->
              <div v-if="activeTab === 'security'" class="tab-content animate-fade-in">
                  <div class="form-grid-premium">
                      <div class="form-group-premium full-width" v-if="form.password">
                         <label>Contraseña Actual <span class="required-dot"></span></label>
                         <input v-model="form.oldPassword" type="password" placeholder="Confirmar identidad para cambiar clave" required class="input-premium highlight" />
                      </div>
                      <div class="form-group-premium">
                         <label>Nueva Contraseña</label>
                         <input v-model="form.password" type="password" placeholder="6+ caracteres" class="input-premium" />
                      </div>
                      <div class="form-group-premium">
                         <label>Confirmar Nueva Contraseña</label>
                         <input v-model="form.confirmPassword" type="password" placeholder="Repite la contraseña" class="input-premium" />
                      </div>
                  </div>
              </div>

              <div class="form-footer-premium">
                 <button type="submit" class="btn-primary btn-premium-action" :disabled="saving">
                    <LoadingSpinner v-if="saving" size="20px" style="margin:0; margin-right: 10px;" />
                    <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="margin-right: 8px;"><path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"></path><polyline points="17 21 17 13 7 13 7 21"></polyline><polyline points="7 3 7 8 15 8"></polyline></svg>
                    {{ saving ? 'Sincronizando...' : 'Actualizar Perfil' }}
                 </button>
              </div>
           </form>
        </div>
    </div>

    <!-- Image Cropper Modal -->
    <CropModal 
      :show="showCropper" 
      :imageSrc="tempImage" 
      @save="onCropSaved" 
      @cancel="showCropper = false" 
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from '../stores/auth';
import api from '../services/api';
import PageHeader from '../components/layout/PageHeader.vue';
import LoadingSpinner from '../components/ui/LoadingSpinner.vue';
import { useNotificationStore } from '../stores/notifications';
import CropModal from '../components/modals/CropModal.vue';

const notificationStore = useNotificationStore();
const authStore = useAuthStore();

const user = ref(null);
const loading = ref(false);
const saving = ref(false);
const error = ref('');
const activeTab = ref('personal');
const fileInput = ref(null);

const form = ref({
    nombre: '',
    apellido: '',
    password: '',
    confirmPassword: '',
    oldPassword: '',
    fotoPerfil: ''
});

// Cropper State
const showCropper = ref(false);
const tempImage = ref('');


const userInitials = computed(() => {
  if (user.value && user.value.nombre) {
    return user.value.nombre.substring(0, 1).toUpperCase() + user.value.apellido.substring(0, 1).toUpperCase();
  }
  return 'YO';
});

const triggerFileInput = () => {
    fileInput.value.click();
};

const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    if (file.size > 2 * 1024 * 1024) {
        notificationStore.showWarning("La imagen es demasiado pesada (Máx 2MB)");
        return;
    }

    const reader = new FileReader();
    reader.onload = (event) => {
        tempImage.value = event.target.result;
        showCropper.value = true;
    };
    reader.readAsDataURL(file);
    // Clear input to allow same file selection
    e.target.value = '';
};

const onCropSaved = (croppedImage) => {
  form.value.fotoPerfil = croppedImage;
  showCropper.value = false;
};

const fetchProfile = async () => {
    const storedUser = authStore.user;
    if (!storedUser || !storedUser.email) {
        error.value = "Sesión no válida. Inicie sesión nuevamente.";
        return;
    }
    
    let id = storedUser.idParticipante || storedUser.id || storedUser.documentoIdentidad; 
    
    loading.value = true;
    error.value = '';

    try {
        const res = await api.get(`/participantes/${id}`);
        user.value = res.data;
        form.value.nombre = user.value.nombre;
        form.value.apellido = user.value.apellido;
        form.value.fotoPerfil = user.value.fotoPerfil || '';
    } catch(err) {
        error.value = "Error: " + (err.response?.data?.message || err.message);
    } finally {
        loading.value = false;
    }
};

const updateProfile = async () => {
    if(!user.value) return;
    
    if (form.value.password || form.value.confirmPassword) {
        if (form.value.password.length < 6) {
            notificationStore.showWarning("La clave debe tener 6+ caracteres.");
            return;
        }
        if (form.value.password !== form.value.confirmPassword) {
            notificationStore.showWarning("Las claves no coinciden.");
            return;
        }
    }

    saving.value = true;
    try {
        const updateData = {
            documentoIdentidad: user.value.documentoIdentidad,
            email: user.value.email,
            nombre: form.value.nombre,
            apellido: form.value.apellido,
            fotoPerfil: form.value.fotoPerfil,
            password: form.value.password || undefined,
            oldPassword: form.value.password ? form.value.oldPassword : undefined
        };
        
        await api.put(`/participantes/${user.value.documentoIdentidad}`, updateData);
        notificationStore.showSuccess("¡Perfil actualizado con éxito!");
        
        form.value.password = '';
        form.value.confirmPassword = '';
        form.value.oldPassword = '';

        // Update auth store
        if(authStore.user) {
            authStore.user.nombre = form.value.nombre;
            authStore.user.apellido = form.value.apellido;
            authStore.user.fotoPerfil = form.value.fotoPerfil;
            localStorage.setItem('user', JSON.stringify(authStore.user));
            // Trigger storage event for navbar to pick up
            window.dispatchEvent(new Event('storage'));
        }
        
        fetchProfile();
    } catch(err) {
        notificationStore.showError(err.response?.data?.message || "Error al actualizar");
    } finally {
        saving.value = false;
    }
};

onMounted(fetchProfile);
</script>

<style scoped>
.profile-container {
    padding-bottom: 4rem;
}

.profile-grid {
    display: grid;
    grid-template-columns: 320px 1fr;
    gap: 2rem;
    margin-top: 1rem;
}

@media (max-width: 992px) {
    .profile-grid {
        grid-template-columns: 1fr;
    }
}

/* Glass Cards */
.glass-card {
    background: rgba(255, 255, 255, 0.7);
    backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.5);
    border-radius: 24px;
    box-shadow: 0 8px 32px rgba(31, 38, 135, 0.07);
    overflow: hidden;
}

/* Sidebar Styles */
.profile-sidebar-card {
    padding: 2.5rem 1.5rem;
    display: flex;
    flex-direction: column;
    align-items: center;
    height: fit-content;
}

.avatar-uploader {
    position: relative;
    margin-bottom: 2rem;
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.avatar-preview-container {
    position: relative;
    width: 140px;
    height: 140px;
    border-radius: 40px;
    background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%);
    box-shadow: 0 20px 40px -10px rgba(99, 102, 241, 0.3);
    margin-bottom: 1.5rem;
    display: flex;
    justify-content: center;
    align-items: center;
    border: 4px solid white;
}

.avatar-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 36px;
}

.avatar-initials {
    font-size: 3rem;
    font-weight: 800;
    color: white;
}

.zoom-control { display: none; }

.avatar-preview-container:hover .avatar-overlay {
    opacity: 1;
}

.avatar-overlay {
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.4);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    color: white;
    cursor: pointer;
    opacity: 0;
    transition: all 0.3s ease;
    border-radius: 40px;
    backdrop-filter: blur(2px);
}

.avatar-overlay span {
    font-size: 0.75rem;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.05em;
}

.uploader-info {
    text-align: center;
}

.uploader-info h3 {
    font-size: 1.5rem;
    font-weight: 800;
    color: #1e293b;
    margin: 0;
}

.active-badge {
    background: #f0f4ff;
    color: #4f46e5;
    padding: 0.35rem 1rem;
    border-radius: 12px;
    font-size: 0.8rem;
    font-weight: 700;
    margin-top: 0.5rem;
    display: inline-block;
    text-transform: uppercase;
    letter-spacing: 0.05em;
}

.stats-mini-grid {
    width: 100%;
    display: grid;
    grid-template-columns: 1fr;
    gap: 1rem;
    margin: 2rem 0;
}

.stat-item {
    background: rgba(255, 255, 255, 0.5);
    padding: 1rem;
    border-radius: 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.stat-label {
    font-size: 0.8rem;
    font-weight: 600;
    color: #64748b;
}

.stat-value {
    font-size: 0.9rem;
    font-weight: 800;
    color: #334155;
}

.pulse-green {
    color: #10b981;
    display: flex;
    align-items: center;
}

.pulse-green::before {
    content: '';
    width: 8px;
    height: 8px;
    background: #10b981;
    border-radius: 50%;
    margin-right: 8px;
    animation: pulse 2s infinite;
}

/* QR Card */
.qr-preview-card {
    width: 100%;
    background: #1e293b;
    border-radius: 20px;
    padding: 1.5rem;
    color: white;
    text-align: center;
}

.qr-preview-card p {
    font-size: 0.8rem;
    font-weight: 600;
    margin-bottom: 1rem;
    opacity: 0.8;
}

.qr-case {
    background: white;
    padding: 0.75rem;
    border-radius: 16px;
    display: inline-block;
    margin-bottom: 1rem;
}

.qr-case img {
    display: block;
}

.qr-preview-card small {
    display: block;
    font-size: 0.7rem;
    opacity: 0.6;
}

/* Main Content Styles */
.profile-main-card {
    padding: 2.5rem;
}

.card-tabs {
    display: flex;
    gap: 1.5rem;
    border-bottom: 1px solid #e2e8f0;
    margin-bottom: 2.5rem;
}

.tab-btn {
    padding: 1rem 0;
    font-size: 1rem;
    font-weight: 700;
    color: #64748b;
    background: none;
    border: none;
    cursor: pointer;
    position: relative;
    transition: all 0.3s;
}

.tab-btn:hover {
    color: #334155;
}

.tab-btn.active {
    color: #6366f1;
}

.tab-btn.active::after {
    content: '';
    position: absolute;
    bottom: -1px;
    left: 0;
    width: 100%;
    height: 3px;
    background: #6366f1;
    border-radius: 3px;
}

.form-grid-premium {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 2rem;
}

.full-width { grid-column: 1 / -1; }

.form-group-premium {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
}

.form-group-premium label {
    font-size: 0.9rem;
    font-weight: 800;
    color: #334155;
    text-transform: uppercase;
    letter-spacing: 0.02em;
}

.input-premium {
    padding: 1rem 1.25rem;
    border-radius: 16px;
    border: 2px solid #e2e8f0;
    background: #f8fafc;
    font-size: 1rem;
    color: #1e293b;
    transition: all 0.3s;
}

.input-premium:focus {
    border-color: #6366f1;
    background: white;
    box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
    outline: none;
}

.input-premium.disabled {
    background: #f1f5f9;
    color: #94a3b8;
    cursor: not-allowed;
    border-color: #f1f5f9;
}

.input-premium.highlight {
    border-color: #a855f7;
    background: #fcfaff;
}

.input-with-icon {
    position: relative;
}

.input-with-icon svg {
    position: absolute;
    left: 1.25rem;
    top: 50%;
    transform: translateY(-50%);
    color: #94a3b8;
}

.input-with-icon .input-premium {
    padding-left: 3.5rem;
    width: 100%;
}

.helper-text-premium {
    font-size: 0.8rem;
    color: #94a3b8;
    display: flex;
    align-items: center;
    gap: 6px;
    margin-top: 0.5rem;
}

.required-dot {
    display: inline-block;
    width: 6px;
    height: 6px;
    background: #ef4444;
    border-radius: 50%;
    vertical-align: middle;
    margin-left: 4px;
}

.form-footer-premium {
    margin-top: 3rem;
    padding-top: 2rem;
    border-top: 1px solid #e2e8f0;
    display: flex;
    justify-content: flex-end;
}

.btn-premium-action {
    padding: 1.25rem 2.5rem;
    font-weight: 800;
    font-size: 1.1rem;
    letter-spacing: -0.01em;
    border-radius: 18px;
    display: flex;
    align-items: center;
    box-shadow: 0 10px 20px -5px rgba(99, 102, 241, 0.4);
}

.btn-premium-action:hover {
    transform: translateY(-2px);
    box-shadow: 0 15px 30px -5px rgba(99, 102, 241, 0.5);
}

/* Error/Notification */
.error-notification {
    background: #fef2f2;
    color: #b91c1c;
    padding: 1.25rem;
    border-radius: 16px;
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 2rem;
    border: 1px solid #fee2e2;
}

@keyframes pulse {
    0% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7); }
    70% { transform: scale(1); box-shadow: 0 0 0 10px rgba(16, 185, 129, 0); }
    100% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); }
}
</style>
