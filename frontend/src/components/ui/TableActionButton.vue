<template>
  <button 
    class="icon-btn" 
    :class="[type, { 'disabled': disabled, 'active': active }]" 
    :title="title"
    :disabled="disabled"
    @click="!disabled && $emit('click')"
  >
    <slot></slot>
  </button>
</template>

<script setup>
defineProps({
  type: {
    type: String,
    default: 'default', // default, edit, delete, view, security, security-locked
    validator: (value) => ['default', 'edit', 'delete', 'view', 'security', 'security-locked'].includes(value)
  },
  title: String,
  disabled: {
    type: Boolean,
    default: false
  },
  active: {
    type: Boolean,
    default: false
  }
});

defineEmits(['click']);
</script>

<style scoped>
.icon-btn {
  background: transparent;
  color: #718096;
  padding: 0.5rem;
  margin-left: 0.25rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  position: relative;
  outline: none;
}

/* Hover effects ONLY for non-disabled buttons */
.icon-btn:not(:disabled):not(.disabled):hover {
  background: #f8fafc;
  transform: translateY(-2px);
  color: #4a5568;
}

/* Specific Types Hover - strictly guarded */
.icon-btn.edit:not(:disabled):not(.disabled):hover { color: #667eea; background: #ebf4ff; }
.icon-btn.delete:not(:disabled):not(.disabled):hover { color: #e53e3e; background: #fff5f5; }
.icon-btn.view:not(:disabled):not(.disabled):hover { color: #38a169; background: #f0fff4; }

/* Security Types */
.icon-btn.security { color: #667eea; background: rgba(102, 126, 234, 0.1); }
.icon-btn.security-locked { color: #94a3b8; background: #f1f5f9; }

/* Active security state */
.icon-btn.active.security {
  background: #ebf4ff;
  color: #667eea;
  box-shadow: inset 0 2px 4px rgba(0,0,0,0.05);
}

/* TOTAL DISABLEMENT - No interaction, no animation, no hover */
.icon-btn:disabled,
.icon-btn.disabled {
  opacity: 0.25 !important;
  cursor: not-allowed !important;
  filter: grayscale(1) !important;
  pointer-events: none !important;
  transform: none !important;
  transition: none !important;
  box-shadow: none !important;
  background: transparent !important;
}

/* Ensure icons inside don't capture anything */
.icon-btn:disabled *,
.icon-btn.disabled * {
  pointer-events: none !important;
}
</style>
