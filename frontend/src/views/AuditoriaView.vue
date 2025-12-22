<template>
  <div class="auditoria container fade-in">
    <PageHeader 
      title="Auditoría del Sistema" 
      subtitle="Registro detallado de actividades, cambios y seguridad de la plataforma"
    >
      <template #actions v-if="authStore.hasPermission('AUDITORIA:VIEW')">
         <div class="export-dropdown" ref="exportMenuRef">
            <button class="btn-premium-header" @click="toggleExportMenu" :class="{ active: showExportMenu }">
               <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="2.5" fill="none"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
               <span>Generar Reporte</span>
            </button>
            <div class="dropdown-menu-export" v-if="showExportMenu">
              <div class="dropdown-arrow"></div>
              <button class="dropdown-item green" @click="exportToCSV">
                 <svg viewBox="0 0 24 24" width="16" height="16" stroke="currentColor" stroke-width="2" fill="none"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline></svg>
                 <span>Excel (CSV)</span>
              </button>
              <button class="dropdown-item red" @click="exportToPDF">
                 <svg viewBox="0 0 24 24" width="16" height="16" stroke="currentColor" stroke-width="2" fill="none"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline></svg>
                 <span>PDF Reporte</span>
              </button>
            </div>
         </div>
      </template>
    </PageHeader>

    <div class="filters-container slide-up">
       <div class="filter-item search">
          <label>Búsqueda Inteligente</label>
          <div class="input-wrapper">
             <svg viewBox="0 0 24 24" width="18" height="18" stroke="currentColor" stroke-width="2" fill="none" class="search-icon"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
             <input v-model="filters.usuario" type="text" class="search-input" placeholder="Usuario, evento o detalle..." />
          </div>
       </div>

       <div class="filter-group">
          <div class="filter-item">
            <label>Tipo de Acción</label>
            <div class="input-wrapper">
              <select v-model="filters.accion" class="custom-select">
                <option value="">Todas</option>
                <option value="LOGIN">Inicio de Sesión</option>
                <option value="CREATE">Creación</option>
                <option value="UPDATE">Actualización</option>
                <option value="DELETE">Eliminación</option>
                <option value="SCAN">Escaneo QR</option>
              </select>
              <svg class="select-icon" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="6 9 12 15 18 9"></polyline></svg>
            </div>
          </div>
          
          <div class="filter-item">
            <label>Filtrar por Rol</label>
            <div class="input-wrapper">
              <select v-model="filters.rol" class="custom-select">
                <option value="">Cualquier Rol</option>
                <option v-for="role in availableRoles" :key="role" :value="role">
                  {{ role }}
                </option>
              </select>
              <svg class="select-icon" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="6 9 12 15 18 9"></polyline></svg>
            </div>
          </div>
          
          <div class="filter-item date">
            <label>Fecha de Registro</label>
            <div class="input-wrapper">
               <input type="date" v-model="filters.fecha" class="date-input-modern" />
            </div>
          </div>

          <div class="filter-actions-right">
               <button v-if="filters.usuario || filters.accion || filters.fecha || filters.rol" class="btn-clear-modern animate-slide-in-right" @click="resetFilters" title="Limpiar todos los filtros">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M18 6L6 18M6 6l12 12"></path></svg>
                  <span>Limpiar Filtros</span>
               </button>
          </div>
       </div>
    </div>


    <!-- Content -->
    <div class="card content-card slide-up-delayed">
       <!-- Loading -->
       <div v-if="loading" class="loading-state">
         <div class="spinner"></div>
         <p>Cargando registros...</p>
       </div>

       <!-- Table -->
       <template v-if="!loading">
         <TablePagination 
           v-if="filteredLogs.length > 0"
           :totalItems="filteredLogs.length" 
           v-model:itemsPerPage="itemsPerPage" 
           v-model:currentPage="currentPage" 
         />
         
         <div class="table-responsive">
           <table class="styled-table">
             <thead>
               <tr>
                 <th>ID</th>
                 <th>Fecha y Hora</th>
                 <th>Referencia</th>
                 <th>Usuario</th>
                 <th>Acción</th>
                 <th>Rol</th>
                 <th>Detalle</th>
                 <th>IP</th>
               </tr>
             </thead>
             <tbody>
               <tr v-for="log in paginatedLogs" :key="log.idAuditoria">
                 <td class="id-col">#{{ log.idAuditoria }}</td>
                 <td>{{ formatDateTime(log.fechaHora) }}</td>
                 <td class="ref-col">{{ log.entidadAfectada || '-' }}</td>
                 <td>
                   <div class="user-cell">
                     <div class="avatar-xs">
                        <img v-if="log.participanteFoto" :src="log.participanteFoto" class="avatar-img" />
                        <span v-else>{{ getInitials(log.participanteNombre) }}</span>
                     </div>
                     <div class="user-info-cell">
                        <span class="username">{{ log.participanteNombre || 'ANONYMOUS' }}</span>
                        <small class="user-id" v-if="log.participanteDocumento">ID: {{ log.participanteDocumento }}</small>
                     </div>
                   </div>
                 </td>
                 <td>
                   <span :class="['action-badge', getActionClass(log.accion)]">
                      {{ log.accion }}
                   </span>
                 </td>
                 <td class="role-cell">
                    <span v-if="log.rolUsuario" class="mini-role-badge">{{ log.rolUsuario }}</span>
                    <span v-else class="no-role warning">Acción sin rol</span>
                 </td>
                 <td class="detail-cell">
                   <div class="detail-wrapper" :class="{ expanded: expandedLogs.includes(log.idAuditoria) }">
                     {{ log.descripcionCambio }}
                   </div>
                   <button 
                     v-if="(log.descripcionCambio || '').length > 40" 
                     @click="toggleExpand(log.idAuditoria)" 
                     class="expand-toggle"
                   >
                     {{ expandedLogs.includes(log.idAuditoria) ? 'Ver menos' : 'Ver más' }}
                   </button>
                 </td>
                 <td class="ip-cell">{{ log.ipOrigen || 'Unknown' }}</td>
               </tr>
                <tr v-if="filteredLogs.length === 0">
                  <td colspan="8" class="empty-cell">
                    <EmptyState 
                      title="No se encontraron coincidencias" 
                      description="Prueba ajustando los filtros de usuario, acción o fecha."
                    >
                      <template #actions>
                          <button class="btn-sm-outline" @click="resetFilters">Limpiar Filtros</button>
                      </template>
                    </EmptyState>
                  </td>
                </tr>
             </tbody>
           </table>
         </div>

         <TablePagination 
           v-if="filteredLogs.length > 0"
           :totalItems="filteredLogs.length" 
           v-model:itemsPerPage="itemsPerPage" 
           v-model:currentPage="currentPage" 
         />
       </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue';
