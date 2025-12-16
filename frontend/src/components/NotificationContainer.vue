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
import { useNotificationStore } from '../stores/notifications';
import { storeToRefs } from 'pinia';
import Notification from './Notification.vue';

const notificationStore = useNotificationStore();
const { notifications } = storeToRefs(notificationStore);
const { removeNotification } = notificationStore;
</script>

<style scoped>
.notification-container {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 9999;
  padding: 20px;
  pointer-events: none;
}

.notification-container > * {
  pointer-events: auto;
  margin-bottom: 10px;
}
</style>