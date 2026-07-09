import { computed, ref, type Ref } from 'vue'
import { readStoredHp } from '@/utils/characterStatus'

const hpStore = new Map<string, Ref<number>>()

function storageKey(campaignId: number, characterId: number) {
  return `campaignkeep:currentHp:${campaignId}:${characterId}`
}

function writeStoredHp(campaignId: number, characterId: number, value: number) {
  localStorage.setItem(storageKey(campaignId, characterId), String(value))
}

function getHpRef(campaignId: number, characterId: number, maxHp: number): Ref<number> {
  const key = `${campaignId}:${characterId}`
  if (!hpStore.has(key)) {
    hpStore.set(key, ref(readStoredHp(campaignId, characterId, maxHp)))
  }
  return hpStore.get(key)!
}

export function useCurrentHp(campaignId: number, characterId: number, maxHp: number) {
  const currentHp = getHpRef(campaignId, characterId, maxHp)

  function persist(value: number) {
    const clamped = Math.min(Math.max(0, value), maxHp)
    currentHp.value = clamped
    writeStoredHp(campaignId, characterId, clamped)
  }

  function setCurrentHp(value: number) {
    persist(value)
  }

  function takeDamage(amount: number) {
    persist(currentHp.value - amount)
  }

  function heal(amount: number) {
    persist(currentHp.value + amount)
  }

  function rest() {
    persist(maxHp)
  }

  const maxHpRef = computed(() => maxHp)

  return {
    currentHp,
    maxHp: maxHpRef,
    setCurrentHp,
    takeDamage,
    heal,
    rest,
  }
}
