<template>
  <BaseModal 
    :show="confirmStore.show" 
    :title="confirmStore.title" 
    max-width="450px"
    @close="confirmStore.cancel"
  >
    <div class="confirm-content">
      <div class="confirm-icon" :class="confirmStore.type">
        <svg v-if="confirmStore.type === 'danger'" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
        <svg v-else-if="confirmStore.type === 'warning'" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>
        <svg v-else width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"></path><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>
      </div>
      <p class="confirm-message">{{ confirmStore.message }}</p>
    </div>

    <template #footer>
      <button class="btn-secondary" @click="confirmStore.cancel">
        {{ confirmStore.cancelText }}
      </button>
      <button 
        :class="confirmStore.type === 'danger' ? 'btn-danger' : 'btn-primary'" 
        @click="confirmStore.confirm"
      >
        {{ confirmStore.confirmText }}
      </button>
    </template>
  </BaseModal>
</template>

<script setup>
import { useConfirmStore } from '../../stores/confirm';
import BaseModal from './BaseModal.vue';

const confirmStore = useConfirmStore();
</script>

<style scoped>
.confirm-content {
  text-align: center;
  padding: 1rem 0;
}

.confirm-icon {
  display: flex;
  justify-content: center;
  margin-bottom: 1.5rem;
}

.confirm-icon.danger { color: #e53e3e; }
.confirm-icon.warning { color: #d69e2e; }
.confirm-icon.primary { color: #667eea; }

.confirm-message {
  font-size: 1.1rem;
  color: #4a5568;
  line-height: 1.5;
  margin: 0;
}

.btn-danger {
  background: #e53e3e;
  color: white;
  border: none;
  padding: 0.6rem 1.5rem;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-danger:hover {
  background: #c53030;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(229, 62, 62, 0.2);
}

.btn-primary {
  background: var(--primary-gradient);
  color: white;
  border: none;
  padding: 0.6rem 1.5rem;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-secondary {
  background: #f1f5f9;
  color: #475569;
  border: none;
  padding: 0.6rem 1.5rem;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
}

.btn-secondary:hover {
  background: #e2e8f0;
}
</style>
