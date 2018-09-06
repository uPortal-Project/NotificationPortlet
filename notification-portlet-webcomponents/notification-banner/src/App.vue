<template>
  <div id="app">
    <img alt="Vue logo" src="./assets/logo.png">
    <NotificationBanner debug="true" notificationApiUrl="sample-notifications.json" />
  </div>
</template>

<script>
import NotificationBanner from './components/NotificationBanner.vue'

export default {
  name: 'app',
  components: {
    NotificationBanner
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
