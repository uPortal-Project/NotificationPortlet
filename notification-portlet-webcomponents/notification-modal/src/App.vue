<template>
  <NotificationModal msg="Welcome to Your Vue.js App"/>
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
