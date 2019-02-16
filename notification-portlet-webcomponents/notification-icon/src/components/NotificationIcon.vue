<template>
  <div class="notification-icon">
    <dropdown id="_uid" no-caret :variant="variant">
      <template slot="button-content">
        <font-awesome-icon icon="bell"/>
        <span class="count">{{ count }}</span>
      </template>
      <dropdown-header>Notifications</dropdown-header>
      <slot v-if="notifications.length < 1" name="empty">
        <dropdown-item>no unread notifications</dropdown-item>
      </slot>
      <notification-item
        v-for="notification in notifications"
        :key="notification.id || notification.body"
        :notification="notification"
      ></notification-item>
      <dropdown-item :href="seeAllNotificationsUrl">See All Notifications</dropdown-item>
    </dropdown>
  </div>
</template>

<script>
import Vue from "vue";
import AsyncComputed from "vue-async-computed";
import ieDropdown from "./ieDropdown";
import ieDropdownHeader from "./ieDropdownHeader";
import ieDropdownItem from "./ieDropdownItem";
import Dropdown from "bootstrap-vue/es/components/dropdown/dropdown";
import DropdownHeader from "bootstrap-vue/es/components/dropdown/dropdown-header";
import DropdownItem from "bootstrap-vue/es/components/dropdown/dropdown-item";
import { library } from "@fortawesome/fontawesome-svg-core";
import { faBell } from "@fortawesome/free-solid-svg-icons/faBell";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import oidc from "@uportal/open-id-connect";
import ky from "ky";
import NotificationItem from "./NotificationItem";

function detectIE() {
  const ua = window.navigator.userAgent;

  const msie = ua.includes("MSIE ");
  const trident = ua.includes("Trident/");
  const edge = ua.includes("Edge/");

  return msie || trident || edge;
}

const isIE = detectIE();

/**
 * HACK: This exists because IE/Edge get caught in an infinite event loop when
 * they try to render bootstrap vue dropdown, dropdown-item, and dropdown-header, this provides a feature
 * incomplete, yet functional version that these browsers can fallback to rather
 * than crashing
 */
const patchedDropdown = isIE ? ieDropdown : Dropdown;
const patchedDropdownHeader = isIE ? ieDropdownHeader : DropdownHeader;
const patchedDropdownItem = isIE ? ieDropdownItem : DropdownItem;

Vue.use(AsyncComputed);

library.add(faBell);

export default {
  name: "NotificationIcon",
  props: {
    userInfoApiUrl: {
      type: String,
      default: "/uPortal/api/v5-1/userinfo"
    },
    notificationApiUrl: {
      type: String,
      default: "/NotificationPortlet/api/v2/notifications"
    },
    seeAllNotificationsUrl: {
      type: String,
      default: "/uPortal/p/notification"
    },
    countAllNotifications: {
      type: Boolean,
      default: false
    },
    debug: {
      type: Boolean,
      default: false
    }
  },
  asyncComputed: {
    notifications: {
      async get() {
        const { notificationApiUrl, debug } = this;

        try {
          const headers = debug
            ? {}
            : {
                Authorization: "Bearer " + (await oidc()).encoded,
                "content-type": "application/jwt"
              };
          return await ky.get(notificationApiUrl, { headers }).json();
        } catch (err) {
          // eslint-disable-next-line no-console
          console.error(err);
          return [];
        }
      },
      default: [],
      lazy: true
    }
  },
  computed: {
    count() {
      if (this.countAllNotifications) {
        return this.notifications.length;
      } else {
        return this.notifications.filter(
          ({ attributes }) => !JSON.parse(attributes?.READ?.[0] || "true")
        ).length;
      }
    },
    variant() {
      return this.count < 1 ? "default" : "danger";
    }
  },
  components: {
    Dropdown: patchedDropdown,
    DropdownHeader: patchedDropdownHeader,
    DropdownItem: patchedDropdownItem,
    FontAwesomeIcon,
    NotificationItem,
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
.notification-icon /deep/ {
  // core bootstrap framework
  @import "../../node_modules/bootstrap/scss/functions";
  @import "../../node_modules/bootstrap/scss/variables";
  @import "../../node_modules/bootstrap/scss/mixins";
  // bootstrap styles needed by component
  @import "../../node_modules/bootstrap/scss/reboot";
  @import "../../node_modules/bootstrap/scss/dropdown";
  @import "../../node_modules/bootstrap/scss/buttons";

  svg.svg-inline--fa {
    width: 1.25rem;
  }
  span.count {
    position: relative;
    top: -.25rem;
    left: .25rem;
  }
  .dropdown-menu {
    max-width: 30rem;

    .dropdown-item {
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
}
</style>
