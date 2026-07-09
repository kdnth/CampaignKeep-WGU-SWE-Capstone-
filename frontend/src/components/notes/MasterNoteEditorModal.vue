<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import { ref, watch } from 'vue'

const props = withDefaults(
  defineProps<{
    noteId?: number | null
    initialTitle?: string | null
    initialBody?: string
    isLoading?: boolean
  }>(),
  {
    noteId: null,
    initialTitle: null,
    initialBody: '',
    isLoading: false,
  },
)

const emit = defineEmits<{
  save: [payload: { title: string | null; body: string }]
  cancel: []
}>()

const title = ref(props.initialTitle ?? '')
const body = ref(props.initialBody ?? '')
const errorMessage = ref<string | null>(null)

watch(
  () => [props.initialTitle, props.initialBody] as const,
  ([nextTitle, nextBody]) => {
    title.value = nextTitle ?? ''
    body.value = nextBody ?? ''
  },
)

function handleSave() {
  errorMessage.value = null
  if (body.value.length > 10000) {
    errorMessage.value = 'Note body cannot exceed 10000 characters.'
    return
  }
  emit('save', {
    title: title.value.trim() || null,
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
        <h3 class="text-xl">{{ noteId ? 'Edit Note' : 'New Note' }}</h3>

        <p v-if="errorMessage" class="text-red-500">{{ errorMessage }}</p>

        <div class="flex flex-col gap-2">
          <input
            class="rounded-lg text-2xl font-bold border-0 px-0"
            id="master-note-title"
            v-model="title"
          />
        </div>

        <div class="flex flex-col gap-2">
          <textarea
            id="master-note-body"
            v-model="body"
            rows="14"
            maxlength="10000"
            class="rounded-lg px-0 border-0 border-neutral-300 p-2"
            placeholder="Campaign prep, plot hooks, session notes..."
          />
        </div>

        <div class="flex items-center justify-between">
          <span class="text-sm text-neutral-500">{{ body.length }} / 10000</span>
          <div class="flex gap-2">
            <BaseButton variant="cancel" type="button" @click="emit('cancel')">Cancel</BaseButton>
            <BaseButton variant="primary" type="button" :loading="isLoading" @click="handleSave">
              {{ isLoading ? 'Saving...' : 'Save' }}
            </BaseButton>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>
