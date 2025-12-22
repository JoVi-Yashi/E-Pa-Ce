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
    <div class="shimmer" v-if="variant === 'primary' && !isDisabled"></div>
    <span v-if="loading" class="button-spinner"></span>
    <span :class="{ 'button-content': loading }" class="inner-content">
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
  border-radius: 50px; /* Modern pill shape */
  cursor: pointer;
  font-weight: 800; /* More impact */
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  text-transform: uppercase;
  letter-spacing: 0.8px;
  position: relative;
  overflow: hidden;
  white-space: nowrap;
  text-decoration: none;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.base-button:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.4);
}

.base-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.base-button:active:not(:disabled) {
  transform: translateY(1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.base-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* Shimmer Effect */
.shimmer {
  position: absolute;
  top: 0;
  left: -100%;
  width: 50%;
  height: 100%;
  background: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0) 0%,
    rgba(255, 255, 255, 0.3) 50%,
    rgba(255, 255, 255, 0) 100%
  );
  transform: skewX(-25deg);
  transition: none;
}

.base-button.primary:hover .shimmer {
  animation: shimmer 1.2s infinite;
}

@keyframes shimmer {
  0% { left: -100%; }
  100% { left: 200%; }
}

/* Inner Content */
.inner-content {
  position: relative;
  z-index: 1;
}

/* Sizes */
.base-button.small {
  padding: 0.6rem 1.2rem;
  font-size: 0.8rem;
}

.base-button.medium {
  padding: 0.8rem 1.6rem;
  font-size: 0.9rem;
}

.base-button.large {
  padding: 1rem 2.5rem;
  font-size: 1rem;
}

/* Variants */
.base-button.primary {
  background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%);
  color: white;
}

.base-button.primary::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, rgba(255,255,255,0.1), rgba(255,255,255,0));
  opacity: 0;
  transition: opacity 0.3s;
}

.base-button.primary:hover::before {
  opacity: 1;
}

.base-button.secondary {
  background: #f8fafc;
  color: #475569;
  border: 1px solid #e2e8f0;
}

.base-button.danger {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
}

.base-button.success {
  background: linear-gradient(135deg, #22c55e, #16a34a);
  color: white;
}

.base-button.outline {
  background: white;
  color: #6366f1;
  border: 2px solid #6366f1;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.1);
}

.base-button.outline:hover:not(:disabled) {
  background: #6366f1;
  color: white;
  box-shadow: 0 10px 20px rgba(99, 102, 241, 0.2);
}

/* Block button */
.base-button.block {
  width: 100%;
}

/* Loading state */
.button-spinner {
  width: 18px;
  height: 18px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-right: 10px;
  position: relative;
  z-index: 1;
}

.button-content {
  display: flex;
  align-items: center;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
