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
export default {
  name: "ie-dropdown",
  data: () => ({isActive: false})
};
</script>
