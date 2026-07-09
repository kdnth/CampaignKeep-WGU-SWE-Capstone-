import apiClient from '@/api/axios'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface SubraceSummaryResponse {
  id: number
  raceId: number
  raceName: string
  name: string
}

export interface SubraceAbilityPointBonusResponse {
  abilityId: number
  abilityName: string
  value: number
}

export interface SubraceDetailResponse {
  id: number
  raceId: number
  raceName: string
  name: string
  description: string
  abilityPointBonuses: SubraceAbilityPointBonusResponse[]
}

export const useSubraceStore = defineStore('subrace', () => {
  const subraces = ref<SubraceSummaryResponse[]>([])
  const activeSubrace = ref<SubraceDetailResponse | null>(null)

  async function fetchSubraces() {
    const response = await apiClient.get<SubraceSummaryResponse[]>(`/subraces`)
    subraces.value = response.data
  }

  async function fetchSubrace(subraceId: number) {
    const response = await apiClient.get<SubraceDetailResponse>(`/subraces/${subraceId}`)
    activeSubrace.value = response.data
  }

  return {
    subraces,
    activeSubrace,
    fetchSubraces,
    fetchSubrace,
  }
})
