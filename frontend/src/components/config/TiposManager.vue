<template>
  <div class="config-component animate-fade-in">
    <div class="component-header">
       <div class="header-info">
          <h3>Tipos de Evento</h3>
          <p class="text-xs text-secondary">Categoriza tus eventos para una mejor organización</p>
       </div>
       <button v-if="authStore.hasPermission('CONFIGURACION:CREATE') || authStore.hasPermission('CONFIGURACION:MANAGE')" class="btn-primary btn-premium-header" @click="resetForm(); showForm = true">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right: 8px;"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
          Nuevo Tipo
       </button>
    </div>

    <div v-if="loading" class="loading-state">
      <LoadingSpinner />
    </div>
    
    <div v-if="error" class="error-msg-premium">{{ error }}</div>

    <div class="table-container-premium" v-if="!loading">
      <table class="styled-table premium-table">
        <thead>
          <tr>
            <th>Nombre del Tipo</th>
            <th v-if="authStore.hasPermission('CONFIGURACION:UPDATE_ALL') || authStore.hasPermission('CONFIGURACION:DELETE_ALL') || authStore.hasPermission('CONFIGURACION:MANAGE')" style="text-align: right">Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="t in tipos" :key="t.idTipoEvento">
            <td>
              <div class="name-cell">
                <div class="name-dot" style="background: #a855f7; box-shadow: 0 0 0 4px rgba(168, 85, 247, 0.1);"></div>
                <strong>{{ t.nombreTipoEvento }}</strong>
              </div>
            </td>
            <td v-if="authStore.hasPermission('CONFIGURACION:UPDATE_ALL') || authStore.hasPermission('CONFIGURACION:DELETE_ALL') || authStore.hasPermission('CONFIGURACION:MANAGE')" style="text-align: right">
              <div class="action-group">
                <button v-if="authStore.hasPermission('CONFIGURACION:UPDATE_ALL') || authStore.hasPermission('CONFIGURACION:MANAGE')" class="action-btn edit" @click="editTipo(t)" title="Editar">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                </button>
                <button v-if="authStore.hasPermission('CONFIGURACION:DELETE_ALL') || authStore.hasPermission('CONFIGURACION:MANAGE')" class="action-btn delete" @click="deleteTipo(t.idTipoEvento)" title="Eliminar">
                   <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div v-if="tipos.length === 0" class="empty-state-premium">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" opacity="0.3"><circle cx="12" cy="12" r="10"></circle><path d="M12 8v4"></path><path d="M12 16h.01"></path></svg>
        <p>No se encontraron tipos de evento registrados.</p>
      </div>
    </div>

    <BaseModal
      :show="showForm"
      :title="form.idTipoEvento ? 'Editar Tipo de Evento' : 'Nuevo Tipo de Evento'"
      maxWidth="500px"
      @close="showForm = false"
    >
      <form @submit.prevent="submitForm">
        <div class="form-group-premium">
            <label>Nombre del Tipo de Evento</label>
            <input v-model="form.nombreTipoEvento" class="form-control-premium" placeholder="Ej. Conferencia, Taller, Seminario" required />
            <small class="form-help">Categoría para facilitar la búsqueda y organización de eventos.</small>
        </div>
        <div class="modal-actions-premium">
            <button type="button" class="btn-outline" @click="showForm = false">Cancelar</button>
            <button type="submit" class="btn-primary">
              {{ form.idTipoEvento ? 'Actualizar' : 'Crear' }} Tipo
            </button>
        </div>
      </form>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../../services/api';
import LoadingSpinner from '../ui/LoadingSpinner.vue';
import BaseModal from '../modals/BaseModal.vue';
import { useAuthStore } from '../../stores/auth';
import { useNotificationStore } from '../../stores/notifications';
import { useConfirmStore } from '../../stores/confirm';

const authStore = useAuthStore();
const notificationStore = useNotificationStore();
const confirmStore = useConfirmStore();

const tipos = ref([]);
const loading = ref(false);
const error = ref('');
const showForm = ref(false);
const form = ref({ idTipoEvento: null, nombreTipoEvento: '' });

