<template>
    <slot></slot>
</template>

<script>
/**
 * HACK: This exists because IE/Edge get caught in an infinite event loop when
 * they try to render bootstrap vue modal or button, this provides a feature
 * incomplete, yet functional version that these browsers can fallback to rather
 * than crashing
 */
export default {
  name: "ie-dropdown-item"
};
</script>
