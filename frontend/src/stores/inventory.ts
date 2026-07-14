import apiClient from '@/api/axios'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export type ItemType = 'weapon' | 'armor' | 'tool' | 'gear' | 'jewelry' | 'clothing'
export type ItemRarity =
  | 'common'
  | 'uncommon'
  | 'rare'
  | 'very_rare'
  | 'legendary'
  | 'artifact'
export type EquipmentSlot = 'armor' | 'shield' | 'main_hand' | 'off_hand'
export type DamageType =
  | 'slashing'
  | 'piercing'
  | 'bludgeoning'
  | 'cold'
  | 'acid'
  | 'fire'
  | 'force'
  | 'lightning'
  | 'necrotic'
  | 'poison'
  | 'psychic'
  | 'radiant'
  | 'thunder'
export type WeaponRange = 'melee' | 'ranged'

export interface ItemDetailResponse {
  id: number
  itemType: ItemType
  name: string
  description: string | null
  value: number | null
  effectiveValue: number
  weight: number | null
  rarity: ItemRarity
  isMagical: boolean
  apiIndex: string | null
  campaignId: number | null
  weaponCategoryName: string | null
  damage: string | null
  damageType: DamageType | null
  range: number | null
  weaponRange: WeaponRange | null
  armorCategoryName: string | null
  baseAc: number | null
  maxDexBonus: number | null
  strMinimum: number | null
}

export interface InventoryItemResponse {
  inventoryItemId: number
  item: ItemDetailResponse
  quantity: number
  equippedSlot: EquipmentSlot | null
}

export interface CharacterInventoryResponse {
  items: InventoryItemResponse[]
  totalWeight: number
  carryingCapacity: number
  gold: number
  startingEquipmentChosen: boolean
  armorClass: number
}

export interface CharacterAttackResponse {
  inventoryItemId: number
  weaponName: string
  attackBonus: number
  damageDice: string
  damageBonus: number
  damageType: DamageType
  weaponRange: WeaponRange
  range: number | null
  equippedSlot: EquipmentSlot
}

export interface StartingEquipmentOptionsResponse {
  optionsByGroup: Record<string, ItemDetailResponse[]>
}

export interface SubmitStartingEquipmentRequest {
  weaponItemId: number
  armorItemId?: number | null
  shieldItemId?: number | null
}

export interface PackContentResponse {
  apiIndex: string
  name: string
  quantity: number
}

export interface EquipmentPackResponse {
  index: string
  name: string
  costGp: number
  contents: PackContentResponse[]
}

export interface CreateCampaignItemRequest {
  itemType: ItemType
  name: string
  description?: string
  value?: number
  weight: number
  rarity?: ItemRarity
  isMagical?: boolean
  weaponCategoryId?: number
  damage?: string
  damageType?: DamageType
  range?: number
  weaponRange?: WeaponRange
  armorCategoryId?: number
  baseAc?: number
  maxDexBonus?: number
  strMinimum?: number
}

export function formatRarityLabel(rarity: ItemRarity): string {
  return rarity
    .split('_')
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(' ')
}

