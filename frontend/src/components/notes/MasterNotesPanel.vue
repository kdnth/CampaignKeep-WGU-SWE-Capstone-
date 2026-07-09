<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import ConfirmationModal from '@/components/ConfirmationModal.vue'
import MasterNoteCard from '@/components/notes/MasterNoteCard.vue'
import MasterNoteEditorModal from '@/components/notes/MasterNoteEditorModal.vue'
import { useNoteStore } from '@/stores/note'
import { isAxiosError } from 'axios'
import { storeToRefs } from 'pinia'
import { computed, onMounted, ref } from 'vue'

const props = defineProps<{
  campaignId: number
}>()

const noteStore = useNoteStore()
const { masterNotes } = storeToRefs(noteStore)

const isLoading = ref(true)
const isSaving = ref(false)
const isDeleting = ref(false)
const errorMessage = ref<string | null>(null)

const searchQuery = ref('')
const sortOrder = ref<'newest' | 'oldest'>('newest')

const editorOpen = ref(false)
const editingNoteId = ref<number | null>(null)
const editorTitle = ref<string | null>(null)
const editorBody = ref('')
const isLoadingEditor = ref(false)

const pendingDeleteNoteId = ref<number | null>(null)

const filteredNotes = computed(() => {
  let result = masterNotes.value

  if (searchQuery.value.trim() !== '') {
    const query = searchQuery.value.trim().toLowerCase()
    result = result.filter((note) => (note.title ?? '').toLowerCase().includes(query))
  }

  result = [...result].sort((a, b) => {
    const dateA = new Date(a.updatedOn).getTime()
    const dateB = new Date(b.updatedOn).getTime()
    return sortOrder.value === 'newest' ? dateB - dateA : dateA - dateB
  })

  return result
})

async function loadNotes() {
  isLoading.value = true
  errorMessage.value = null
  try {
    await noteStore.fetchMasterNotes(props.campaignId)
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to load notes')
      : 'An unexpected error occurred. Please try again.'
  } finally {
    isLoading.value = false
  }
}

function openCreateModal() {
  editingNoteId.value = null
  editorTitle.value = null
  editorBody.value = ''
  editorOpen.value = true
}

async function openEditModal(noteId: number) {
  editingNoteId.value = noteId
  editorOpen.value = true
  isLoadingEditor.value = true
  errorMessage.value = null
  try {
    const note = await noteStore.fetchMasterNote(props.campaignId, noteId)
    editorTitle.value = note.title
    editorBody.value = note.body
  } catch (err: unknown) {
    editorOpen.value = false
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to load note')
      : 'An unexpected error occurred. Please try again.'
  } finally {
    isLoadingEditor.value = false
  }
}

function closeEditor() {
  editorOpen.value = false
  editingNoteId.value = null
}

async function handleSave(payload: { title: string | null; body: string }) {
  isSaving.value = true
  errorMessage.value = null
  try {
    if (editingNoteId.value) {
      await noteStore.updateMasterNote(props.campaignId, editingNoteId.value, payload)
    } else {
      await noteStore.createMasterNote(props.campaignId, payload)
    }
    closeEditor()
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to save note')
      : 'An unexpected error occurred. Please try again.'
  } finally {
    isSaving.value = false
  }
}

function requestDelete(noteId: number) {
  pendingDeleteNoteId.value = noteId
}

async function confirmDelete() {
  if (!pendingDeleteNoteId.value) return
  isDeleting.value = true
  errorMessage.value = null
  try {
    await noteStore.deleteMasterNote(props.campaignId, pendingDeleteNoteId.value)
    pendingDeleteNoteId.value = null
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to delete note')
      : 'An unexpected error occurred. Please try again.'
  } finally {
    isDeleting.value = false
  }
}

onMounted(loadNotes)
</script>

<template>
  <div class="flex flex-col gap-4">
    <div class="flex flex-wrap items-center gap-4">
      <input
        v-model="searchQuery"
        type="text"
        placeholder="Search by title..."
        class="rounded-lg border-2 border-white bg-neutral-400 p-2 placeholder:text-neutral-700"
      />
      <select
        v-model="sortOrder"
        class="rounded-lg border-2 border-white bg-neutral-400 p-2"
      >
        <option value="newest">Newest first</option>
        <option value="oldest">Oldest first</option>
      </select>
      <BaseButton variant="primary" @click="openCreateModal">New Note</BaseButton>
    </div>

    <p v-if="isLoading" class="text-neutral-400">Loading notes...</p>
    <p v-else-if="errorMessage" class="text-red-500">{{ errorMessage }}</p>
    <p v-else-if="masterNotes.length === 0" class="text-neutral-400">No notes yet.</p>
    <p v-else-if="filteredNotes.length === 0" class="text-neutral-400">
      No notes match your search.
    </p>

    <div v-else class="grid gap-3">
      <div v-for="note in filteredNotes" :key="note.id" class="flex items-stretch gap-2">
        <MasterNoteCard :note="note" class="flex-1" @select="openEditModal(note.id)" />
        <BaseButton variant="danger" @click="requestDelete(note.id)">Delete</BaseButton>
      </div>
    </div>

    <MasterNoteEditorModal
      v-if="editorOpen"
      :note-id="editingNoteId"
      :initial-title="editorTitle"
      :initial-body="editorBody"
      :is-loading="isSaving || isLoadingEditor"
      @save="handleSave"
      @cancel="closeEditor"
    />

    <ConfirmationModal
      v-if="pendingDeleteNoteId"
      title="Delete Note"
      message="Are you sure you want to delete this note? This action cannot be undone."
      variant="danger"
      confirm-label="Delete"
      :is-loading="isDeleting"
      @confirm="confirmDelete"
      @cancel="pendingDeleteNoteId = null"
    />
  </div>
</template>
