<template>
  <!-- escape key, clicking the background, and the close button are all availible
    unless there are availible actions, which requires a user to click buttons to continue -->
  <b-modal
    v-model="modalShow"
    :title=currentNotification.title
    :hide-header-close=hasActions
    :no-close-on-backdrop=hasActions
    :no-close-on-esc=hasActions
    :hide-footer=!hasActions
    @hide="handleClose">

    {{ currentNotification.body }}

    <!-- The footer only displays when there are availible actions to render -->
    <div slot="modal-footer">
      <b-button
        variant="primary"
        v-for="action in currentNotification.availableActions"
        :key="action.id"
        @click="handleAction(action.apiUrl)">

        {{ action.label }}
      </b-button>
    </div>
  </b-modal>
</template>

<script>
import bModal from "bootstrap-vue/es/components/modal/modal";
import bButton from "bootstrap-vue/es/components/button/button";
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
    "b-modal": bModal,
    "b-button": bButton
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

        // store notifications to state
        // @see modalShow - for logic determining if notification should be shown
        // @see currentNotification - for logic rendering a modal
        this.notifications = notifications;
      } catch (err) {
        // eslint-disable-next-line no-console
        console.error(err);
      }
    },

    // handle the close event
    handleClose(evt) {
      // if there are more notifications, keep modal open
      if (this.notifications.length > 1) {
        evt.preventDefault();
      }

      // remove current notification from list
      // this will automatically move to the next notification
      // @see currentNotification - for how next notification will display
      this.notifications.shift();
    }
  },

  handleAction(actionUrl) {
    return () => (window.location = actionUrl);
  },

  // entrypoint
  created() {
    return this.fetchNotifications();
  },

  computed: {
    // if there are notifications in this filter, display modal
    // @see handleClose - for to how notifications are cleared
    modalShow() {
      return this.notifications.length > 0;
    },

    // current notification is the first notification in the list
    // the template reads values directly from current notification
    // @see handleClose - for to how notifications are cleared
    currentNotification() {
      if (this.notifications.length < 0) {
        return {};
      }

      return this.notifications[0];
    },

    // determine if current notification has availible actions
    hasActions() {
      return (
        this.currentNotification &&
        this.currentNotification.availableActions &&
        this.currentNotification.availableActions.length > 0
      );
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
