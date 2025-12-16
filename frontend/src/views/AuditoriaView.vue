<template>
  <div class="auditoria">
    <h1>Logs de Auditoría</h1>
    <router-link to="/">Volver al Dashboard</router-link>

    <div v-if="loading">Cargando...</div>
    <div v-if="error" style="color: red">{{ error }}</div>

    <table border="1" style="width: 100%; border-collapse: collapse; margin-top: 20px;">
      <thead>
        <tr>
          <th>ID</th>
          <th>Fecha/Hora</th>
          <th>Participante</th>
          <th>Acción</th>
          <th>Entidad</th>
          <th>Descripción</th>
          <th>IP Origen</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="log in logs" :key="log.idAuditoria">
          <td>{{ log.idAuditoria }}</td>
          <td>{{ log.fechaHora }}</td>
          <td>{{ log.participanteNombre }} ({{ log.participanteDocumento }})</td>
          <td>{{ log.accion }}</td>
          <td>{{ log.entidadAfectada }}</td>
          <td>{{ log.descripcionCambio }}</td>
          <td>{{ log.ipOrigen }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const logs = ref([]);
const loading = ref(false);
const error = ref('');

const fetchLogs = async () => {
  loading.value = true;
  try {
    const res = await api.get('/auditoria');
    logs.value = res.data;
  } catch (err) {
    error.value = 'Error al cargar logs: ' + err.message;
  } finally {
    loading.value = false;
  }
};

onMounted(fetchLogs);
</script>

<style scoped>
table { width: 100%; border-collapse: collapse; }
th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
</style>
