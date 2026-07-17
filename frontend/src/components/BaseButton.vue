<script setup lang="ts">
withDefaults(
  defineProps<{
    disabled?: boolean
    loading?: boolean
    type?: 'button' | 'submit'
    variant?: 'submit' | 'primary' | 'danger' | 'cancel' | 'secondary' | 'agree'
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
    class="w-fit cursor-pointer rounded-lg px-8 py-2.5 transition-colors hover:border-2 hover:border-border-strong hover:text-white disabled:cursor-not-allowed disabled:opacity-60"
    :class="{
      'bg-surface text-fg hover:bg-surface-muted': variant === 'submit',
      'bg-accent text-white hover:bg-accent-hover': variant === 'primary',
      'bg-surface text-fg hover:bg-danger': variant === 'cancel',
      'bg-danger text-white hover:bg-danger-hover': variant === 'danger',
      'bg-surface text-fg hover:bg-accent': variant === 'secondary',
      'bg-surface text-fg hover:bg-success': variant === 'agree',
    }"
  >
    <slot />
  </button>
</template>
