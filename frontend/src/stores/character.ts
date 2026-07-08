import apiClient from '@/api/axios'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface CharacterResponse {
  id: number
  characterType: 'PC' | 'NPC'
  ownerId: number
  name: string
  raceId: number
  raceName: string
  subraceId: number | null
  subraceName: string | null
  backgroundId: number | null
  backgroundName: string | null
  status: 'alive' | 'down' | 'dead'
  strength: number
  dexterity: number
  constitution: number
  intelligence: number
  wisdom: number
  charisma: number
  hitPoints: number
  armorClass: number
  initiativeBonus: number
  speed: number
  classNames: string[]
  languageNames: string[]
  createdOn: string
  updatedOn: string
}

export interface CreatePlayableCharacterRequest {
  name: string
  raceId: number
  subraceId: number | null
  backgroundId: number | null
  classId: number
  languageIds: number[] | null
  strength: number
  dexterity: number
  constitution: number
  intelligence: number
  wisdom: number
  charisma: number
  hitPoints: number
  armorClass: number
}

export interface CreateNonplayableCharacterRequest {
  name: string
  raceId: number
  subraceId: number | null
  backgroundId: number | null
  languageIds: number[] | null
  strength: number
  dexterity: number
  constitution: number
  intelligence: number
  wisdom: number
  charisma: number
  hitPoints: number
  armorClass: number
}

export const useCharacterStore = defineStore('character', () => {
  const nonplayableCharacters = ref<CharacterResponse[]>([])
  const playableCharacters = ref<CharacterResponse[]>([])
  const activeCharacter = ref<CharacterResponse | null>(null)

  async function createPlayableCharacter(request: CreatePlayableCharacterRequest) {
    const response = await apiClient.post<CharacterResponse>(
      `/characters/playable-characters`,
      request,
    )
    playableCharacters.value.push(response.data)
    return response.data
  }

  async function createNonplayableCharacter(request: CreateNonplayableCharacterRequest) {
    const response = await apiClient.post<CharacterResponse>(
      `/characters/nonplayable-characters`,
      request,
    )
    nonplayableCharacters.value.push(response.data)
    return response.data
  }

  async function fetchCharacter(id: number) {
    const response = await apiClient.get<CharacterResponse>(`/characters/${id}`)
    activeCharacter.value = response.data
  }

  async function fetchCharacterCampaignView(id: number, campaignId: number) {
    const response = await apiClient.get<CharacterResponse>(
      `/characters/${id}/campaign-view?campaignId=${campaignId}`,
    )
    activeCharacter.value = response.data
  }

  async function deleteCharacter(id: number) {
    await apiClient.delete(`/characters/${id}`)
    playableCharacters.value = playableCharacters.value.filter((c) => c.id !== id)
    nonplayableCharacters.value = nonplayableCharacters.value.filter((c) => c.id !== id)
  }

  async function fetchCurrentUserPlayableCharacters() {
    const response = await apiClient.get<CharacterResponse[]>(`/characters/mine/playable`)
    playableCharacters.value = response.data
  }

  async function fetchCurrentUserNonplayableCharacters() {
    const response = await apiClient.get<CharacterResponse[]>(`/characters/mine/nonplayable`)
    nonplayableCharacters.value = response.data
  }

  return {
    nonplayableCharacters,
    playableCharacters,
    activeCharacter,
    createPlayableCharacter,
    createNonplayableCharacter,
    fetchCharacter,
    fetchCharacterCampaignView,
    deleteCharacter,
    fetchCurrentUserPlayableCharacters,
    fetchCurrentUserNonplayableCharacters,
  }
})
