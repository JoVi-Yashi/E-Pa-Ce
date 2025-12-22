<template>
  <div class="input-wrapper">
    <label v-if="label" :for="id" class="input-label">
      {{ label }}
      <span v-if="required" class="required">*</span>
    </label>
    <input
      :id="id"
      :type="type"
      :value="modelValue"
      :placeholder="placeholder"
      :disabled="disabled"
      :readonly="readonly"
      :class="['base-input', size, { error: hasError }]"
      @input="handleInput"
      @blur="handleBlur"
      @focus="handleFocus"
    />
    <p v-if="helpText" class="help-text">{{ helpText }}</p>
    <p v-if="errorMessage && hasError" class="error-message">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  id: {
    type: String,
    required: true
  },
  modelValue: {
    type: [String, Number],
    default: ''
  },
  label: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'text'
  },
  size: {
    type: String,
    default: 'medium', // small, medium, large
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  required: {
    type: Boolean,
    default: false
  },
  disabled: {
    type: Boolean,
    default: false
  },
  readonly: {
    type: Boolean,
    default: false
  },
  helpText: {
    type: String,
    default: ''
  },
  errorMessage: {
    type: String,
    default: ''
  },
  hasError: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:modelValue', 'blur', 'focus']);

const handleInput = (event) => {
  emit('update:modelValue', event.target.value);
};

const handleBlur = (event) => {
  emit('blur', event);
};

const handleFocus = (event) => {
  emit('focus', event);
};
</script>

<style scoped>
.input-wrapper {
  margin-bottom: 1.5rem;
  width: 100%;
}

.input-label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #333;
}

.required {
  color: #e74c3c;
}

.base-input {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 12px;
  font-family: inherit;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  box-sizing: border-box;
}

.base-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.base-input.error {
  border-color: #e74c3c;
}

.base-input.error:focus {
  box-shadow: 0 0 0 3px rgba(231, 76, 60, 0.1);
}

/* Sizes */
.base-input.small {
  padding: 0.5rem 0.75rem;
  font-size: 0.875rem;
}

.base-input.medium {
  padding: 0.75rem 1rem;
  font-size: 1rem;
}

.base-input.large {
  padding: 1rem 1.25rem;
  font-size: 1.125rem;
}

.base-input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.help-text {
  margin-top: 0.5rem;
  font-size: 0.875rem;
  color: #666;
}

.error-message {
  margin-top: 0.5rem;
  font-size: 0.875rem;
  color: #e74c3c;
  font-weight: 500;
}
</style>