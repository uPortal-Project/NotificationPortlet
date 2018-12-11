<template>
  <notification-icon debug notification-api-url="sample-notifications.json"></notification-icon>
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
