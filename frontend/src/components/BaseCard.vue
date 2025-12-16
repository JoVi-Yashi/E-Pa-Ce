<template>
  <div class="base-card" :class="{ elevated }">
    <div v-if="$slots.header" class="card-header">
      <slot name="header"></slot>
    </div>
    <div class="card-body">
      <slot></slot>
    </div>
    <div v-if="$slots.footer" class="card-footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  elevated: {
    type: Boolean,
    default: false
  }
});
</script>

<style scoped>
.base-card {
  background: white;
  border-radius: 15px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: all 0.3s ease;
}

.base-card.elevated {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.base-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.1);
}

.card-header {
  padding: 1.5rem 1.5rem 1rem;
  border-bottom: 1px solid #eee;
}

.card-body {
  padding: 1.5rem;
}

.card-footer {
  padding: 1rem 1.5rem 1.5rem;
  border-top: 1px solid #eee;
  background-color: #f8f9fa;
}

@media screen and (max-width: 768px) {
  .card-header,
  .card-body,
  .card-footer {
    padding: 1rem;
  }
}
</style>