import apiClient from '@/api/axios'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface DndClassResponse {
  id: number
  name: string
  hitDice: number
  description: string
  index: string
}

export const useDndClassStore = defineStore('dndClass', () => {
  const classes = ref<DndClassResponse[]>([])
  const activeClass = ref<DndClassResponse | null>(null)

  async function fetchClasses() {
    const response = await apiClient.get<DndClassResponse[]>(`/classes`)
    classes.value = response.data
  }

  async function fetchClass(id: number) {
    const response = await apiClient.get<DndClassResponse>(`/classes/${id}`)
    activeClass.value = response.data
  }

  return {
    classes,
    activeClass,
    fetchClasses,
    fetchClass,
  }
})