import api from '../services/api';
import PageHeader from '../components/layout/PageHeader.vue';
import TablePagination from '../components/ui/TablePagination.vue';
import EmptyState from '../components/ui/EmptyState.vue';
import { useRoute } from 'vue-router';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import logoSvgUrl from '../assets/image/EPaCe_logo.svg';
import { useAuthStore } from '../stores/auth';
import { useNotificationStore } from '../stores/notifications';
import { useConfirmStore } from '../stores/confirm';

const authStore = useAuthStore();
const notificationStore = useNotificationStore();
const confirmStore = useConfirmStore();

const route = useRoute();

const logs = ref([]);
const loading = ref(false);
const showExportMenu = ref(false);
const exportMenuRef = ref(null);
const filters = ref({
  usuario: '',
  accion: '',
  fecha: '',
  rol: ''
});

// Pagination State
const currentPage = ref(1);
const itemsPerPage = ref(10);

const totalPages = computed(() => Math.ceil(filteredLogs.value.length / itemsPerPage.value));
const paginatedLogs = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  return filteredLogs.value.slice(start, start + itemsPerPage.value);
});

// Reset pagination on filter change
watch(filters, () => {
    currentPage.value = 1;
}, { deep: true });

const expandedLogs = ref([]);

const toggleExpand = (id) => {
    const index = expandedLogs.value.indexOf(id);
    if (index > -1) {
        expandedLogs.value.splice(index, 1);
    } else {
        expandedLogs.value.push(id);
    }
};

