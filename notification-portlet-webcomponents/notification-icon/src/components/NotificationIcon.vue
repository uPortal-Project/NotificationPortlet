<template>
  <div class="notification-icon">
    <dropdown id="_uid" no-caret toggle-class="btn-icon">
      <template slot="button-content">
        <font-awesome-icon icon="bell" size="2x" fixed-width/>
        <span class="count" :class="{ 'alert': count > 1 }">{{ countDisplay }}</span>
      </template>
      <dropdown-header tag="h3">Notifications</dropdown-header>
      <slot v-if="notifications.length < 1" name="empty">
        <dropdown-item>no unread notifications</dropdown-item>
      </slot>
      <notification-item
        v-for="notification in notifications"
        :key="notification.id || notification.body"
        :notification="notification"
      ></notification-item>
      <dropdown-item :href="seeAllNotificationsUrl" class="text-center">See All Notifications</dropdown-item>
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
      return this.countAllNotifications ?
          this.notifications.length :
          this.notifications.filter(
            ({ attributes }) => !JSON.parse(attributes?.READ?.[0] || "true")
          ).length;
    },
    countDisplay() {
      return this.count < 10 ? this.count : '*';
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

  .btn-icon {
    background: transparent;
    border-color: transparent;
    width: 18px;
    height: 25px;
    margin-left: 4px;
    margin-right: 4px;
    background: transparent;
    border: 0 none;
    line-height: 0;
    padding: 0;
    position: relative;
    color: #FFFFFF;
    color: var(--notif-icon-fg-color, #FFFFFF);

    &:hover {
      background: transparent;
      color: var(--notif-icon-fg-hover-color, #FFFFFF);
    }
    &:not(:disabled), :disabled {
      &:focus, &:active {
        outline: none;
        box-shadow: none;
        background-color: transparent;
        border-color: transparent;
      }
    }
    

    .count {
      border: 1px solid var(--notif-icon-fg-color, #FFFFFF);
      border-radius: 50%;
      display: block;
      width: 1.5rem;
      height: 1.5rem;
      font-size: 1.1rem;
      line-height: 1.2rem;
      position: absolute;
      top: -.25rem;
      right: -.5rem;

      &.alert {
        color: var(--notif-icon-fg-alert-color, #FFFFFF);
        background-color: #dc3545;
        background-color: var(--notif-icon-bg-alert-color, #dc3545);
        border-color: #dc3545;
        border-color: var(--notif-icon-bg-alert-color, #dc3545);
      }
    }
    &:after {
      display: none;
    }
  }
  .dropdown-menu {
    max-width: 30rem;
    background: #E5E5E5;
    background: var(--notif-heading-bg-color, #E5E5E5);
    padding: 0px;

    .dropdown-header {
      padding: 1rem;
      font-size: 1.5rem;
      text-align: center;
      font-weight: normal;
    }

    .dropdown-item {
      overflow: hidden;
      text-overflow: ellipsis;
      padding: 1rem 1.5rem;
      font-size: 1.1rem;
      border-top: #E5E5E5;
      border-top: 1px solid var(--notif-item-border-color, #E5E5E5);

      &.text-center {
        text-align: center;
      }
    }
  }
}
</style>
