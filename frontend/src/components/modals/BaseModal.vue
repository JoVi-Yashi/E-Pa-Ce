<template>
  <transition name="modal-fade">
    <div v-if="show" class="modal-overlay" @click.self="$emit('close')">
      <div class="modal-content animate-slide-up" :style="{ maxWidth: maxWidth }">
        <div class="modal-header">
           <h3>{{ title }}</h3>
           <button class="close-btn" @click="$emit('close')">&times;</button>
        </div>
        
        <div class="modal-body">
            <slot></slot>
        </div>

        <div class="modal-footer" v-if="$slots.footer">
            <slot name="footer"></slot>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
defineProps({
  show: {
    type: Boolean,
    required: true
  },
  title: {
    type: String,
    default: 'Modal'
  },
  maxWidth: {
    type: String,
    default: '600px'
  }
});

defineEmits(['close']);
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  backdrop-filter: blur(8px);
  padding: 1rem;
}

.modal-content {
  background: white;
  border-radius: 20px;
  width: 100%;
  max-height: calc(100vh - 40px);
  overflow-y: auto;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  display: flex;
  flex-direction: column;
  position: relative;
}

@media (max-width: 600px) {
  .modal-overlay {
    padding: 0;
    align-items: flex-end; /* Mobile Bottom Sheet feel */
  }
  .modal-content {
    border-radius: 24px 24px 0 0;
    max-height: 95vh;
  }
}

.modal-header {
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  background: white;
  z-index: 10;
}

.modal-header h3 {
    margin: 0;
    font-size: 1.2rem;
    font-weight: 700;
    color: var(--text-primary);
}

.modal-body {
  padding: 1.5rem;
  flex-grow: 1;
  overflow-y: auto;
}

@media (max-width: 600px) {
  .modal-body {
    padding: 1.25rem;
  }
}

.modal-footer {
  padding: 1.25rem 1.5rem;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  background: #f8fafc;
}

@media (max-width: 600px) {
  .modal-footer {
    flex-direction: column;
    padding: 1rem 1.25rem;
  }
  .modal-footer .btn-secondary,
  .modal-footer .btn-primary,
  .modal-footer button {
    width: 100%;
    margin-bottom: 0.5rem;
  }
}

.close-btn {
  background: transparent;
  color: #a0aec0;
  font-size: 1.75rem;
  padding: 0;
  line-height: 1;
  box-shadow: none;
  cursor: pointer;
  border: none;
}

.close-btn:hover {
  color: var(--text-primary);
}

/* Transitions */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}
</style>