const toggleExportMenu = () => {
    showExportMenu.value = !showExportMenu.value;
};

const handleClickOutside = (event) => {
    if (exportMenuRef.value && !exportMenuRef.value.contains(event.target)) {
        showExportMenu.value = false;
    }
};

onMounted(() => {
    document.addEventListener('mousedown', handleClickOutside);
});

onUnmounted(() => {
    document.removeEventListener('mousedown', handleClickOutside);
});

const fetchLogs = async () => {
    loading.value = true;
    try {
        // Usar ruta relativa para que respete el baseURL: '.../api' de axios
        const res = await api.get('/auditoria'); 
        logs.value = res.data;
        currentPage.value = 1; // Reset to first page
    } catch (err) {
        notificationStore.showError('Error al cargar auditoría: ' + (err.response?.data?.message || err.message));
        // Fallback robusto
        if (logs.value.length === 0) {
            logs.value = [];
        }
    } finally {
        loading.value = false;
    }
};

// Helper to get unique roles from logs for the filter
const availableRoles = computed(() => {
    if (!logs.value) return [];
    const roles = logs.value
        .map(log => log.rolUsuario)
        .filter(role => role && role !== '');
    return [...new Set(roles)].sort();
});

const filteredLogs = computed(() => {
    if (!logs.value || !Array.isArray(logs.value)) return [];
    
    return logs.value.filter(log => {
        const query = (filters.value.usuario || '').toLowerCase();
        const nombre = (log.participanteNombre || 'ANONYMOUS').toLowerCase();
        const descripcion = (log.descripcionCambio || '').toLowerCase();
        const entidad = (log.entidadAfectada || '').toLowerCase();
        const rolLog = (log.rolUsuario || '').toLowerCase();
        
        // Búsqueda global en el campo "usuario"
        const matchSearchInput = !query || 
            nombre.includes(query) || 
            descripcion.includes(query) || 
            entidad.includes(query);
        
        const matchAction = !filters.value.accion || 
            log.accion === filters.value.accion;

        const matchRole = !filters.value.rol ||
            log.rolUsuario === filters.value.rol;
            
        // Soporte adicional para query param de búsqueda (desde Dashboard)
        const searchQuery = route.query.search;
        const matchDashboardSearch = !searchQuery || 
            (log.descripcionCambio && log.descripcionCambio.toLowerCase().includes(searchQuery.toLowerCase())) ||
            (log.entidadAfectada && log.entidadAfectada.toLowerCase().includes(searchQuery.toLowerCase()));

        let matchDate = true;
        if (filters.value.fecha && log.fechaHora) {
            const logDate = new Date(log.fechaHora).toISOString().split('T')[0];
            matchDate = logDate === filters.value.fecha;
        }
        
        return matchSearchInput && matchAction && matchRole && matchDate && matchDashboardSearch;
    });
});

const resetFilters = () => {
    filters.value = { usuario: '', accion: '', fecha: '', rol: '' };
    currentPage.value = 1;
};

// --- Export Logic ---

const exportToCSV = () => {
    const data = filteredLogs.value;
    if (!data.length) return;

    // Usar punto y coma (;) para compatibilidad con Excel en español
    const separator = ';';
    const headers = ['ID', 'Fecha', 'Referencia', 'Usuario', 'Acción', 'Rol', 'Detalle', 'IP'];
    
    const rows = data.map(log => [
        `#${log.idAuditoria}`,
        new Date(log.fechaHora).toLocaleString('es-ES'),
        log.entidadAfectada || '-',
        log.participanteNombre || 'ANONYMOUS',
        log.accion,
        log.rolUsuario || '-',
        (log.descripcionCambio || '').replace(/;/g, ' '), // Limpiar separador del texto
        log.ipOrigen || 'Unknown'
    ]);

    // Agregar BOM para que Excel detecte UTF-8 correctamente
    const BOM = '\uFEFF';
    const csvContent = BOM + headers.join(separator) + '\n' + rows.map(r => r.join(separator)).join('\n');
    
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', `auditoria_epace_${new Date().getTime()}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    showExportMenu.value = false;
};

