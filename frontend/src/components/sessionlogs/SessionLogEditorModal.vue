<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import { ref } from 'vue'

const props = withDefaults(
  defineProps<{
    isLoading?: boolean
  }>(),
  {
    isLoading: false,
  },
)

const emit = defineEmits<{
  save: [payload: { title: string; body: string }]
  cancel: []
}>()

const title = ref('')
const body = ref('')
const errorMessage = ref<string | null>(null)

function handleSave() {
  errorMessage.value = null
  const trimmedTitle = title.value.trim()
  if (!trimmedTitle) {
    errorMessage.value = 'Title is required.'
    return
  }
  if (trimmedTitle.length > 100) {
    errorMessage.value = 'Title cannot exceed 100 characters.'
    return
  }
  if (body.value.length > 10000) {
    errorMessage.value = 'Session log body cannot exceed 10000 characters.'
    return
  }
  emit('save', {
    title: trimmedTitle,
    body: body.value,
  })
}
</script>

<template>
  <Teleport to="body">
    <div
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/70"
      @click.self="emit('cancel')"
    >
      <div
        class="flex max-h-[90vh] w-full max-w-2xl flex-col gap-4 overflow-y-auto rounded-xl bg-white p-6"
      >
        <h3 class="text-xl">New Session Log</h3>

        <p v-if="errorMessage" class="text-red-500">{{ errorMessage }}</p>

        <div class="flex flex-col gap-2">
          <label for="session-log-title" class="text-sm font-medium text-neutral-700">Title</label>
          <input
            id="session-log-title"
            v-model="title"
            maxlength="100"
            class="rounded-lg border border-neutral-300 p-2 text-lg font-bold"
            placeholder="Session 1 — The Goblin Cave"
          />
        </div>

        <div class="flex flex-col gap-2">
          <label for="session-log-body" class="text-sm font-medium text-neutral-700">
            Session notes
          </label>
          <textarea
            id="session-log-body"
            v-model="body"
            rows="14"
            maxlength="10000"
            class="rounded-lg border border-neutral-300 p-2"
            placeholder="What happened this session..."
          />
        </div>

        <div class="flex items-center justify-between">
          <span class="text-sm text-neutral-500">{{ body.length }} / 10000</span>
          <div class="flex gap-2">
            <BaseButton variant="cancel" type="button" @click="emit('cancel')">Cancel</BaseButton>
            <BaseButton variant="primary" type="button" :loading="isLoading" @click="handleSave">
              {{ isLoading ? 'Saving...' : 'Create Log' }}
            </BaseButton>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>
