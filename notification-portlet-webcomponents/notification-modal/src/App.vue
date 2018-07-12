<template>
  <NotificationModal userInfoApiUrl="http://localhost:8080/uPortal/api/v5-1/userinfo" notificationApiUrl="http://localhost:8080/NotificationPortlet/api/v2/notifications" />
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
