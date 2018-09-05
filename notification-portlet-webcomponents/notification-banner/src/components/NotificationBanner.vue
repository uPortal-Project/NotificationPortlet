<template>
  <div class="notification-banner">
    <b-alert varient="info" show>
      Hello World!
    </b-alert>
  </div>
</template>

<script>
import bAlert from "bootstrap-vue/es/components/alert/alert";
import oidc from "@uportal/open-id-connect/esm/open-id-connect";

export default {
  name: 'NotificationBanner',

  props: {
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
      const { notificationApiUrl, userInfoApiUrl } = this;

      try {
        // get user token, skipped in debug mode
        const { encoded: token } = await oidc({ userInfoApiUrl });

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
