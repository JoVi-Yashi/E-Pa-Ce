<template>
  <div class="notification-container">
    <Notification
      v-for="notification in notifications"
      :key="notification.id"
      :message="notification.message"
      :type="notification.type"
      :duration="notification.duration"
      @click="removeNotification(notification.id)"
    />
  </div>
</template>

<script setup>
import { useNotificationStore } from '../../stores/notifications';
import { storeToRefs } from 'pinia';
import Notification from './Notification.vue';

const notificationStore = useNotificationStore();
const { notifications } = storeToRefs(notificationStore);
const { removeNotification } = notificationStore;
</script>

<style scoped>
.notification-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: flex-end;
  pointer-events: none;
  width: 350px;
}

.notification-container > * {
  pointer-events: auto;
}
</style>