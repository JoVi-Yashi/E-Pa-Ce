<template>
  <div class="table-responsive mobile-cards">
    <table class="styled-table">
      <thead>
        <tr>
          <slot name="header"></slot>
        </tr>
      </thead>
      <tbody>
        <slot name="body"></slot>
      </tbody>
    </table>
  </div>
</template>

<style>
/* Global Table Styles */
.styled-table {
  width: 100%;
  border-collapse: separate; 
  border-spacing: 0; 
  margin: 1rem 0;
  font-size: 0.95rem;
  background: white;
  border-radius: 12px; 
  overflow: hidden; 
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05); 
}

.styled-table thead tr {
  background-color: #f8fafc;
  color: #4a5568;
  text-align: left;
}

.styled-table th,
.styled-table td {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #edf2f7;
}

.styled-table th {
    font-weight: 700;
    text-transform: uppercase;
    font-size: 0.75rem;
    letter-spacing: 0.05em;
    border-bottom: 2px solid #e2e8f0;
}

.styled-table tbody tr {
  transition: all 0.2s ease;
}

.styled-table tbody tr:hover {
  background-color: #f8fafc;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
}

.styled-table tbody tr:last-of-type {
  border-bottom: 2px solid #f1f5f9;
}

/* Mobile Responsive Cards */
@media (max-width: 768px) {
  .styled-table, .styled-table thead, .styled-table tbody, .styled-table th, .styled-table td, .styled-table tr { 
    display: block; 
  }
  
  .styled-table thead tr { 
    position: absolute;
    top: -9999px;
    left: -9999px;
  }
  
  .styled-table tr { 
    border: 1px solid #e2e8f0; 
    margin-bottom: 1rem;
    border-radius: 12px;
    background: white;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    padding: 1rem;
  }
  
  .styled-table td { 
    border: none;
    border-bottom: 1px solid #f1f5f9; 
    position: relative;
    padding-left: 50%; 
    text-align: right;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.75rem 0;
  }
  
  .styled-table td:last-child {
      border-bottom: none;
  }

  .styled-table td:before { 
    content: attr(data-label);
    font-weight: 700;
    color: #718096;
    text-transform: uppercase;
    font-size: 0.75rem;
    text-align: left;
  }
}
</style>
