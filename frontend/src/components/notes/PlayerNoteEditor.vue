<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import { useNoteStore } from '@/stores/note'
import { isAxiosError } from 'axios'
import { storeToRefs } from 'pinia'
import { computed, onMounted, ref, watch } from 'vue'

const props = defineProps<{
  campaignId: number
  characterId: number
  characterName: string
}>()

const noteStore = useNoteStore()
const { playerNote } = storeToRefs(noteStore)

const body = ref('')
const isLoading = ref(true)
const isSaving = ref(false)
const errorMessage = ref<string | null>(null)
const saveStatus = ref<'idle' | 'saved'>('idle')

const panelTitle = computed(() => `${props.characterName}'s Notes`)

function formatError(err: unknown, fallback: string): string {
  if (isAxiosError(err)) {
    if (!err.response) {
      return 'Unable to reach the server.'
    }
    return err.response.data?.message ?? fallback
  }
  return 'An unexpected error occurred. Please try again.'
}

async function loadNote() {
  isLoading.value = true
  errorMessage.value = null
  try {
    const note = await noteStore.fetchPlayerNote(props.campaignId, props.characterId)
    body.value = note.body ?? ''
  } catch (err: unknown) {
    errorMessage.value = formatError(err, 'Failed to load note')
  } finally {
    isLoading.value = false
  }
}

async function handleSave() {
  isSaving.value = true
  errorMessage.value = null
  saveStatus.value = 'idle'
  try {
    await noteStore.savePlayerNote(props.campaignId, props.characterId, { body: body.value })
    saveStatus.value = 'saved'
  } catch (err: unknown) {
    errorMessage.value = formatError(err, 'Failed to save note')
  } finally {
    isSaving.value = false
  }
}

watch(playerNote, (note) => {
  if (!note) return
  body.value = note.body ?? ''
})

onMounted(loadNote)
</script>

<template>
  <div
    class="flex min-h-[calc(100vh-18rem)] flex-col rounded-2xl bg-white p-6 text-black shadow-sm"
  >
    <div class="mb-4 flex items-center justify-between gap-4 border-b border-neutral-200 pb-4">
      <h2 class="text-xl font-semibold">{{ panelTitle }}</h2>
      <span v-if="saveStatus === 'saved'" class="text-sm text-green-700">Saved</span>
    </div>

    <p v-if="isLoading" class="text-neutral-500">Loading note...</p>

    <template v-else>
      <p v-if="errorMessage" class="mb-4 text-red-600">{{ errorMessage }}</p>

      <textarea
        v-model="body"
        maxlength="10000"
        class="min-h-[calc(100vh-26rem)] w-full flex-1 resize-none bg-white text-base leading-relaxed text-black placeholder:text-neutral-400 focus:outline-none border-0 focus:border-0"
        placeholder="Write your character notes here..."
      />

      <div class="mt-4 flex items-center justify-between border-t border-neutral-200 pt-4">
        <span class="text-sm text-neutral-500">{{ body.length }} / 10000</span>
        <BaseButton variant="primary" :loading="isSaving" @click="handleSave">Save</BaseButton>
      </div>
    </template>
  </div>
</template>
