<script setup>
import { RouterView } from 'vue-router'
import TopNavbar from './components/layout/TopNavbar.vue'
import AnimatedBackground from './components/layout/AnimatedBackground.vue'
import NotificationContainer from './components/ui/NotificationContainer.vue'
import ConfirmModal from './components/modals/ConfirmModal.vue'
import SessionExpiryModal from './components/modals/SessionExpiryModal.vue'
import SessionTimer from './components/ui/SessionTimer.vue'
import AppFooter from './components/layout/AppFooter.vue'
import { useAuthStore } from './stores/auth'
import { onMounted } from 'vue'

const authStore = useAuthStore()

onMounted(() => {
  // Initialize auth store
  authStore.$subscribe(() => {
    // React to auth changes if needed
  })
})
</script>

<template>
  <AnimatedBackground />
  <TopNavbar />
  <NotificationContainer />
  <ConfirmModal />
  <SessionExpiryModal />
  <SessionTimer />
  <div id="app-container">
    <RouterView />
  </div>
  <AppFooter />
</template>

<style>
:root {
  --primary-color: #6366f1;
  --primary-accent: #4f46e5;
  --primary-gradient: linear-gradient(135deg, #6366f1, #4f46e5);
  --secondary-gradient: linear-gradient(135deg, #f6d365 0%, #fda085 100%);
  --bg-color: #f8fafc;
  --text-primary: #1e293b;
  --text-secondary: #64748b;
  --card-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  --card-hover-shadow: 0 10px 30px rgba(79, 70, 229, 0.12);
  --border-radius: 16px;
}

/* Custom Scrollbar - Global */
::-webkit-scrollbar {
  width: 10px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border: 2px solid #f1f5f9;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb:hover {
  background: #6366f1;
}

/* Premium Button Style - Applied to specific actions */
.btn-premium-outline {
  background: white;
  border: 2px solid #6366f1;
  color: #6366f1;
  padding: 0.75rem 1.75rem;
  border-radius: 50px;
  font-weight: 800;
  font-size: 0.85rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.1);
}

.btn-premium-outline:hover {
  background: #6366f1;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(99, 102, 241, 0.2);
}

.btn-premium-outline:active {
  transform: translateY(0);
}

/* Global basic styles */
body {
  font-family: 'Inter', 'Segoe UI', system-ui, -apple-system, sans-serif;
  margin: 0;
  padding: 0;
  background-color: var(--bg-color);
  color: var(--text-primary);
  line-height: 1.5;
  -webkit-font-smoothing: antialiased;
  overflow-x: hidden; 
  width: 100%;
}

* {
  box-sizing: border-box;
}

#app-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 2rem 1.5rem;
  min-height: calc(100vh - 120px);
  width: 100%;
}

@media (max-width: 1100px) {
  #app-container {
    padding: 75px 1rem 80px; /* Space for top and bottom nav bars */
  }
}

/* Typography */
h1, h2, h3, h4 {
  color: var(--text-primary);
  margin-top: 0;
}

@media (max-width: 768px) {
  h1 { font-size: 1.5rem; }
  h2 { font-size: 1.25rem; }
  h3 { font-size: 1.1rem; }
}

/* Grid Utilities */
.form-grid, .grid-2 {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.5rem;
}

.grid-3 {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
}

@media (max-width: 768px) {
  .form-grid, .grid-2, .grid-3 {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
}

.full-width { grid-column: 1 / -1; }

/* Common UI Layouts */
.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  gap: 1.5rem;
  flex-wrap: wrap;
}

@media (max-width: 600px) {
  .header-actions {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
    margin-bottom: 1.5rem;
  }
}

