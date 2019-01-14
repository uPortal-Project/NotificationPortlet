<template>
  <div class="dropdown">
    <button class="btn btn-secondary dropdown-toggle"
            type="button"
            id="dropdownMenuButton"
            data-toggle="dropdown"
            aria-haspopup="true"
            aria-expanded="false"
            v-on:click="isActive = !isActive">
      <slot name="button-content"></slot>
    </button>
    <div class="dropdown-menu" aria-labelledby="dropdownMenuButton" v-bind:class="{ show: isActive }">
      <slot></slot>
    </div>
  </div>
</template>

<script>
/**
 * HACK: This exists because IE/Edge get caught in an infinite event loop when
 * they try to render bootstrap vue modal or button, this provides a feature
 * incomplete, yet functional version that these browsers can fallback to rather
 * than crashing
 */
export default {
  name: "ie-dropdown",
  data: () => ({isActive: false})
};
</script>
