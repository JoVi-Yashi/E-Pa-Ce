import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useConfirmStore = defineStore('confirm', () => {
    const show = ref(false);
    const title = ref('Confirmación');
    const message = ref('¿Estás seguro de realizar esta acción?');
    const confirmText = ref('Confirmar');
    const cancelText = ref('Cancelar');
    const type = ref('primary'); // primary, danger, warning

    let resolvePromise = null;

    const ask = (options) => {
        title.value = options.title || 'Confirmación';
        message.value = options.message || '¿Estás seguro?';
        confirmText.value = options.confirmText || 'Confirmar';
        cancelText.value = options.cancelText || 'Cancelar';
        type.value = options.type || 'primary';
        
        show.value = true;

        return new Promise((resolve) => {
            resolvePromise = resolve;
        });
    };

    const confirm = () => {
        show.value = false;
        if (resolvePromise) resolvePromise(true);
    };

    const cancel = () => {
        show.value = false;
        if (resolvePromise) resolvePromise(false);
    };

    return {
        show,
        title,
        message,
        confirmText,
        cancelText,
        type,
        ask,
        confirm,
        cancel
    };
});