.glass-header h1 {
  font-size: 1.75rem;
  font-weight: 800;
  margin: 0;
  background: var(--primary-gradient);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.actions {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

@media (max-width: 600px) {
  .actions {
    width: 100%;
  }
  .actions > * {
    flex: 1; 
    justify-content: center;
  }
}

/* Card styles */
.card {
  background: white;
  border-radius: var(--border-radius);
  box-shadow: var(--card-shadow);
  padding: 2rem;
  margin-bottom: 2rem;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: 1px solid rgba(0,0,0,0.03);
  width: 100%;
}

@media (max-width: 768px) {
  .card {
    padding: 1.25rem;
    border-radius: 12px;
    margin-bottom: 1.25rem;
  }
}

/* Form elements */
.form-group {
  margin-bottom: 1.25rem;
  width: 100%;
}

.form-label, label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  font-size: 0.85rem;
}

.form-input, .form-control, input, select, textarea {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  font-size: 16px; 
  transition: all 0.2s ease;
  background-color: white;
}

.form-input:focus, .form-control:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* Table Management */
.table-responsive {
  width: 100%;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  border-radius: 12px;
  border: 1px solid #edf2f7;
  background: white;
  margin-bottom: 1rem;
}

.styled-table {
  width: 100%;
  border-collapse: collapse;
}

.styled-table th {
  text-align: left;
  padding: 1rem;
  background: #f8fafc;
  color: var(--text-secondary);
  font-weight: 700;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 1px solid #e2e8f0;
}

.styled-table td {
  padding: 1.25rem 1rem;
  border-bottom: 1px solid #f1f5f9;
  vertical-align: middle;
}

/* Table to Cards Conversion on Mobile */
@media (max-width: 768px) {
  .mobile-cards .styled-table {
    min-width: 0;
  }
  .mobile-cards .styled-table, 
  .mobile-cards .styled-table thead, 
  .mobile-cards .styled-table tbody, 
  .mobile-cards .styled-table th, 
  .mobile-cards .styled-table td, 
  .mobile-cards .styled-table tr { 
    display: block; 
    width: 100%;
  }
  
  .mobile-cards .styled-table thead {
    display: none; /* Hide headers on mobile */
  }

  .mobile-cards .styled-table tr {
    margin-bottom: 1rem;
    border: 1px solid #edf2f7;
    border-radius: 12px;
    padding: 0.5rem;
    background: white;
  }

  .mobile-cards .styled-table td {
    text-align: right;
    padding: 0.5rem 1rem;
    border-bottom: 1px solid #f7fafc;
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    font-size: 0.85rem;
  }

  .mobile-cards .styled-table td:last-child {
    border-bottom: none;
    justify-content: flex-end;
    gap: 0.5rem;
    padding-top: 1rem;
  }

  /* Add labels before data using data-label attribute */
  .mobile-cards .styled-table td::before {
    content: attr(data-label);
    font-weight: 700;
    text-transform: uppercase;
    font-size: 0.7rem;
    color: var(--text-secondary);
    text-align: left;
  }
  
  .mobile-cards .styled-table td:only-child::before,
  .mobile-cards .styled-table td[data-label=""]::before {
    display: none;
  }
}

/* Status Badges */
.badge, .status-badge {
    padding: 4px 10px;
    border-radius: 20px;
    font-size: 0.75rem;
    font-weight: 700;
    text-transform: uppercase;
    display: inline-block;
}

/* Pagination Styles */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  margin-top: 2rem;
  padding: 1rem;
  flex-wrap: wrap;
}

.pagination-btn {
  padding: 0.6rem 1rem;
  border: 1px solid #e2e8f0;
  background: white;
  border-radius: 10px;
  color: var(--text-primary);
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.pagination-btn:hover:not(:disabled) {
  border-color: var(--primary-color);
  color: var(--primary-color);
  background: #f0f4ff;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: #f8fafc;
}

.pagination-info {
  font-size: 0.9rem;
  color: var(--text-secondary);
  font-weight: 500;
  margin: 0 1rem;
}

@media (max-width: 600px) {
  .pagination {
    gap: 0.4rem;
    margin-top: 1.5rem;
  }
  .pagination-info {
    width: 100%;
    text-align: center;
    margin-bottom: 0.5rem;
    order: -1;
  }
  .pagination-btn {
    flex: 1;
    justify-content: center;
    padding: 0.75rem;
  }
}

/* Animation utilities */
.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>