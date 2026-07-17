<script setup lang="ts">
defineProps<{
  id?: string
  type?: string
  modelValue: string
  error?: boolean
}>()

defineEmits<{
  'update:modelValue': [value: string]
}>()
</script>
<template>
  <input
    :id="id"
    :type="type"
    :value="modelValue"
    @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
    class="w-full rounded-lg border-2 bg-input p-2 text-fg placeholder:text-placeholder"
    :class="{
      'border-input-border': !error,
      'border-danger': error,
    }"
  />
</template>
