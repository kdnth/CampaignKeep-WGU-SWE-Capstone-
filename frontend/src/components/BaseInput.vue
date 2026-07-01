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
    class="p-2 border-2 rounded-lg bg-gray-400 placeholder:text-gray-700"
    :class="{ 'border-white': !error, 'border-red-500': error }"
  />
</template>
