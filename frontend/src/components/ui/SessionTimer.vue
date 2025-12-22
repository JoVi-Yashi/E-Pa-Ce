<template>
  <div v-if="authStore.isAuthenticated" 
       class="session-timer-floating" 
       :class="['corner-' + currentCorner, { 'timer-low': isLowTime }]"
  >
    <div class="timer-wrapper" @click="nextCorner">
      <div class="timer-icon">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
          <polyline points="12 6 12 12 16 14"></polyline>
        </svg>
      </div>
      <div class="timer-details">
        <div class="timer-text">
          <span class="label">Sesión:</span>
          <span class="countdown">{{ timeRemaining }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import { useAuthStore } from '../../stores/auth';

const authStore = useAuthStore();
const timeRemaining = ref('..:..');
const isLowTime = ref(false);
const currentCorner = ref(0); // 0: BL, 1: TL, 2: TR, 3: BR
let interval = null;

const nextCorner = () => {
    currentCorner.value = (currentCorner.value + 1) % 4;
};

const calculateTime = () => {
  const expiryStr = authStore.getExpiresAt; // Use the improved getter
  if (!expiryStr) {
      timeRemaining.value = '--:--';
      return;
  }
  
  const now = new Date().getTime();
  const expiry = parseInt(expiryStr);
  const diff = expiry - now;
  
  if (diff <= 0) {
    timeRemaining.value = 'EXP';
    isLowTime.value = true;
    return;
  }
  
  const minutes = Math.floor(diff / 60000);
  const seconds = Math.floor((diff % 60000) / 1000);
  
  timeRemaining.value = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  isLowTime.value = diff < 120000; // 2 minutes
};

// Re-calculate if token changes
watch(() => authStore.token, () => {
    calculateTime();
});

onMounted(() => {
  calculateTime();
  interval = setInterval(calculateTime, 1000);
});

onUnmounted(() => {
  if (interval) clearInterval(interval);
});
</script>

<style scoped>
.session-timer-floating {
  position: fixed;
  z-index: 2000;
  cursor: pointer;
  transition: all 0.6s cubic-bezier(0.19, 1, 0.22, 1);
  display: flex;
  align-items: center;
  pointer-events: auto;
}

/* Base "Hidden" State */
.timer-wrapper {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  border: 1.5px solid rgba(102, 126, 234, 0.2);
  border-radius: 50px;
  padding: 10px;
  display: flex;
  align-items: center;
  gap: 0;
  width: 44px; /* Only icon visible */
  height: 44px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

/* Hover "Expanded" State */
.session-timer-floating:hover .timer-wrapper {
  width: 140px;
  padding: 8px 16px;
  gap: 12px;
  background: white;
  border-color: var(--primary-color);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.25);
  transform: scale(1.1);
}

.timer-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-color);
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.session-timer-floating:hover .timer-icon {
  transform: rotate(360deg);
}

.timer-details {
  opacity: 0;
  transform: translateX(-10px);
  transition: all 0.3s ease 0.1s;
  pointer-events: none;
  white-space: nowrap;
}

.session-timer-floating:hover .timer-details {
  opacity: 1;
  transform: translateX(0);
  pointer-events: auto;
}

.timer-text {
  display: flex;
  flex-direction: column;
  line-height: 1.1;
}

.label {
  font-size: 0.6rem;
  font-weight: 800;
  text-transform: uppercase;
  color: #94a3b8;
  letter-spacing: 0.08em;
}

.countdown {
  font-size: 1rem;
  font-weight: 900;
  color: #1e293b;
  font-variant-numeric: tabular-nums;
  font-family: 'JetBrains Mono', 'Courier New', monospace; /* Monospace for steady timer */
}

/* Corner Positions */
.corner-0 { bottom: 2rem; left: 2rem; }
.corner-1 { top: 4.5rem; left: 2rem; }
.corner-2 { top: 4.5rem; right: 2rem; }
.corner-3 { bottom: 2rem; right: 2rem; }

/* Mirror handling for right corners */
.corner-2 .timer-wrapper, .corner-3 .timer-wrapper {
    flex-direction: row-reverse;
}
.corner-2 .timer-details, .corner-3 .timer-details {
    transform: translateX(10px);
}
.session-timer-floating.corner-2:hover .timer-details, 
.session-timer-floating.corner-3:hover .timer-details {
    transform: translateX(0);
}

/* Low Time State */
.timer-low .timer-wrapper {
    background: rgba(255, 245, 245, 0.9);
    border-color: #feb2b2;
}
.timer-low .timer-icon {
    color: #f56565;
    animation: bounce 1s infinite;
}
.timer-low .countdown {
    color: #e53e3e;
}

@keyframes bounce {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-3px); }
}

/* Mobile Adjustments */
@media (max-width: 768px) {
    .corner-0 { bottom: 1rem; left: 1rem; }
    .corner-1 { top: 4rem; left: 1rem; }
    .corner-2 { top: 4rem; right: 1rem; }
    .corner-3 { bottom: 1rem; right: 1rem; }
    
    .session-timer-floating:hover .timer-wrapper {
        width: 120px;
        transform: scale(1.05);
    }
}
</style>
