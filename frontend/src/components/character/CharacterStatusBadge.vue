<script setup lang="ts">
import type { CharacterStatus } from '@/utils/characterStatus'

defineProps<{
  status: CharacterStatus
}>()
</script>

<template>
  <span
    class="rounded-lg px-3 py-1 text-sm font-semibold capitalize"
    :class="{
      'bg-green-900 text-green-200': status === 'alive',
      'bg-yellow-900 text-yellow-200': status === 'down',
      'bg-red-900 text-red-200': status === 'dead',
    }"
  >
    {{ status }}
  </span>
</template>