// Helper to convert image (SVG/PNG) to DataURL via Canvas
const getLogoDataUrl = (url) => {
    return new Promise((resolve, reject) => {
        const img = new Image();
        img.crossOrigin = 'Anonymous';
        img.onload = () => {
            const canvas = document.createElement('canvas');
            // Set scale for high quality
            canvas.width = img.width * 2; 
            canvas.height = img.height * 2;
            const ctx = canvas.getContext('2d');
            ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
            resolve(canvas.toDataURL('image/png'));
        };
        img.onerror = (e) => reject(e);
        img.src = url;
    });
};

const exportToPDF = async () => {
    try {
        const data = filteredLogs.value;
        if (!data.length) return;

        const doc = new jsPDF('l', 'mm', 'a4');
        const pageWidth = doc.internal.pageSize.width;
        
        // --- Header Section ---
        // Gradient-like background using multiple rects
        doc.setFillColor(79, 70, 229); // Primary Indigo
        doc.rect(0, 0, pageWidth, 40, 'F');
        
        // Overlay for "modern" effect
        doc.setFillColor(99, 102, 241); // Ligher Indigo
        doc.circle(pageWidth, 0, 60, 'F');
        doc.setFillColor(67, 56, 202); // Darker Indigo
        doc.circle(0, 0, 40, 'F');

        // Logo Check with Async Loading
        try {
           const logoData = await getLogoDataUrl(logoSvgUrl);
           doc.addImage(logoData, 'PNG', 15, 8, 24, 24);
        } catch (e) {
           console.warn("Logo loading failed:", e);
           // Fallback text
           doc.setFont("helvetica", "bold");
           doc.setFontSize(24);
           doc.setTextColor(255, 255, 255);
           doc.text("E-Pa-Ce", 15, 25);
        }

        // Report Title
        doc.setFont("helvetica", "bold");
        doc.setFontSize(22);
        doc.setTextColor(255, 255, 255);
        doc.text("Reporte de Auditoría", 50, 20);

        // Subtitle / Date
        doc.setFont("helvetica", "normal");
        doc.setFontSize(10);
        doc.setTextColor(224, 231, 255); // Light Indigo Text
        doc.text(`Generado el: ${new Date().toLocaleString('es-ES')}`, 50, 28);
        doc.text(`Total de registros: ${data.length}`, 50, 33);

        const body = data.map(log => [
            `#${log.idAuditoria}`,
            formatDateTime(log.fechaHora),
            log.entidadAfectada || '-',
            log.participanteNombre || 'ANONYMOUS',
            log.accion,
            log.rolUsuario || 'Sin Rol',
            log.descripcionCambio,
            log.ipOrigen || 'Unknown'
        ]);

        const tableOptions = {
            startY: 45,
            head: [['ID', 'Fecha', 'Ref.', 'Usuario', 'Acción', 'Rol', 'Detalle', 'IP']],
            body: body,
            theme: 'striped',
            headStyles: { 
                fillColor: [30, 41, 59], // Slate 800
                textColor: 255,
                fontStyle: 'bold',
                halign: 'left'
            },
            styles: { 
                fontSize: 9, 
                cellPadding: 3, 
                font: 'helvetica',
                overflow: 'linebreak'
            },
            columnStyles: {
                0: { cellWidth: 15, fontStyle: 'bold', textColor: [100, 116, 139] }, // ID Gray
                1: { cellWidth: 35 },
                2: { cellWidth: 25 },
                3: { cellWidth: 35 },
                4: { cellWidth: 25, fontStyle: 'bold' },
                5: { cellWidth: 25 },
                6: { cellWidth: 'auto' }, // Detail takes remaining space
                7: { cellWidth: 25 }
            },
            didParseCell: function(data) {
                // Colorize Actions
                if (data.column.index === 4) {
                    const action = data.cell.text[0];
                    if (action === 'DELETE') data.cell.styles.textColor = [220, 38, 38];
                    else if (action === 'CREATE') data.cell.styles.textColor = [22, 163, 74];
                    else if (action === 'UPDATE') data.cell.styles.textColor = [202, 138, 4];
                    else if (action === 'LOGIN') data.cell.styles.textColor = [37, 99, 235];
                }
                // Highlight No-Role
                if (data.column.index === 5 && (data.cell.text[0] === 'Sin Rol' || data.cell.text[0] === 'Acción sin rol')) {
                    data.cell.styles.textColor = [156, 163, 175];
                    data.cell.styles.fontStyle = 'italic';
                }
            },
            willDrawPage: function (data) {
                // Header is already drawn on page 1, but for subsequent pages?
                // Actually jspdf-autotable handles paging well. 
                // We can add a simple footer page number here
            },
            didDrawPage: function (data) {
                // Footer
                doc.setFontSize(8);
                doc.setTextColor(150);
                const pageStr = 'Página ' + doc.internal.getNumberOfPages();
                doc.text(pageStr, data.settings.margin.left, doc.internal.pageSize.height - 10);
            }
        };

        if (typeof autoTable === 'function') {
            autoTable(doc, tableOptions);
        } else if (typeof doc.autoTable === 'function') {
             doc.autoTable(tableOptions);
        } else {
             notificationStore.showError("Error: No se pudo cargar el motor de tablas del PDF.");
             return;
        }

        doc.save(`Auditoria_EPaCe_${new Date().toISOString().slice(0,10)}.pdf`);
        showExportMenu.value = false;
        notificationStore.showSuccess('Reporte PDF generado correctamente');
    } catch (error) {
        console.error(error);
        notificationStore.showError("Error al generar el PDF. Por favor, intente de nuevo.");
    }
};

