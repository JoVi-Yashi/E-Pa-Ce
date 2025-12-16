<template>
  <button 
    :class="[
      'base-button',
      variant,
      size,
      { block, disabled: isDisabled, loading }
    ]"
    :disabled="isDisabled || loading"
    @click="handleClick"
  >
    <span v-if="loading" class="button-spinner"></span>
    <span :class="{ 'button-content': loading }">
      <slot></slot>
    </span>
  </button>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  variant: {
    type: String,
    default: 'primary', // primary, secondary, danger, success, outline
    validator: (value) => ['primary', 'secondary', 'danger', 'success', 'outline'].includes(value)
  },
  size: {
    type: String,
    default: 'medium', // small, medium, large
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  block: {
    type: Boolean,
    default: false
  },
  disabled: {
    type: Boolean,
    default: false
  },
  loading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['click']);

const isDisabled = computed(() => props.disabled || props.loading);

const handleClick = (event) => {
  if (!isDisabled.value) {
    emit('click', event);
  }
};
</script>

<style scoped>
.base-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  position: relative;
  overflow: hidden;
  white-space: nowrap;
  text-decoration: none;
}

.base-button:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.3);
}

.base-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.base-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* Sizes */
.base-button.small {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
}

.base-button.medium {
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
}

.base-button.large {
  padding: 1rem 2rem;
  font-size: 1.125rem;
}

/* Variants */
.base-button.primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.base-button.secondary {
  background: #f0f2f5;
  color: #333;
}

.base-button.danger {
  background: linear-gradient(135deg, #c0392b, #e74c3c);
  color: white;
}

.base-button.success {
  background: linear-gradient(135deg, #27ae60, #2ecc71);
  color: white;
}

.base-button.outline {
  background: transparent;
  color: #667eea;
  border: 2px solid #667eea;
}

.base-button.outline:hover:not(:disabled) {
  background: #667eea;
  color: white;
}

/* Block button */
.base-button.block {
  width: 100%;
  display: flex;
}

/* Loading state */
.button-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top-color: currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 8px;
}

.button-content {
  display: flex;
  align-items: center;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>