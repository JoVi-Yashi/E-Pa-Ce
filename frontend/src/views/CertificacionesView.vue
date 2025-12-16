<template>
  <div class="certificaciones">
    <h1>Gestión de Certificaciones</h1>
     <!-- Emitir is creating a new one really -->
    <button @click="resetForm(); showForm = true">Emitir Certificación</button>
    <router-link to="/">Volver al Dashboard</router-link>

    <div v-if="loading">Cargando...</div>
    <div v-if="error" style="color: red">{{ error }}</div>

    <table border="1" style="width: 100%; border-collapse: collapse; margin-top: 20px;">
      <thead>
        <tr>
          <th>ID</th>
          <th>Código</th>
          <th>Fecha Emisión</th>
          <th>Participio ID</th>
          <th>Participante</th>
          <th>Evento</th>
          <th>Emitido?</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="c in certificaciones" :key="c.idCertificacion">
          <td>{{ c.idCertificacion }}</td>
          <td>{{ c.codigoUnicoAPI }}</td>
          <td>{{ c.fechaEmision }}</td>
          <td>{{ c.participacionId }}</td>
          <td>{{ c.participanteNombre }}</td>
          <td>{{ c.eventoNombre }}</td>
          <td>{{ c.emitido ? 'Sí' : 'No' }}</td>
          <td>
            <button @click="deleteCert(c.idCertificacion)">Eliminar</button>
            <button v-if="c.rutaPDF">Descargar PDF</button> <!-- Placeholder -->
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showForm" class="form-overlay">
      <div class="form-content">
        <h3>Emitir Certificación</h3>
        <form @submit.prevent="submitForm">
          <div>
            <label>ID Participación:</label>
            <input v-model="form.participacionId" type="number" required />
          </div>
          <div style="margin-top: 10px;">
            <button type="submit">Emitir</button>
            <button type="button" @click="showForm = false">Cancelar</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const certificaciones = ref([]);
const loading = ref(false);
const error = ref('');
const showForm = ref(false);
const form = ref({ participacionId: null });

const fetchCertificaciones = async () => {
  loading.value = true;
  try {
    const res = await api.get('/certificaciones');
    certificaciones.value = res.data;
  } catch (err) {
    error.value = 'Error al cargar certificaciones: ' + err.message;
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  form.value = { participacionId: null };
};

const deleteCert = async (id) => {
  if (!confirm('Eliminar seguro?')) return;
  try {
    await api.delete(`/certificaciones/${id}`);
    fetchCertificaciones();
  } catch (err) {
    alert(err.message);
  }
};

const submitForm = async () => {
  try {
    await api.post('/certificaciones/emitir', form.value);
    showForm.value = false;
    fetchCertificaciones();
  } catch (err) {
     alert('Error: ' + (err.response?.data?.message || err.message));
  }
};

onMounted(fetchCertificaciones);
</script>

<style scoped>
.form-overlay {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center;
}
.form-content { background: white; padding: 20px; border-radius: 5px; }
table { width: 100%; border-collapse: collapse; }
th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
</style>
