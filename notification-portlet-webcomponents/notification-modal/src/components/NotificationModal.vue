<template>
  <b-modal
    v-model="modalShow"
    :title="currentNotification.title"
    no-close-on-backdrop
    no-close-on-esc
    ok-only
    @ok="handleOk">
    {{ currentNotification.body }}
  </b-modal>
</template>

<script>
import bModal from "bootstrap-vue/es/components/modal/modal";
import oidc from "@uportal/open-id-connect/esm/open-id-connect";
import { get } from "axios";

export default {
  name: "NotificationModal",

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
    },
    filter: {
      type: String,
      default: ""
    }
  },

  components: {
    "b-modal": bModal
  },

  data() {
    return {
      notifications: []
    };
  },

  methods: {
    async fetchNotifications() {
      // read props
      const { debug, filter, notificationApiUrl, userInfoApiUrl } = this;

      try {
        // get user token, skipped in debug mode
        const { encoded: token } = debug
          ? { encoded: null }
          : await oidc({ userInfoApiUrl });

        // gather notifications
        const { data: notifications } = await get(notificationApiUrl + filter, {
          withCredentials: true,
          headers: {
            Authorization: `Bearer ${token}`,
            "content-type": "application/jwt"
          }
        });

        this.notifications = notifications;
      } catch (err) {
        // eslint-disable-next-line no-console
        console.error(err);
      }
    },

    handleOk(evt) {
      if (this.notifications.length > 1) {
        evt.preventDefault();
        this.notifications.shift();
      }
    }
  },

  created() {
    return this.fetchNotifications();
  },

  computed: {
    modalShow() {
      return this.notifications.length > 0;
    },
    currentNotification() {
      if (this.notifications.length < 0) {
        return {};
      }

      return this.notifications[0];
    }
  }
};
</script>

<style lang="scss">
// core bootstrap framework
@import "../../node_modules/bootstrap/scss/functions.scss";
@import "../../node_modules/bootstrap/scss/variables.scss";
@import "../../node_modules/bootstrap/scss/mixins.scss";
// bootstrap styles needed by page
@import "../../node_modules/bootstrap/scss/utilities.scss";
@import "../../node_modules/bootstrap/scss/type.scss";
@import "../../node_modules/bootstrap/scss/buttons.scss";
@import "../../node_modules/bootstrap/scss/close.scss";
@import "../../node_modules/bootstrap/scss/modal.scss";
</style>
