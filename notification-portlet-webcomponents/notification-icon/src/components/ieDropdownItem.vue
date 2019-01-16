<template>
    <a v-if="href" class="dropdown-item" :href="href"><slot></slot></a>
    <div v-else class="dropdown-item">
        <slot></slot>
    </div>
</template>

<script>
export default {
  name: "ie-dropdown-item",
  props: ['href'],
};
</script>
