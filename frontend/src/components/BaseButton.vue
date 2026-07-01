<script setup lang="ts">
withDefaults(
  defineProps<{
    disabled?: boolean
    loading?: boolean
    type?: 'button' | 'submit'
  }>(),
  {
    disabled: false,
    loading: false,
    type: 'button',
  },
)
</script>
<template>
  <button
    :type="type"
    :disabled="disabled || loading"
    class="w-fit py-2.5 px-8 my-4 bg-white text-black rounded-lg cursor-pointer disabled:opacity-60 disabled:cursor-not-allowed hover:bg-gray-500 hover:text-white hover:border-2 hover:border-white"
  >
    <slot />
  </button>
</template>
