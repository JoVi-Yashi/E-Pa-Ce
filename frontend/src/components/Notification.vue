<template>
  <transition name="notification">
    <div 
      v-if="visible" 
      :class="['notification', type]" 
      @click="hide"
    >
      <div class="notification-content">
        <span class="notification-message">{{ message }}</span>
        <button class="notification-close">&times;</button>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';

const props = defineProps({
  message: {
    type: String,
    required: true
  },
  type: {
    type: String,
    default: 'info' // success, error, warning, info
  },
  duration: {
    type: Number,
    default: 5000 // milliseconds
  }
});

const visible = ref(true);
let timer = null;

const hide = () => {
  visible.value = false;
};

onMounted(() => {
  if (props.duration > 0) {
    timer = setTimeout(() => {
      hide();
    }, props.duration);
  }
});

onBeforeUnmount(() => {
  if (timer) {
    clearTimeout(timer);
  }
});
</script>

<style scoped>
.notification {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  min-width: 300px;
  max-width: 500px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  cursor: pointer;
  animation: slideInRight 0.3s ease-out;
}

.notification-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.25rem;
}

.notification-message {
  flex: 1;
  font-weight: 500;
}

.notification-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0;
  margin-left: 1rem;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s ease;
}

.notification-close:hover {
  background-color: rgba(0, 0, 0, 0.1);
}

/* Notification types */
.notification.success {
  background: linear-gradient(135deg, #27ae60, #2ecc71);
  color: white;
}

.notification.error {
  background: linear-gradient(135deg, #c0392b, #e74c3c);
  color: white;
}

.notification.warning {
  background: linear-gradient(135deg, #f39c12, #f1c40f);
  color: white;
}

.notification.info {
  background: linear-gradient(135deg, #2980b9, #3498db);
  color: white;
}

/* Transitions */
.notification-enter-active,
.notification-leave-active {
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.notification-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.notification-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(100%);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@media screen and (max-width: 768px) {
  .notification {
    min-width: 250px;
    right: 10px;
    left: 10px;
  }
}
</style>