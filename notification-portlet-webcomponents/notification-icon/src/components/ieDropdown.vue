<template>
  <div class="dropdown">
    <button class="btn btn-secondary"
            type="button"
            id="dropdownMenuButton"
            data-toggle="dropdown"
            aria-haspopup="true"
            aria-expanded="false"
            :class="['btn-' + variant]"
            v-on:click="isActive = !isActive">
      <slot name="button-content"></slot>
    </button>
    <div class="dropdown-menu" aria-labelledby="dropdownMenuButton" v-bind:class="{ show: isActive }">
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: "ie-dropdown",
  props: ['variant'],
  data: () => ({
    isActive: false,
  })
};
</script>

<style lang="scss" scoped>
@media screen and (max-width: 768px) {
  .dropdown-menu {
    right: 0;
    left: auto !important;
  }
}
</style>
