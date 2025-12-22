<template>
  <BaseModal
    :show="show"
    title="¡Tu sesión está por expirar!"
    maxWidth="450px"
    @close="handleLogout"
  >
    <div class="session-modal-content">
      <div class="warning-icon-wrapper">
        <div class="pulse-ring"></div>
        <div class="warning-icon">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
            <circle cx="12" cy="11" r="3"></circle>
            <line x1="12" y1="11" x2="12" y2="11.01"></line>
          </svg>
        </div>
      </div>
      
      <h3 class="expiry-title">Seguridad de Sesión</h3>
      <p class="expiry-description">
        Por motivos de seguridad, tu sesión expirará pronto.
        <br>
        <span class="highlight">¿Deseas extenderla por 20 minutos más?</span>
      </p>

      <div class="countdown-display">
        <span class="countdown-number">{{ countdownText }}</span>
        <span class="countdown-label">segundos restantes</span>
      </div>
      
      <div class="timer-container">
        <div class="timer-bar-bg">
          <div class="timer-bar-fill" :style="{ width: progress + '%' }"></div>
        </div>
      </div>
    </div>

    <template #footer>
      <button class="btn-secondary" @click="handleLogout">Cerrar Sesión</button>
      <button class="btn-primary" @click="handleExtend" :disabled="extending">
        {{ extending ? 'Extendiendo...' : 'Extender Sesión' }}
      </button>
    </template>
  </BaseModal>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import { useAuthStore } from '../../stores/auth';
import BaseModal from '../modals/BaseModal.vue';

const authStore = useAuthStore();
const show = ref(false);
const extending = ref(false);
const progress = ref(100);
const countdownText = ref('60');
let timer = null;
let interval = null;

const checkToken = () => {
  if (!authStore.token || !authStore.expiresAt) return;
  
  const exp = parseInt(authStore.expiresAt);
  const now = Date.now();
  const timeLeft = exp - now;

  // Show warning if less than 60 seconds left
  if (timeLeft > 0 && timeLeft < 60000 && !show.value) {
    show.value = true;
    startCountDown(timeLeft);
  } else if (timeLeft <= 0) {
      authStore.logout();
      window.location.href = '/login';
  }
};

const startCountDown = (duration) => {
    const start = Date.now();
    interval = setInterval(() => {
        const now = Date.now();
        const elapsed = now - start;
        const remaining = Math.max(0, duration - elapsed);
        
        progress.value = (remaining / duration) * 100;
        countdownText.value = Math.ceil(remaining / 1000).toString();
        
        if (remaining <= 0) {
            handleLogout();
        }
    }, 50); // Higher frequency for smoother bar
};

const handleExtend = async () => {
  extending.value = true;
  try {
    await authStore.refreshToken();
    show.value = false;
    if (interval) clearInterval(interval);
  } finally {
    extending.value = false;
  }
};

const handleLogout = () => {
    show.value = false;
    if (interval) clearInterval(interval);
    authStore.logout();
    window.location.href = '/login';
};

onMounted(() => {
  timer = setInterval(checkToken, 10000); // Check every 10s
});

onUnmounted(() => {
  if (timer) clearInterval(timer);
  if (interval) clearInterval(interval);
});
</script>

<style scoped>
.session-modal-content {
  text-align: center;
  padding: 1.5rem 0.5rem;
}

.warning-icon-wrapper {
  position: relative;
  width: 80px;
  height: 80px;
  margin: 0 auto 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.warning-icon {
  width: 100%;
  height: 100%;
  background: #fffbeb;
  color: #f59e0b;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
  box-shadow: 0 10px 15px -3px rgba(245, 158, 11, 0.2);
}

.pulse-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  background: #fef3c7;
  border-radius: 50%;
  animation: pulse-spread 2s infinite;
  z-index: 1;
}

@keyframes pulse-spread {
  0% { transform: scale(1); opacity: 0.8; }
  100% { transform: scale(1.6); opacity: 0; }
}

.expiry-title {
  font-size: 1.25rem;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.expiry-description {
  color: #64748b;
  font-size: 0.95rem;
  line-height: 1.6;
  margin-bottom: 2rem;
}

.highlight {
  color: #4f46e5;
  font-weight: 700;
  display: block;
  margin-top: 0.5rem;
  font-size: 1rem;
}

.countdown-display {
  display: flex;
  flex-direction: column;
  margin-bottom: 2rem;
}

.countdown-number {
  font-size: 3rem;
  font-weight: 900;
  color: #1e293b;
  font-family: 'JetBrains Mono', monospace;
  line-height: 1;
}

.countdown-label {
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  color: #94a3b8;
  letter-spacing: 0.1em;
  margin-top: 0.25rem;
}

.timer-container {
  width: 100%;
  padding: 0 1.5rem;
}

.timer-bar-bg {
  height: 6px;
  background: #f1f5f9;
  border-radius: 10px;
  overflow: hidden;
}

.timer-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #f59e0b, #ef4444);
  border-radius: 10px;
  transition: width 0.05s linear;
}

/* Modal Footer Buttons */
:deep(.modal-footer) {
  display: flex;
  gap: 1rem;
  padding: 1.5rem;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
}

.btn-secondary {
  flex: 1;
  padding: 0.875rem 1.5rem;
  background: white;
  color: #475569;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-weight: 700;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.btn-secondary:hover {
  background: #fef2f2;
  border-color: #fecaca;
  color: #dc2626;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px -1px rgba(220, 38, 38, 0.1);
}

.btn-secondary:active {
  transform: translateY(0);
}

.btn-primary {
  flex: 1;
  padding: 0.875rem 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 10px 20px -5px rgba(102, 126, 234, 0.4);
  position: relative;
  overflow: hidden;
}

.btn-primary::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  opacity: 0;
  transition: opacity 0.3s;
}

.btn-primary:hover::before {
  opacity: 1;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 15px 30px -5px rgba(102, 126, 234, 0.5);
}

.btn-primary:active {
  transform: translateY(-1px);
  box-shadow: 0 8px 15px -5px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.btn-primary span,
.btn-secondary span {
  position: relative;
  z-index: 1;
}
</style>
