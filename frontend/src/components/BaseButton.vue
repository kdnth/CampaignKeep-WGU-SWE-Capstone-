<script setup lang="ts">
withDefaults(
  defineProps<{
    disabled?: boolean
    loading?: boolean
    type?: 'button' | 'submit'
    variant?: 'submit' | 'primary' | 'danger' | 'cancel' | 'secondary'
  }>(),
  {
    disabled: false,
    loading: false,
    type: 'button',
    variant: 'secondary',
  },
)
</script>
<template>
  <button
    :type="type"
    :disabled="disabled || loading"
    class="w-fit py-2.5 px-8 my-4 rounded-lg cursor-pointer disabled:opacity-60 disabled:cursor-not-allowed transition-colors hover:text-white hover:border-2 hover:border-white"
    :class="{
      'hover:bg-neutral-400 bg-white text-black': variant === 'submit',
      'hover:bg-purple-800 bg-purple-600 text-white': variant === 'primary',
      'hover:bg-red-600 bg-white text-black': variant === 'cancel',
      'hover:bg-red-800 bg-red-600 text-white': variant === 'danger',
      'hover:bg-purple-600 bg-white text-black': variant === 'secondary',
    }"
  >
    <slot />
  </button>
</template>
