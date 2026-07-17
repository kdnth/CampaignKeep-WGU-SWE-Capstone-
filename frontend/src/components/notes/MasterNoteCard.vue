<script setup lang="ts">
import { formatDateStr } from '@/utils/dateUtil'
import type { NoteSummaryResponse } from '@/stores/note'

defineProps<{
  note: NoteSummaryResponse
}>()

defineEmits<{
  select: []
}>()
</script>

<template>
  <button
    type="button"
    class="w-full rounded-xl bg-surface p-4 text-left transition-colors hover:border-4 hover:border-accent"
    @click="$emit('select')"
  >
    <h3 class="truncate text-lg font-medium text-fg">
      {{ note.title?.trim() || 'Untitled Note' }}
    </h3>
    <p class="mt-1 text-sm text-fg-muted">
      Updated: {{ formatDateStr(note.updatedOn) }}
    </p>
  </button>
</template>
