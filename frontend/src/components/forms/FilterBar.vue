<template>
  <div class="filters-container">
    <!-- Search Input -->
    <div class="filter-item search">
      <label>Búsqueda</label>
      <div class="search-wrapper">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
        </svg>
        <input 
          :value="modelValue"
          @input="$emit('update:modelValue', $event.target.value)"
          type="text" 
          :placeholder="placeholder" 
        />
      </div>
    </div>

    <!-- Extra Slot (for dropdowns) -->
    <slot></slot>

    <!-- Clear Button -->
    <div class="filter-actions" v-if="hasFilters">
      <button class="btn-text" @click="$emit('clear')">Limpiar</button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  modelValue: { type: String, default: '' },
  placeholder: { type: String, default: 'Buscar...' },
  hasFilters: { type: Boolean, default: false }
});

defineEmits(['update:modelValue', 'clear']);
</script>

<style scoped>
.filters-container {
    display: flex;
    gap: 1.5rem;
    margin-bottom: 1.5rem;
    align-items: flex-end;
    flex-wrap: wrap;
    background: #f8fafc;
    padding: 1rem;
    border-radius: 12px;
    border: 1px solid #edf2f7;
}

.filter-item {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.filter-item label {
    font-size: 0.8rem;
    font-weight: 600;
    color: #718096;
    text-transform: uppercase;
}

.filter-item.search {
    flex: 1;
    min-width: 200px;
}

.search-wrapper {
    position: relative;
    display: flex;
    align-items: center;
}

.search-wrapper svg {
    position: absolute;
    left: 10px;
    color: #a0aec0;
}

.search-wrapper input {
    width: 100%;
    padding: 0.5rem 0.5rem 0.5rem 2.2rem;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    font-size: 0.9rem;
    transition: all 0.2s;
}

.search-wrapper input:focus {
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    outline: none;
}

.btn-text {
    background: none;
    border: none;
    color: #667eea;
    font-weight: 600;
    cursor: pointer;
    text-decoration: underline;
    margin-bottom: 0.5rem;
}

/* Specific styling for slotted selects needs to be global or deep, 
   but since this is a standardized component, we can expect standard classes
   or provide them here via deep selector if we want to encapsulate them.
   Or we assume the consumer uses standard 'filter-item' structure inside slot.
*/
:deep(.filter-item select) {
    padding: 0.5rem;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    background: white;
    min-width: 150px;
    font-size: 0.9rem;
}

@media (max-width: 768px) {
    .filters-container {
        flex-direction: column;
        align-items: stretch;
    }
    .filter-item {
        width: 100%;
    }
    :deep(.filter-item select) {
        width: 100%;
    }
}
</style>
