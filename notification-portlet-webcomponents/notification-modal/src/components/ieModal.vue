<template>
  <div class="modal" tabindex="-1" role="dialog" v-if="visible" style="display: block">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">
            {{ title }}
          </h5>
          <button type="button" class="close" aria-label="Close" @click="clickHandler" v-if="!hideHeaderClose">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
          <slot></slot>
        </div>
        <div class="modal-footer" v-if="!hideFooter">
          <slot name="modal-footer"></slot>
        </div>
      </div>
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
  name: "ie-modal",

  props: {
    title: {
      type: String
    },
    visible: {
      type: Boolean,
      default: false
    },
    hideFooter: {
      type: Boolean,
      default: false
    },
    hideHeaderClose: {
      type: Boolean,
      default: false
    }
  },

  methods: {
    clickHandler(evt) {
      this.$emit("hide", evt);
    }
  }
};
</script>