export const useInventoryStore = defineStore('inventory', () => {
  const inventory = ref<CharacterInventoryResponse | null>(null)
  const attacks = ref<CharacterAttackResponse[]>([])
  const startingOptions = ref<StartingEquipmentOptionsResponse | null>(null)
  const campaignItems = ref<ItemDetailResponse[]>([])
  const equipmentPacks = ref<EquipmentPackResponse[]>([])

  async function fetchInventory(characterId: number) {
    const response = await apiClient.get<CharacterInventoryResponse>(
      `/characters/${characterId}/inventory`,
    )
    inventory.value = response.data
    return response.data
  }

  async function submitStartingEquipment(
    characterId: number,
    request: SubmitStartingEquipmentRequest,
  ) {
    const response = await apiClient.post<CharacterInventoryResponse>(
      `/characters/${characterId}/starting-equipment`,
      request,
    )
    inventory.value = response.data
    return response.data
  }

  async function submitStartingPack(characterId: number, packIndex: string) {
    const response = await apiClient.post<CharacterInventoryResponse>(
      `/characters/${characterId}/starting-pack`,
      { packIndex },
    )
    inventory.value = response.data
    return response.data
  }

  async function equipItem(characterId: number, inventoryItemId: number, slot: EquipmentSlot) {
    const response = await apiClient.patch<CharacterInventoryResponse>(
      `/characters/${characterId}/inventory/${inventoryItemId}`,
      { equipToSlot: slot },
    )
    inventory.value = response.data
    return response.data
  }

  async function unequipItem(characterId: number, inventoryItemId: number) {
    const response = await apiClient.patch<CharacterInventoryResponse>(
      `/characters/${characterId}/inventory/${inventoryItemId}`,
      { unequip: true },
    )
    inventory.value = response.data
    return response.data
  }

  async function updateItemQuantity(
    characterId: number,
    inventoryItemId: number,
    quantity: number,
  ) {
    const response = await apiClient.patch<CharacterInventoryResponse>(
      `/characters/${characterId}/inventory/${inventoryItemId}`,
      { quantity },
    )
    inventory.value = response.data
    return response.data
  }

  async function removeItem(characterId: number, inventoryItemId: number) {
    await apiClient.delete(`/characters/${characterId}/inventory/${inventoryItemId}`)
    if (inventory.value) {
      inventory.value.items = inventory.value.items.filter(
        (i) => i.inventoryItemId !== inventoryItemId,
      )
    }
  }

  async function updateGold(characterId: number, gold: number) {
    const response = await apiClient.patch<CharacterInventoryResponse>(
      `/characters/${characterId}/gold`,
      { gold },
    )
    inventory.value = response.data
    return response.data
  }

  async function fetchAttacks(characterId: number) {
    const response = await apiClient.get<CharacterAttackResponse[]>(
      `/characters/${characterId}/attacks`,
    )
    attacks.value = response.data
    return response.data
  }

  async function fetchStartingOptions(classId: number) {
    const response = await apiClient.get<StartingEquipmentOptionsResponse>(
      `/classes/${classId}/starting-equipment-options`,
    )
    startingOptions.value = response.data
    return response.data
  }

  async function fetchCampaignItems(campaignId: number) {
    const response = await apiClient.get<ItemDetailResponse[]>(`/campaigns/${campaignId}/items`)
    campaignItems.value = response.data
    return response.data
  }

  async function createCampaignItem(campaignId: number, request: CreateCampaignItemRequest) {
    const response = await apiClient.post<ItemDetailResponse>(
      `/campaigns/${campaignId}/items`,
      request,
    )
    campaignItems.value.push(response.data)
    return response.data
  }

  async function grantItem(
    campaignId: number,
    characterId: number,
    itemId: number,
    quantity: number,
    characterType: 'PC' | 'NPC' = 'PC',
  ) {
    const path =
      characterType === 'NPC'
        ? `/campaigns/${campaignId}/nonplayable-characters/${characterId}/items`
        : `/campaigns/${campaignId}/playable-characters/${characterId}/items`
    const response = await apiClient.post<CharacterInventoryResponse>(path, { itemId, quantity })
    return response.data
  }

  async function fetchEquipmentPacks() {
    const response = await apiClient.get<EquipmentPackResponse[]>('/equipment-packs')
    equipmentPacks.value = response.data
    return response.data
  }

  function clearInventory() {
    inventory.value = null
    attacks.value = []
    startingOptions.value = null
    campaignItems.value = []
    equipmentPacks.value = []
  }

  return {
    inventory,
    attacks,
    startingOptions,
    campaignItems,
    equipmentPacks,
    fetchInventory,
    submitStartingEquipment,
    submitStartingPack,
    equipItem,
    unequipItem,
    updateItemQuantity,
    removeItem,
    updateGold,
    fetchAttacks,
    fetchStartingOptions,
    fetchCampaignItems,
    createCampaignItem,
    grantItem,
    fetchEquipmentPacks,
    clearInventory,
  }
})
