import { useCharacterStore } from '@/stores/character'
import type { CharacterResponse } from '@/stores/character'
import { useCampaignStore } from '@/stores/campaign'
import type { CharacterStatus } from '@/utils/characterStatus'
import { isAxiosError } from 'axios'
import { type MaybeRefOrGetter, type Ref, ref, toRef, toValue, watch } from 'vue'

export class CharacterStatusUpdateError extends Error {
  constructor(message: string) {
    super(message)
    this.name = 'CharacterStatusUpdateError'
  }
}

export function useCharacterStatusSync(
  character: MaybeRefOrGetter<CharacterResponse>,
  currentHp: Ref<number>,
) {
  const characterStore = useCharacterStore()
  const campaignStore = useCampaignStore()
  const isUpdatingStatus = ref(false)
  const lastError = ref<string | null>(null)

  async function applyStatus(status: CharacterStatus) {
    const current = toValue(character)
    if (current.status === status) return
    if (isUpdatingStatus.value) {
      throw new CharacterStatusUpdateError('A status update is already in progress.')
    }
    isUpdatingStatus.value = true
    lastError.value = null
    try {
      const updated = await characterStore.updateCharacterStatus(current.id, status)
      campaignStore.syncCampaignCharacter(updated)
    } catch (err: unknown) {
      const message = isAxiosError(err)
        ? (err.response?.data?.message ?? err.message)
        : 'Failed to update status'
      lastError.value = message
      throw new CharacterStatusUpdateError(message)
    } finally {
      isUpdatingStatus.value = false
    }
  }

  watch(
    currentHp,
    (hp) => {
      const current = toValue(character)
      if (current.status === 'dead') return
      if (hp === 0 && current.status !== 'down') {
        void applyStatus('down').catch(() => undefined)
      } else if (hp > 0 && current.status === 'down') {
        void applyStatus('alive').catch(() => undefined)
      }
    },
    { immediate: true },
  )

  async function markDead() {
    await applyStatus('dead')
  }

  async function revive() {
    await applyStatus('alive')
  }

  return { markDead, revive, isUpdatingStatus, lastError }
}

export function useCharacterStatusSyncFromProps(
  props: { character: CharacterResponse },
  currentHp: Ref<number>,
) {
  return useCharacterStatusSync(toRef(props, 'character'), currentHp)
}
