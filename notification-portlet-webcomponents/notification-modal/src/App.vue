<template>
  <notification-modal debug notification-api-url="./sample-notifications.json"></notification-modal>
</template>

<script>
import NotificationModal from "./components/NotificationModal.vue";

export default {
  name: "app",
  components: {
    NotificationModal
  }
};
</script>
