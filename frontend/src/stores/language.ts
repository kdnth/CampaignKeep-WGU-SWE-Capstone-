import apiClient from '@/api/axios'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface LanguageResponse {
  id: number
  name: string
}

export const useLanguageStore = defineStore('language', () => {
  const languages = ref<LanguageResponse[]>([])
  const activeLanguage = ref<LanguageResponse | null>(null)

  async function fetchLanguages() {
    const response = await apiClient.get<LanguageResponse[]>(`/languages`)
    languages.value = response.data
  }

  async function fetchLanguage(id: number) {
    const response = await apiClient.get<LanguageResponse>(`/languages/${id}`)
    activeLanguage.value = response.data
  }

  return {
    languages,
    activeLanguage,
    fetchLanguages,
    fetchLanguage,
  }
})