import apiClient from '@/api/axios'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface RaceSummaryResponse {
  id: number
  name: string
  size: string
  speed: number
  index: string
}

export interface RaceAbilityPointBonusResponse {
  abilityId: number
  abilityName: string
  value: number
}

export interface RaceDetailResponse {
  id: number
  name: string
  ageDesc: string
  alignmentDesc: string
  languageDesc: string
  size: string
  sizeDesc: string
  speed: number
  index: string
  abilityPointBonuses: RaceAbilityPointBonusResponse[]
}

export const useRaceStore = defineStore('race', () => {
  const races = ref<RaceSummaryResponse[]>([])
  const activeRace = ref<RaceDetailResponse | null>(null)

  async function fetchRaces() {
    const response = await apiClient.get<RaceSummaryResponse[]>(`/races`)
    races.value = response.data
  }

  async function fetchRace(raceId: number) {
    const response = await apiClient.get<RaceDetailResponse>(`/races/${raceId}`)
    activeRace.value = response.data
  }

  return {
    races,
    activeRace,
    fetchRaces,
    fetchRace,
  }
})