const formatDateTime = (date) => {
    if (!date) return '-';
    try {
        return new Date(date).toLocaleString('es-ES', {
            day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit'
        });
    } catch (e) {
        return date;
    }
};

const getInitials = (name) => {
    if (!name || name === 'ANONYMOUS') return 'A';
    return name.substring(0, 2).toUpperCase();
};

const truncate = (str, len) => {
    if (!str) return '';
    return str.length > len ? str.substring(0, len) + '...' : str;
};

const getActionClass = (action) => {
    if (!action) return 'badge-neutral';
    const map = {
        'LOGIN': 'badge-info',
        'CREATE': 'badge-success',
        'UPDATE': 'badge-warning',
        'DELETE': 'badge-danger',
        'SCAN': 'badge-info',
        'CHECKIN': 'badge-success'
    };
    return map[action] || 'badge-neutral';
};

onMounted(fetchLogs);
</script>

<style scoped>
/* Page Layout & Typography */
.auditoria {
    padding-bottom: 2rem;
}

/* Header Styling */
.glass-header {
    margin-bottom: 2rem; 
}

.glass-header h1 {
    color: white;
    text-shadow: 0 2px 4px rgba(0,0,0,0.2);
    margin: 0;
}

.subtitle {
    color: #e2e8f0; 
    margin-top: 0.5rem;
    font-size: 1rem;
    font-weight: 400;
}

