<template>
  <BaseModal :show="show" title="Ajustar Foto de Perfil" @close="$emit('cancel')" maxWidth="500px">
    <div class="crop-container" @mousedown="startDrag" @touchstart="startDrag" @mousemove="onDrag" @touchmove="onDrag" @mouseup="endDrag" @touchend="endDrag" @mouseleave="endDrag">
      <div class="crop-viewport" ref="viewport">
        <div class="image-container" :style="imageStyle">
          <img :src="imageSrc" ref="image" @load="onImageLoad" />
        </div>
        <div class="crop-mask"></div>
      </div>
    </div>

    <div class="crop-controls">
      <div class="zoom-slider">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
        <input type="range" v-model="zoom" :min="minZoom" :max="maxZoom" step="0.01" />
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line><line x1="11" y1="8" x2="11" y2="14"></line><line x1="8" y1="11" x2="14" y2="11"></line></svg>
      </div>
      <p class="crop-hint">Arrastra para posicionar y usa el slider para acercar</p>
    </div>

    <template #footer>
      <button class="btn-secondary" @click="$emit('cancel')">Cancelar</button>
      <button class="btn-primary" @click="cropImage">Confirmar Corte</button>
    </template>
  </BaseModal>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import BaseModal from './BaseModal.vue';

const props = defineProps({
  show: Boolean,
  imageSrc: String
});

const emit = defineEmits(['save', 'cancel']);

const viewport = ref(null);
const image = ref(null);

const zoom = ref(1);
const minZoom = ref(1);
const maxZoom = ref(3);

const offset = ref({ x: 0, y: 0 });
const isDragging = ref(false);
const startPos = ref({ x: 0, y: 0 });

const onImageLoad = () => {
    if (!image.value || !viewport.value) return;
    
    const vWidth = viewport.value.offsetWidth;
    const vHeight = viewport.value.offsetHeight;
    const iWidth = image.value.naturalWidth;
    const iHeight = image.value.naturalHeight;
    
    // Calculate initial scale to cover the viewport
    const scaleX = vWidth / iWidth;
    const scaleY = vHeight / iHeight;
    const initialScale = Math.max(scaleX, scaleY);
    
    minZoom.value = initialScale;
    zoom.value = initialScale;
    maxZoom.value = initialScale * 4;
    
    // Center image
    offset.value = { x: 0, y: 0 };
};

const imageStyle = computed(() => ({
  transform: `translate(${offset.value.x}px, ${offset.value.y}px) scale(${zoom.value})`,
  cursor: isDragging.value ? 'grabbing' : 'grab'
}));

const startDrag = (e) => {
  isDragging.value = true;
  const pos = e.type === 'touchstart' ? e.touches[0] : e;
  startPos.value = {
    x: pos.clientX - offset.value.x,
    y: pos.clientY - offset.value.y
  };
};

const onDrag = (e) => {
  if (!isDragging.value) return;
  const pos = e.type === 'touchmove' ? e.touches[0] : e;
  offset.value = {
    x: pos.clientX - startPos.value.x,
    y: pos.clientY - startPos.value.y
  };
  
  // Constrain limits (optional, but good for UX)
  // For now let's keep it simple
};

const endDrag = () => {
  isDragging.value = false;
};

const cropImage = () => {
  const canvas = document.createElement('canvas');
  const size = 300; // Target resolution
  canvas.width = size;
  canvas.height = size;
  const ctx = canvas.getContext('2d');

  if (!image.value || !viewport.value) return;

  const vRect = viewport.value.getBoundingClientRect();
  const iRect = image.value.getBoundingClientRect();

  // Draw the image on canvas shifted by the current relative position
  const sx = (vRect.left - iRect.left) / zoom.value;
  const sy = (vRect.top - iRect.top) / zoom.value;
  const sWidth = vRect.width / zoom.value;
  const sHeight = vRect.height / zoom.value;

  ctx.drawImage(
    image.value,
    sx * (image.value.naturalWidth / image.value.width),
    sy * (image.value.naturalHeight / image.value.height),
    sWidth * (image.value.naturalWidth / image.value.width),
    sHeight * (image.value.naturalHeight / image.value.height),
    0, 0, size, size
  );

  const croppedDataUrl = canvas.toDataURL('image/jpeg', 0.8);
  emit('save', croppedDataUrl);
};

// Reset zoom and offset when a new image is loaded
watch(() => props.imageSrc, () => {
    zoom.value = 1;
    offset.value = { x: 0, y: 0 };
});
</script>

<style scoped>
.crop-container {
    width: 100%;
    aspect-ratio: 1;
    background: #000;
    position: relative;
    overflow: hidden;
    border-radius: 12px;
    touch-action: none;
}

.crop-viewport {
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
}

.image-container {
    position: absolute;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: transform 0.05s linear;
}

.image-container img {
    max-width: none;
    user-select: none;
    pointer-events: none;
}

.crop-mask {
    position: absolute;
    inset: 0;
    pointer-events: none;
    box-shadow: 0 0 0 1000px rgba(15, 23, 42, 0.75);
    border-radius: 50%;
    border: 3px solid rgba(255, 255, 255, 0.7);
    margin: 40px; /* Viewport margin */
}

.crop-controls {
    padding: 1.5rem 0;
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.zoom-slider {
    display: flex;
    align-items: center;
    gap: 1rem;
    color: #64748b;
}

.zoom-slider input {
    flex: 1;
    accent-color: #6366f1;
    height: 6px;
    border-radius: 3px;
}

.crop-hint {
    text-align: center;
    font-size: 0.85rem;
    color: #94a3b8;
    margin: 0;
}

.btn-primary {
    background: linear-gradient(135deg, #6366f1, #a855f7);
    color: white;
    border: none;
    padding: 0.75rem 1.5rem;
    border-radius: 10px;
    font-weight: 700;
    cursor: pointer;
    transition: all 0.3s;
}

.btn-primary:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(99, 102, 241, 0.4);
}

.btn-secondary {
    background: #f1f5f9;
    color: #64748b;
    border: none;
    padding: 0.75rem 1.5rem;
    border-radius: 10px;
    font-weight: 700;
    cursor: pointer;
}
</style>