const fetchTipos = async () => {
  loading.value = true;
  try {
    const res = await api.get('/tipos-evento');
    tipos.value = res.data;
  } catch (err) {
    error.value = 'Error al cargar tipos: ' + err.message;
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  form.value = { idTipoEvento: null, nombreTipoEvento: '' };
};

const editTipo = (t) => {
  form.value = { ...t };
  showForm.value = true;
};

const deleteTipo = async (id) => {
  const confirmed = await confirmStore.ask({
      title: 'Eliminar Tipo de Evento',
      message: '¿Estás seguro de eliminar este tipo de evento?',
      type: 'danger'
  });
  if (!confirmed) return;
  try {
    await api.delete(`/tipos-evento/${id}`);
    notificationStore.showSuccess('Tipo eliminado correctamente');
    fetchTipos();
  } catch (err) {
    notificationStore.showError('Error eliminando: ' + (err.response?.data?.message || err.message));
  }
};

const submitForm = async () => {
  try {
    if (form.value.idTipoEvento) {
      await api.put(`/tipos-evento/${form.value.idTipoEvento}`, form.value);
      notificationStore.showSuccess('Tipo actualizado correctamente');
    } else {
      await api.post('/tipos-evento', form.value);
      notificationStore.showSuccess('Tipo creado correctamente');
    }
    showForm.value = false;
    fetchTipos();
  } catch (err) {
    notificationStore.showError('Error guardando: ' + (err.response?.data?.message || err.message));
  }
};

onMounted(fetchTipos);
</script>

<style scoped>
/* Configuration Components Premium Styling */
.config-component {
    padding: 0.5rem;
}

.component-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
}

.header-info h3 {
    font-size: 1.25rem;
    font-weight: 800;
    color: #1e293b;
    margin: 0;
}

.header-info p {
    margin: 0.25rem 0 0;
    font-size: 0.85rem;
    color: #64748b;
}

/* Premium Table Styling */
.table-container-premium {
    background: #fcfdfe;
    border-radius: 16px;
    border: 1px solid #f1f5f9;
    overflow: hidden;
}

.premium-table {
    width: 100%;
    border-collapse: collapse;
}

.premium-table th {
    background: #f8fafc;
    color: #64748b;
    font-weight: 700;
    text-transform: uppercase;
    font-size: 0.75rem;
    letter-spacing: 0.05em;
    padding: 1rem 1.5rem;
    text-align: left;
    border-bottom: 1px solid #f1f5f9;
}

.premium-table td {
    padding: 1.25rem 1.5rem;
    border-bottom: 1px solid #f1f5f9;
    color: #334155;
}

.premium-table tr:last-child td {
    border-bottom: none;
}

.premium-table tr:hover {
    background: #f8fbff;
}

.name-cell {
    display: flex;
    align-items: center;
    gap: 12px;
}

.name-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #6366f1;
    box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
}

/* Action Group */
.action-group {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
}

.action-btn {
    width: 38px;
    height: 38px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: white;
    border: 1px solid #e2e8f0;
    color: #64748b;
    cursor: pointer;
    transition: all 0.2s;
    padding: 0;
}

.action-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);
}

.action-btn.edit:hover {
    border-color: #6366f1;
    color: #6366f1;
    background: #f5f7ff;
}

.action-btn.delete:hover {
    border-color: #ef4444;
    color: #ef4444;
    background: #fef2f2;
}

/* Modal Form Premium Elements */
.form-group-premium {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin-bottom: 1.5rem;
}

.form-group-premium label {
    font-size: 0.9rem;
    font-weight: 700;
    color: #334155;
}

.form-control-premium {
    padding: 0.8rem 1rem;
    border-radius: 12px;
    border: 1px solid #e2e8f0;
    background: #f8fafc;
    transition: all 0.2s;
}

.form-control-premium:focus {
    background: white;
    border-color: #6366f1;
    box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
    outline: none;
}

.form-help {
    font-size: 0.75rem;
    color: #94a3b8;
}

.modal-actions-premium {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    padding-top: 1rem;
}

/* Empty State Premium */
.empty-state-premium {
    padding: 4rem 2rem;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1rem;
    color: #94a3b8;
    text-align: center;
}

.error-msg-premium {
    background: #fef2f2;
    color: #dc2626;
    padding: 1rem;
    border-radius: 12px;
    margin-bottom: 1.5rem;
    font-size: 0.9rem;
    font-weight: 600;
    border-left: 4px solid #ef4444;
}
</style>
