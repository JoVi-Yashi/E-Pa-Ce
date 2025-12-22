<template>
  <div class="empty-state">
    <div class="icon-container">
        <slot name="icon">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="8" y1="12" x2="16" y2="12"></line>
            </svg>
        </slot>
    </div>
    <h3>{{ title }}</h3>
    <p>{{ description }}</p>
    <div class="actions" v-if="$slots.actions">
        <slot name="actions"></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: 'No hay datos'
  },
  description: {
    type: String,
    default: 'No se encontraron registros para mostrar.'
  }
});
</script>

<style scoped>
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  color: var(--text-secondary);
  background: white;
  border-radius: 12px;
  border: 2px dashed #e2e8f0;
}

.icon-container {
  color: #cbd5e0;
  margin-bottom: 1rem;
}

h3 {
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

p {
  margin-bottom: 1.5rem;
  max-width: 400px;
  margin-left: auto;
  margin-right: auto;
}

.actions {
    display: flex;
    justify-content: center;
    gap: 1rem;
}
</style>