/* Standardized Filters - Perfect Alignment */
.filters-container {
    display: flex;
    gap: 1.5rem;
    margin-bottom: 2rem;
    align-items: flex-end;
    flex-wrap: wrap;
    background: white;
    padding: 1.25rem 1.5rem;
    border-radius: 16px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.filter-item {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.filter-item label {
    font-size: 0.75rem;
    font-weight: 700;
    color: #94a3b8;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-bottom: 2px;
}

.filter-item.search { flex: 1; min-width: 300px; }

.input-wrapper {
    display: flex;
    align-items: center;
    background: #f8fafc;
    border: 1.5px solid #e2e8f0;
    border-radius: 12px;
    height: 48px; /* Fixed height for all */
    padding: 0 1rem;
    transition: all 0.2s;
    box-sizing: border-box;
}

.input-wrapper:focus-within {
    background: white;
    border-color: #6366f1;
    box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.search-input, .custom-select, .date-input-modern {
    border: none !important;
    background: transparent !important;
    width: 100%;
    height: 100%;
    font-size: 0.95rem;
    color: #1e293b;
    font-weight: 600;
    outline: none;
    padding: 0;
    margin: 0;
    display: flex;
    align-items: center;
}

.custom-select {
    appearance: none;
    cursor: pointer;
}

.search-icon { color: #94a3b8; margin-right: 12px; flex-shrink: 0; }
.select-icon { margin-left: 8px; color: #94a3b8; pointer-events: none; }

.filter-group {
    display: flex;
    gap: 1.25rem;
    align-items: flex-end;
    flex-wrap: wrap;
    flex: 2;
}

.filter-actions-right {
    display: flex;
    gap: 0.75rem;
    margin-left: auto;
    align-items: center;
}

/* Header Actions & Premium Button */
.header-main {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.btn-premium-header {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 0 1.75rem;
    height: 52px;
    background: white;
    border: none;
    border-radius: 14px;
    color: #4f46e5;
    font-weight: 800;
    font-size: 1rem;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

.btn-premium-header:hover {
    transform: translateY(-3px);
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
    background: #f8fafc;
}

.btn-premium-header:active {
    transform: translateY(-1px);
}

.btn-premium-header svg {
    color: #6366f1;
}

/* Standardized Filters - Perfect Alignment */
.filters-container {
    display: flex;
    gap: 1.5rem;
    margin-bottom: 2rem;
    align-items: flex-end;
    flex-wrap: wrap;
    background: white;
    padding: 1.25rem 1.5rem;
    border-radius: 16px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.filter-item {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.filter-item label {
    font-size: 0.75rem;
    font-weight: 700;
    color: #94a3b8;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-bottom: 2px;
}

.filter-item.search { flex: 1; min-width: 300px; }

.input-wrapper {
    display: flex;
    align-items: center;
    background: #f8fafc;
    border: 1.5px solid #e2e8f0;
    border-radius: 12px;
    height: 48px; /* Fixed height for all */
    padding: 0 1rem;
    transition: all 0.2s;
    box-sizing: border-box;
}

.input-wrapper:focus-within {
    background: white;
    border-color: #6366f1;
    box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.search-input, .custom-select, .date-input-modern {
    border: none !important;
    background: transparent !important;
    width: 100%;
    height: 100%;
    font-size: 0.95rem;
    color: #1e293b;
    font-weight: 600;
    outline: none;
    padding: 0;
    margin: 0;
    display: flex;
    align-items: center;
}

.custom-select {
    appearance: none;
    cursor: pointer;
}

.search-icon { color: #94a3b8; margin-right: 12px; flex-shrink: 0; }
.select-icon { margin-left: 8px; color: #94a3b8; pointer-events: none; }

.filter-group {
    display: flex;
    gap: 1.25rem;
    align-items: flex-end;
    flex-wrap: wrap;
    flex: 2;
}

.filter-actions-right {
    display: flex;
    gap: 0.75rem;
    margin-left: auto;
    align-items: center;
}

.btn-clear-modern {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 0 1.25rem;
    height: 48px;
    background: #f1f5f9;
    color: #64748b;
    border: 1.5px solid #e2e8f0;
    border-radius: 12px;
    font-weight: 700;
    font-size: 0.9rem;
    cursor: pointer;
    transition: all 0.25s;
}

.btn-clear-modern:hover {
    background: white;
    color: #ef4444;
    border-color: #fecaca;
    transform: translateY(-1px);
}

/* Content Card - Seamless */
.content-card {
    padding: 0;
    overflow: hidden;
    border: 1px solid #e2e8f0;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
    background: white;
    border-radius: 16px;
}

/* Table - Professional Grid */
.table-responsive {
    overflow-x: auto;
}

.styled-table {
    width: 100%;
    border-collapse: separate; 
    border-spacing: 0;
}

.styled-table th {
    background: #f8fafc;
    color: #475569;
    font-weight: 600;
    padding: 1rem 1.5rem;
    text-align: left;
    font-size: 0.85rem;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    border-bottom: 1px solid #e2e8f0;
}

.styled-table td {
    padding: 1rem 1.5rem;
    border-bottom: 1px solid #f1f5f9;
    color: #334155;
    font-size: 0.95rem;
    vertical-align: middle;
}

.styled-table tr:last-child td {
    border-bottom: none;
}

.styled-table tr:hover td {
    background: #fafafa;
}

.id-col { 
    color: #94a3b8; 
    font-family: 'JetBrains Mono', 'Fira Code', monospace; 
    font-size: 0.85rem; 
}

.user-cell {
    display: flex;
    align-items: center;
    gap: 12px;
}

.avatar-xs {
    width: 32px; 
    height: 32px;
    flex-shrink: 0;
    background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e1 100%);
    color: #475569;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.75rem;
    font-weight: 700;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    overflow: hidden; 
    position: relative; 
}

.avatar-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
}

.username { 
    font-weight: 600; 
    color: #1e293b;
    display: block;
    line-height: 1.2;
}

.user-info-cell {
    display: flex;
    flex-direction: column;
}

.user-id {
    font-size: 0.75rem;
    color: #94a3b8;
}

.ref-col {
    font-weight: 500;
    color: #64748b;
    font-size: 0.85rem;
}

/* Badges - Refined Colors */
.action-badge {
    padding: 0.35rem 0.75rem;
    border-radius: 6px;
    font-size: 0.75rem;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    display: inline-block;
    min-width: 80px;
    text-align: center;
}

.badge-info { 
    background: #eff6ff; 
    color: #3b82f6; 
    border: 1px solid #dbeafe;
}
.badge-success { 
    background: #f0fdf4; 
    color: #16a34a; 
    border: 1px solid #dcfce7;
}
.badge-warning { 
    background: #fffbeb; 
    color: #d97706; 
    border: 1px solid #fef3c7;
}
.badge-danger { 
    background: #fef2f2; 
    color: #dc2626; 
    border: 1px solid #fee2e2;
}
.badge-neutral { 
    background: #f8fafc; 
    color: #64748b; 
    border: 1px solid #e2e8f0;
}

/* Export Button & Dropdown Styles */
.export-dropdown {
    position: relative;
    display: flex;
    align-items: center;
}

.btn-export-main {
    box-sizing: border-box;
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 0 1.25rem;
    height: 42px;
    background: white;
    border: 1.5px solid #e2e8f0;
    border-radius: 12px;
    color: #1e293b;
    font-weight: 700;
    font-size: 0.9rem;
    cursor: pointer;
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.btn-export-main:hover, .btn-export-main.active {
    border-color: #6366f1;
    color: #4f46e5;
    background: #f5f3ff;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
    transform: translateY(-1px);
}

.btn-export-main svg {
    transition: transform 0.2s;
}

.btn-export-main.active svg {
    transform: rotate(180deg);
}

.dropdown-menu-export {
    position: absolute;
    top: calc(100% + 15px);
    right: 0;
    background: white;
    border-radius: 16px;
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
    padding: 0.75rem;
    z-index: 2000;
    min-width: 180px;
    border: 1px solid #e2e8f0;
    animation: scaleIn 0.2s ease-out;
}

.dropdown-arrow {
    position: absolute;
    top: -8px;
    right: 30px;
    width: 16px;
    height: 16px;
    background: white;
    transform: rotate(45deg);
    border-top: 1px solid #e2e8f0;
    border-left: 1px solid #e2e8f0;
}

@keyframes scaleIn {
    from { opacity: 0; transform: scale(0.9) translateY(-10px); }
    to { opacity: 1; transform: scale(1) translateY(0); }
}

.dropdown-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 0.8rem 1rem;
    border: none;
    background: transparent;
    border-radius: 10px;
    font-size: 0.85rem;
    font-weight: 600;
    text-align: left;
    cursor: pointer;
    transition: all 0.2s;
    color: #475569;
}

.dropdown-item svg {
    opacity: 0.7;
}

.dropdown-item.green:hover { 
    background: #f0fdf4; 
    color: #166534; 
}
.dropdown-item.green:hover svg { stroke: #166534; opacity: 1; }

.dropdown-item.red:hover { 
    background: #fef2f2; 
    color: #991b1b; 
}
.dropdown-item.red:hover svg { stroke: #991b1b; opacity: 1; }

@keyframes scaleIn {
    from { opacity: 0; transform: scale(0.9) translateY(-10px); }
    to { opacity: 1; transform: scale(1) translateY(0); }
}

.detail-cell { 
    color: #64748b; 
    max-width: 300px; 
}

.detail-wrapper {
    overflow: hidden;
    text-overflow: ellipsis; 
    white-space: nowrap;
    transition: all 0.3s ease;
    font-size: 0.9rem;
}

.detail-wrapper.expanded {
    white-space: normal;
    word-break: break-word;
}

.expand-toggle {
    background: none;
    border: none;
    color: #6366f1;
    font-size: 0.75rem;
    font-weight: 600;
    cursor: pointer;
    padding: 0;
    margin-top: 4px;
}

.expand-toggle:hover {
    text-decoration: underline;
    color: #4f46e5;
}
.ip-cell { 
    font-family: 'JetBrains Mono', monospace; 
    color: #64748b; 
    font-size: 0.85rem; 
    border-radius: 4px;
}

.mini-role-badge {
    background: #f0f4ff;
    color: #4f46e5;
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 0.75rem;
    font-weight: 700;
    text-transform: uppercase;
    border: 1px solid rgba(79, 70, 229, 0.1);
}

.no-role {
    color: #cbd5e1;
}

.no-role.warning {
    color: #ea580c;
    font-weight: 600;
    font-size: 0.8rem;
    background: #fff7ed;
    padding: 2px 6px;
    border-radius: 4px;
}

/* Empty & Loading */
.empty-cell { text-align: center; padding: 4rem 1rem; }
.empty-state { color: #cbd5e1; display: flex; flex-direction: column; align-items: center; gap: 1rem; }
.empty-state p { margin: 0; color: #64748b; }

.pagination-footer {
    padding: 1rem 1.5rem;
    border-top: 1px solid #f1f5f9;
    color: #64748b;
    font-size: 0.9rem;
    background: #f8fafc;
}

/* Animations */
.slide-down { animation: slideDown 0.5s ease-out; }
.slide-up { animation: slideUp 0.5s ease-out; }
.slide-up-delayed { animation: slideUp 0.5s ease-out 0.1s backwards; }
.fade-in { animation: fadeIn 0.3s ease-out; }

@keyframes slideDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

/* Spinner */
.spinner {
    border: 3px solid #f3f3f3;
    border-top: 3px solid #667eea;
    border-radius: 50%;
    width: 30px;
    height: 30px;
    animation: spin 1s linear infinite;
    margin: 0 auto 1rem;
}
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
.loading-state { text-align: center; padding: 3rem; color: #718096; }

@media (max-width: 768px) {
    .header-main { flex-direction: column; align-items: flex-start; gap: 1rem; }
    .filters-container { flex-direction: column; align-items: stretch; }
    .filter-group { flex-direction: column; align-items: stretch; }
    .search-wrapper, .custom-select-wrapper, .date-input-modern, .btn-clear { width: 100%; }
}
</style>
