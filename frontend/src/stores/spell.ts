import apiClient from '@/api/axios'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export type MagicSchool =
  | 'abjuration'
  | 'conjuration'
  | 'divination'
  | 'enchantment'
  | 'evocation'
  | 'illusion'
  | 'necromancy'
  | 'transmutation'

export type SpellRangeType = 'self' | 'touch' | 'ranged' | 'special' | 'sight' | 'unlimited'

export interface SpellSummaryResponse {
  id: number
  apiIndex: string
  name: string
  level: number
  school: MagicSchool
  castingTime: string
  concentration: boolean
  ritual: boolean
  hasVerbal: boolean
  hasSomatic: boolean
  hasMaterial: boolean
}

export interface SpellDetailResponse extends SpellSummaryResponse {
  duration: string | null
  rangeType: SpellRangeType
  rangeFeet: number | null
  rangeDisplay: string
  materialDesc: string | null
  description: string | null
  higherLevel: string | null
}

export interface SpellListParams {
  search?: string
  level?: number
  school?: MagicSchool
  classIndex?: string
  maxLevel?: number
}

export const useSpellStore = defineStore('spell', () => {
  const spells = ref<SpellSummaryResponse[]>([])
  const characterSpells = ref<SpellDetailResponse[]>([])
  const activeSpell = ref<SpellDetailResponse | null>(null)
  const isSyncing = ref(false)

  function clearSpells() {
    spells.value = []
    characterSpells.value = []
    activeSpell.value = null
  }

  async function fetchSpells(params: SpellListParams = {}) {
    isSyncing.value = true
    try {
      const response = await apiClient.get<SpellSummaryResponse[]>('/spells', { params })
      spells.value = response.data
      return response.data
    } finally {
      isSyncing.value = false
    }
  }

  async function fetchSpell(id: number) {
    const response = await apiClient.get<SpellDetailResponse>(`/spells/${id}`)
    activeSpell.value = response.data
    return response.data
  }

  async function fetchCharacterSpells(characterId: number) {
    const response = await apiClient.get<SpellDetailResponse[]>(`/characters/${characterId}/spells`)
    characterSpells.value = response.data
    return response.data
  }

  async function addSpellToCharacter(characterId: number, spellId: number) {
    const response = await apiClient.post<SpellDetailResponse>(`/characters/${characterId}/spells`, {
      spellId,
    })
    if (!characterSpells.value.some((spell) => spell.id === spellId)) {
      characterSpells.value.push(response.data)
    }
    return response.data
  }

  async function removeSpellFromCharacter(characterId: number, spellId: number) {
    await apiClient.delete(`/characters/${characterId}/spells/${spellId}`)
    characterSpells.value = characterSpells.value.filter((spell) => spell.id !== spellId)
  }

  return {
    spells,
    characterSpells,
    activeSpell,
    isSyncing,
    clearSpells,
    fetchSpells,
    fetchSpell,
    fetchCharacterSpells,
    addSpellToCharacter,
    removeSpellFromCharacter,
  }
})
