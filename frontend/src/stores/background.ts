import apiClient from '@/api/axios'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface BackgroundResponse {
  id: number
  name: string
  description: string
}

export interface CreateBackgroundRequest {
  name: string
  description: string
}

export const useBackgroundStore = defineStore('background', () => {
  const backgrounds = ref<BackgroundResponse[]>([])
  const activeBackground = ref<BackgroundResponse | null>(null)

  async function fetchBackgrounds() {
    const response = await apiClient.get<BackgroundResponse[]>(`/backgrounds`)
    backgrounds.value = response.data
  }

  async function fetchBackground(backgroundId: number) {
    const response = await apiClient.get<BackgroundResponse>(`/backgrounds/${backgroundId}`)
    activeBackground.value = response.data
  }

  async function createBackground(request: CreateBackgroundRequest) {
    const response = await apiClient.post<BackgroundResponse>(`/backgrounds`, request)
    backgrounds.value.push(response.data)
    return response.data
  }

  return {
    backgrounds,
    activeBackground,
    fetchBackgrounds,
    fetchBackground,
    createBackground,
  }
})