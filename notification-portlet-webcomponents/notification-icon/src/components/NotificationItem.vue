<template>
  <dropdown-item :target="target" :class="{ read: isRead, unread: !isRead }" :href="url" @click="onClickAction()">{{notification.body}}</dropdown-item>
</template>

<script>
import DropdownItem from "bootstrap-vue/es/components/dropdown/dropdown-item";

export default {
  name: "NotificationItem",
  props: {
    notification: Object
  },
  computed: {
    redirectAction() {
      return this.notification?.availableActions?.some?.(
        ({ id }) => id === "MarkAsReadAndRedirectAction"
      );
    },
    isRead() {
      return JSON.parse(this.notification?.attributes?.READ?.[0] || "true");
    },
    url() {
      return this.redirectAction ? "javascript:void(0)" : this.notification.url;
    },
    onClickAction() {
      if (this.redirectAction) {
        return () => {
          let form = document.createElement("form");
          form.action = this.notification.url;
          form.method = "POST";
          form.style.display = "none";
          document.body.appendChild(form);
          form.submit();
        };
      }
      return () => {};
    },
    target() {
      return this.redirectAction ? "_self" : "_blank";
    }
  },
  components: {
    DropdownItem
  }
};
</script>

<style lang="scss" scoped>
.unread {
  background-color: aliceblue !important;
}
.read {
  background-color: transparent !important;
}
</style>