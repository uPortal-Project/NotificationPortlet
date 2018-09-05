<template>
  <div class="notification-banner">
    <b-alert v-for="(notification, index) in notifications" :key="index" varient="info" show>
      <h3>
        <i class="fas fa-info"></i>
        {{notification.title}}
      </h3>
      <span v-html="notification.body" />
    </b-alert>
  </div>
</template>

<script>
import bAlert from "bootstrap-vue/es/components/alert/alert";
import oidc from "@uportal/open-id-connect/esm/open-id-connect";
import { get } from "axios";

export default {
  name: 'NotificationBanner',

  props: {
    debug: {
      type: Boolean,
      default: false
    },
    userInfoApiUrl: {
      type: String,
      default: "/uPortal/api/v5-1/userinfo"
    },
    notificationApiUrl: {
      type: String,
      default: "/NotificationPortlet/api/v2/notifications"
    }
  },

  components: {
    "b-alert": bAlert
  },

  data() {
    return {
      // list of notifications to display
      notifications: []
    };
  },

  methods: {
    async fetchNotifications() {
      // read props
      const { debug, notificationApiUrl, userInfoApiUrl } = this;

      try {
        // Obtain an OIDC Id Token, except in debug mode
        const { encoded: token } = debug
          ? { encoded: null }
          : await oidc({ userInfoApiUrl });


        // gather notifications
        const { data: notifications } = await get(notificationApiUrl, {
          withCredentials: true,
          headers: {
            Authorization: `Bearer ${token}`,
            "content-type": "application/jwt"
          }
        });

        // store notifications to state
        this.notifications = notifications;
      } catch (err) {
        // eslint-disable-next-line no-console
        console.error(err);
      }
    }
  },

  // entrypoint
  created() {
    return this.fetchNotifications();
  },

}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
