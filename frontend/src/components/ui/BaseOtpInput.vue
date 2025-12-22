<template>
  <div class="otp-input-container">
    <label v-if="label" class="input-label">{{ label }}</label>
    <div class="otp-inputs">
      <input
        v-for="(digit, index) in digits"
        :key="index"
        :ref="el => inputRefs[index] = el"
        type="text"
        inputmode="numeric"
        maxlength="1"
        :value="digits[index]"
        @input="handleInput($event, index)"
        @keydown="handleKeyDown($event, index)"
        @paste="handlePaste"
        @focus="handleFocus($event)"
        class="otp-box animate-pop"
        :class="{ 'filled': digits[index] !== '' }"
        :disabled="disabled"
      />
    </div>
    <p v-if="helpText" class="help-text">{{ helpText }}</p>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';

const props = defineProps({
  length: {
    type: Number,
    default: 6
  },
  modelValue: {
    type: String,
    default: ''
  },
  label: String,
  helpText: String,
  disabled: Boolean
});

const emit = defineEmits(['update:modelValue']);

const digits = ref(Array(props.length).fill(''));
const inputRefs = ref([]);

watch(() => props.modelValue, (newVal) => {
  if (newVal === digits.value.join('')) return;
  const chars = newVal.split('').slice(0, props.length);
  digits.value = Array(props.length).fill('').map((_, i) => chars[i] || '');
}, { immediate: true });

const emitValue = () => {
  emit('update:modelValue', digits.value.join(''));
};

const handleInput = (event, index) => {
  const value = event.target.value;
  // Allow only numbers
  if (!/^\d*$/.test(value)) {
    event.target.value = digits.value[index];
    return;
  }

  // Take only the last character entered
  const char = value.slice(-1);
  digits.value[index] = char;
  
  emitValue();

  if (char && index < props.length - 1) {
    inputRefs.value[index + 1].focus();
  }
};

const handleKeyDown = (event, index) => {
  if (event.key === 'Backspace') {
    if (!digits.value[index] && index > 0) {
      digits.value[index - 1] = '';
      inputRefs.value[index - 1].focus();
      emitValue();
    } else {
        digits.value[index] = '';
        emitValue();
    }
  } else if (event.key === 'ArrowLeft' && index > 0) {
    inputRefs.value[index - 1].focus();
  } else if (event.key === 'ArrowRight' && index < props.length - 1) {
    inputRefs.value[index + 1].focus();
  }
};

const handlePaste = (event) => {
  event.preventDefault();
  const pasteData = event.clipboardData.getData('text').replace(/\D/g, '').slice(0, props.length);
  
  if (pasteData) {
    const chars = pasteData.split('');
    chars.forEach((char, i) => {
      digits.value[i] = char;
    });
    emitValue();
    
    // Focus last filled
    const lastIndex = Math.min(chars.length, props.length) - 1;
    if (lastIndex >= 0 && inputRefs.value[lastIndex]) {
        inputRefs.value[lastIndex].focus();
    }
  }
};

const handleFocus = (event) => {
  event.target.select();
};

onMounted(() => {
    // If we want to auto-focus first input. Maybe optional?
});
</script>

<style scoped>
.otp-input-container {
  margin-bottom: 1.5rem;
  width: 100%;
}

.input-label {
  display: block;
  margin-bottom: 0.75rem;
  font-weight: 600;
  color: #4a5568;
  font-size: 0.9rem;
}

.otp-inputs {
  display: flex;
  gap: 0.75rem;
  justify-content: space-between;
}

.otp-box {
  width: 100%;
  aspect-ratio: 1; /* Square */
  max-width: 50px;
  height: 50px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  text-align: center;
  font-size: 1.5rem;
  font-weight: 700;
  color: #1a202c;
  background: white;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  outline: none;
}

.otp-box:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
  transform: translateY(-2px);
}

.otp-box.filled {
  border-color: #a5b4fc;
  background: #f8fafc;
}

.otp-box:disabled {
  background: #f1f5f9;
  cursor: not-allowed;
  opacity: 0.7;
}

.help-text {
  margin-top: 0.75rem;
  font-size: 0.85rem;
  color: #718096;
  text-align: center;
}

/* Animation */
.animate-pop {
    animation: popIn 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

@keyframes popIn {
    from { opacity: 0; transform: scale(0.8); }
    to { opacity: 1; transform: scale(1); }
}

@media (max-width: 400px) {
    .otp-inputs {
        gap: 0.5rem;
    }
    .otp-box {
        font-size: 1.2rem;
        height: 40px;
        max-width: 40px;
    }
}
</style>
