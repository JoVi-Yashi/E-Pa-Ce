<template>
  <div class="pagination-container">
    <div class="pagination-settings">
      <div class="settings-label-wrapper">
        <svg class="subtle-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
          <line x1="3" y1="9" x2="21" y2="9"></line>
          <line x1="3" y1="15" x2="21" y2="15"></line>
          <line x1="9" y1="3" x2="9" y2="21"></line>
        </svg>
        <label class="per-page-label">Mostrar:</label>
      </div>
      <div class="custom-select-wrapper">
        <select 
          :value="itemsPerPage" 
          @change="$emit('update:itemsPerPage', parseInt($event.target.value))"
          class="per-page-select"
        >
          <option :value="5">5</option>
          <option :value="10">10</option>
          <option :value="20">20</option>
          <option :value="50">50</option>
        </select>
        <svg class="select-arrow-icon" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
          <polyline points="6 9 12 15 18 9"></polyline>
        </svg>
      </div>
      <span class="total-count-info">de {{ totalItems }} registros</span>
    </div>

    <div class="pagination-controls">
      <button 
        class="pagination-btn nav-btn" 
        :disabled="currentPage === 1" 
        @click="$emit('update:currentPage', currentPage - 1)"
      >
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
        <span>Anterior</span>
      </button>
      
      <div class="pagination-pages">
         <svg class="subtle-page-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
           <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"></path>
           <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"></path>
         </svg>
         <span class="current-page">{{ currentPage }}</span>
         <span class="divider">/</span>
         <span class="total-pages">{{ totalPages }}</span>
      </div>

      <button 
        class="pagination-btn nav-btn" 
        :disabled="currentPage === totalPages || totalPages === 0" 
        @click="$emit('update:currentPage', currentPage + 1)"
      >
        <span>Siguiente</span>
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <polyline points="9 18 15 12 9 6"></polyline>
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  totalItems: {
    type: Number,
    required: true
  },
  itemsPerPage: {
    type: Number,
    default: 10
  },
  currentPage: {
    type: Number,
    default: 1
  }
});

const emit = defineEmits(['update:currentPage', 'update:itemsPerPage']);

const totalPages = computed(() => {
  return Math.ceil(props.totalItems / props.itemsPerPage);
});
</script>

<style scoped>
.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background: white;
  border-top: 1px solid #edf2f7;
  gap: 1rem;
  flex-wrap: wrap;
}

.pagination-settings {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.settings-label-wrapper {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.subtle-icon {
  color: #94a3b8;
  opacity: 0.6;
}

.per-page-label {
  font-size: 0.875rem;
  color: #64748b;
  font-weight: 600;
}

.custom-select-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  height: 38px;
}

.per-page-select {
  appearance: none;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 0 2.2rem 0 0.75rem;
  height: 100%;
  font-size: 0.875rem;
  font-weight: 700;
  color: #1e293b;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 70px;
  line-height: 38px;
}

.per-page-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.select-arrow-icon {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  color: #64748b;
  pointer-events: none;
}

.total-count-info {
  font-size: 0.875rem;
  color: #94a3b8;
  font-weight: 500;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 1.25rem;
}

.nav-btn {
  padding: 0 1rem;
  min-width: 110px;
  font-size: 0.85rem;
  font-weight: 600;
  gap: 0.5rem;
}

.pagination-pages {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  font-size: 0.9rem;
  font-weight: 700;
  color: #64748b;
}

.subtle-page-icon {
  opacity: 0.3;
  color: #667eea;
}

.current-page {
  color: #667eea;
  background: #f0f4ff;
  min-width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  padding: 0 4px;
}

.divider {
  color: #cbd5e1;
}

.total-pages {
  color: #64748b;
}

.pagination-btn {
  background: white;
  border: 1px solid #e2e8f0;
  color: #64748b;
  height: 38px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.pagination-btn svg {
  display: block;
}

.pagination-btn:hover:not(:disabled) {
  border-color: #667eea;
  color: #667eea;
  background: #f8fafc;
  transform: translateY(-1px);
}

.pagination-btn:active:not(:disabled) {
  transform: translateY(0);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: #f8fafc;
}

@media (max-width: 640px) {
  .pagination-container {
    flex-direction: column;
    padding: 1.25rem;
  }
  
  .pagination-settings {
    width: 100%;
    justify-content: center;
  }
  
  .pagination-controls {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
