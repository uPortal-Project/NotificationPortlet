<template>
  <b-modal v-model="modalShow">
    <p>Hello from modal!</p>
  </b-modal>
</template>

<script>
import bModal from "bootstrap-vue/es/components/modal/modal";

export default {
  name: "NotificationModal",
  props: {
    msg: String
  },
  components: {
    "b-modal": bModal
  },
  data() {
    return {
      modalShow: true
    };
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
