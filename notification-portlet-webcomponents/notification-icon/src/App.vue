<template>
  <header class="example-heading">
    <notification-icon debug notification-api-url="sample-notifications.json"></notification-icon>
  </header>
</template>

<script>
import NotificationIcon from './components/NotificationIcon.vue'

export default {
  name: 'app',
  components: {
    NotificationIcon
  }
}
</script>
<style lang="scss">
html {
  font-size: 10px;
  font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
}
body {
  font-size: 14px;
}
.example-heading {
  background-color: #001c4d;
  padding: 12px;
}
</style>
