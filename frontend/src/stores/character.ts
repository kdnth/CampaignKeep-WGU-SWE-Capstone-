import apiClient from '@/api/axios'
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { CampaignResponse } from './campaign'

export interface CharacterWithCampaignsResponse {
  character: CharacterResponse
  campaignNames: string[]
}

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
  gold: number
  startingEquipmentChosen: boolean
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
  spellIds?: number[]
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

export type CharacterFormBase =
  | CreatePlayableCharacterRequest
  | CreateNonplayableCharacterRequest

export const useCharacterStore = defineStore('character', () => {
  const nonplayableCharacters = ref<CharacterWithCampaignsResponse[]>([])
  const playableCharacters = ref<CharacterWithCampaignsResponse[]>([])
  const activeCharacter = ref<CharacterResponse | null>(null)
  const characterCampaigns = ref<CampaignResponse[]>([])

  async function createPlayableCharacter(request: CreatePlayableCharacterRequest) {
    const response = await apiClient.post<CharacterResponse>(
      `/characters/playable-characters`,
      request,
    )
    playableCharacters.value.push({ character: response.data, campaignNames: [] })
    return response.data
  }

  async function createNonplayableCharacter(request: CreateNonplayableCharacterRequest) {
    const response = await apiClient.post<CharacterResponse>(
      `/characters/nonplayable-characters`,
      request,
    )
    nonplayableCharacters.value.push({ character: response.data, campaignNames: [] })
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

  async function fetchCharacterCampaigns(id: number) {
    const response = await apiClient.get<CampaignResponse[]>(`/characters/${id}/campaigns`)
    characterCampaigns.value = response.data
  }

  async function getCharacterCampaigns(id: number) {
    const response = await apiClient.get<CampaignResponse[]>(`/characters/${id}/campaigns`)
    return response.data
  }

  async function deleteCharacter(id: number) {
    await apiClient.delete(`/characters/${id}`)
    playableCharacters.value = playableCharacters.value.filter((c) => c.character.id !== id)
    nonplayableCharacters.value = nonplayableCharacters.value.filter((c) => c.character.id !== id)
  }

  async function fetchCurrentUserPlayableCharacters() {
    const response = await apiClient.get<CharacterWithCampaignsResponse[]>(
      `/characters/mine/playable`,
    )
    playableCharacters.value = response.data
  }

  async function fetchCurrentUserNonplayableCharacters() {
    const response = await apiClient.get<CharacterWithCampaignsResponse[]>(
      `/characters/mine/nonplayable`,
    )
    nonplayableCharacters.value = response.data
  }

  async function updateCharacterStatus(id: number, status: CharacterResponse['status']) {
    const response = await apiClient.patch<CharacterResponse>(`/characters/${id}/status`, {
      status,
    })
    if (activeCharacter.value?.id === id) {
      activeCharacter.value = response.data
    }
    const updateInList = (list: CharacterWithCampaignsResponse[]) => {
      const index = list.findIndex((c) => c.character.id === id)
      if (index !== -1) list[index]!.character = response.data
    }
    updateInList(playableCharacters.value)
    updateInList(nonplayableCharacters.value)
    return response.data
  }

  return {
    nonplayableCharacters,
    playableCharacters,
    activeCharacter,
    characterCampaigns,
    createPlayableCharacter,
    createNonplayableCharacter,
    fetchCharacter,
    fetchCharacterCampaignView,
    fetchCharacterCampaigns,
    getCharacterCampaigns,
    deleteCharacter,
    fetchCurrentUserPlayableCharacters,
    fetchCurrentUserNonplayableCharacters,
    updateCharacterStatus,
  }
})
