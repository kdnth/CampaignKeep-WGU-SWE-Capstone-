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
      'bg-success-muted text-success': status === 'alive',
      'bg-warning-muted text-warning': status === 'down',
      'bg-danger-muted text-danger': status === 'dead',
    }"
  >
    {{ status }}
  </span>
</template>
