import { defineStore } from 'pinia'
import { ref } from 'vue'
import apiClient from '@/api/axios'

export interface NoteSummaryResponse {
  id: number
  title: string | null
  updatedOn: string
}

export interface NoteDetailResponse {
  id: number | null
  title: string | null
  body: string
  createdOn: string | null
  updatedOn: string | null
}

export interface UpsertNoteRequest {
  title?: string | null
  body?: string
}

export const useNoteStore = defineStore('note', () => {
  const playerNote = ref<NoteDetailResponse | null>(null)
  const masterNotes = ref<NoteSummaryResponse[]>([])
  const activeMasterNote = ref<NoteDetailResponse | null>(null)

  function clearNotes() {
    playerNote.value = null
    masterNotes.value = []
    activeMasterNote.value = null
  }

  async function fetchPlayerNote(campaignId: number, characterId: number) {
    const response = await apiClient.get<NoteDetailResponse>(
      `/campaigns/${campaignId}/player-note`,
      { params: { characterId } },
    )
    playerNote.value = response.data
    return response.data
  }

  async function savePlayerNote(
    campaignId: number,
    characterId: number,
    request: UpsertNoteRequest,
  ) {
    const response = await apiClient.put<NoteDetailResponse>(
      `/campaigns/${campaignId}/player-note`,
      request,
      { params: { characterId } },
    )
    playerNote.value = response.data
    return response.data
  }

  async function fetchMasterNotes(campaignId: number) {
    const response = await apiClient.get<NoteSummaryResponse[]>(
      `/campaigns/${campaignId}/master-notes`,
    )
    masterNotes.value = response.data
    return response.data
  }

  async function fetchMasterNote(campaignId: number, noteId: number) {
    const response = await apiClient.get<NoteDetailResponse>(
      `/campaigns/${campaignId}/master-notes/${noteId}`,
    )
    activeMasterNote.value = response.data
    return response.data
  }

  async function createMasterNote(campaignId: number, request: UpsertNoteRequest) {
    const response = await apiClient.post<NoteDetailResponse>(
      `/campaigns/${campaignId}/master-notes`,
      request,
    )
    masterNotes.value.unshift({
      id: response.data.id!,
      title: response.data.title,
      updatedOn: response.data.updatedOn!,
    })
    return response.data
  }

  async function updateMasterNote(
    campaignId: number,
    noteId: number,
    request: UpsertNoteRequest,
  ) {
    const response = await apiClient.put<NoteDetailResponse>(
      `/campaigns/${campaignId}/master-notes/${noteId}`,
      request,
    )
    const index = masterNotes.value.findIndex((n) => n.id === noteId)
    if (index !== -1) {
      masterNotes.value[index] = {
        id: noteId,
        title: response.data.title,
        updatedOn: response.data.updatedOn!,
      }
    }
    activeMasterNote.value = response.data
    return response.data
  }

  async function deleteMasterNote(campaignId: number, noteId: number) {
    await apiClient.delete(`/campaigns/${campaignId}/master-notes/${noteId}`)
    masterNotes.value = masterNotes.value.filter((n) => n.id !== noteId)
    if (activeMasterNote.value?.id === noteId) {
      activeMasterNote.value = null
    }
  }

  return {
    playerNote,
    masterNotes,
    activeMasterNote,
    clearNotes,
    fetchPlayerNote,
    savePlayerNote,
    fetchMasterNotes,
    fetchMasterNote,
    createMasterNote,
    updateMasterNote,
    deleteMasterNote,
  }
})
