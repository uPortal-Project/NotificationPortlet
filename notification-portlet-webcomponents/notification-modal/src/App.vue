<template>
  <NotificationModal debug notificationApiUrl="http://localhost:8080/sample-notifications.json" />
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
