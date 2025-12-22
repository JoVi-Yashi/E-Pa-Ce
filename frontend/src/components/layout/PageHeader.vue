<template>
  <div class="page-header-container slide-down">
    <div class="glass-header">
      <div class="header-main-content">
        <h1>{{ title }}</h1>
        <p v-if="subtitle" class="glass-subtitle">{{ subtitle }}</p>
      </div>
    </div>
    <div class="header-actions">
      <slot name="actions"></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    default: ''
  }
});
</script>

<style scoped>
/* Scoped styles are minimal as they rely primarily on global.css for consistency */
.page-header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2.5rem;
  width: 100%;
}

.glass-header {
  flex: 1;
  margin-bottom: 0 !important; /* Reset global margin when in container */
  max-width: fit-content;
}

.header-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
  justify-content: flex-end;
}

@media (max-width: 992px) {
  .page-header-container {
    flex-direction: column;
    align-items: stretch;
    gap: 1.5rem;
  }
  
  .glass-header {
    max-width: 100%;
  }
  
  .header-actions {
    justify-content: flex-start;
  }
}
</style>
