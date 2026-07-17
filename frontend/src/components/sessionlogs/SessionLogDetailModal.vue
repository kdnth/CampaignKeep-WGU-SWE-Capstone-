<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import { formatDateStr } from '@/utils/dateUtil'
import type { SessionLogDetailResponse } from '@/stores/sessionLog'

defineProps<{
  log: SessionLogDetailResponse
}>()

defineEmits<{
  close: []
}>()
</script>

<template>
  <Teleport to="body">
    <div
      class="fixed inset-0 z-50 flex items-center justify-center bg-overlay"
      @click.self="$emit('close')"
    >
      <div
        class="flex max-h-[90vh] w-full max-w-2xl flex-col gap-4 overflow-y-auto rounded-xl bg-surface p-6"
      >
        <div class="flex items-start justify-between gap-4">
          <div>
            <h3 class="text-2xl font-bold text-fg">
              {{ log.title.trim() || 'Untitled Session' }}
            </h3>
            <p class="mt-1 text-sm text-fg-muted">
              Session date: {{ formatDateStr(log.createdOn) }}
            </p>
          </div>
          <BaseButton variant="cancel" type="button" @click="$emit('close')">Close</BaseButton>
        </div>

        <div class="whitespace-pre-wrap text-fg">
          {{ log.body || 'No session notes recorded.' }}
        </div>
      </div>
    </div>
  </Teleport>
</template>
