<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import { useCurrentHp } from '@/composables/useCurrentHp'
import { useCharacterStatusSyncFromProps } from '@/composables/useCharacterStatusSync'
import type { CharacterResponse } from '@/stores/character'
import { isAxiosError } from 'axios'
import { ref, computed } from 'vue'

const props = defineProps<{
  character: CharacterResponse
  campaignId: number
}>()

const statusError = ref<string | null>(null)

const { currentHp, maxHp, takeDamage, heal, rest } = useCurrentHp(
  props.campaignId,
  props.character.id,
  props.character.hitPoints,
)

const { markDead, revive, isUpdatingStatus } = useCharacterStatusSyncFromProps(
  props,
  currentHp,
)

const adjustAmount = ref(1)

const isDead = computed(() => props.character.status === 'dead')

function formatInitiative(mod: number) {
  return mod >= 0 ? `+${mod}` : `${mod}`
}

async function handleMarkDead() {
  statusError.value = null
  try {
    await markDead()
  } catch (err: unknown) {
    statusError.value = isAxiosError(err)
      ? (err.response?.data?.message ?? err.message)
      : err instanceof Error
        ? err.message
        : 'Failed to update status'
  }
}

async function handleRevive() {
  statusError.value = null
  try {
    await revive()
  } catch (err: unknown) {
    statusError.value = isAxiosError(err)
      ? (err.response?.data?.message ?? err.message)
      : err instanceof Error
        ? err.message
        : 'Failed to update status'
  }
}
</script>

<template>
  <div class="space-y-6 rounded-2xl border-2 border-border-strong p-6 text-fg">
    <div class="grid gap-4 sm:grid-cols-3">
      <div>
        <p class="text-sm text-fg-subtle">Armor Class</p>
        <p class="text-2xl font-semibold">{{ character.armorClass }}</p>
      </div>
      <div>
        <p class="text-sm text-fg-subtle">Speed</p>
        <p class="text-2xl font-semibold">{{ character.speed }} ft.</p>
      </div>
      <div>
        <p class="text-sm text-fg-subtle">Initiative</p>
        <p class="text-2xl font-semibold">{{ formatInitiative(character.initiativeBonus) }}</p>
      </div>
    </div>

    <div class="border-t border-border pt-6">
      <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
        <div>
          <p class="mb-2 text-sm text-fg-subtle">Hit Points</p>
          <div class="flex items-baseline gap-4">
            <p class="text-3xl font-semibold">{{ currentHp }} / {{ maxHp }}</p>
            <BaseButton variant="primary" :disabled="isDead" @click="rest">Rest</BaseButton>
          </div>
        </div>

        <div class="flex flex-wrap items-end gap-3">
          <div>
            <label :for="`adjust-amount-${character.id}`" class="text-sm text-fg-subtle">
              Amount
            </label>
            <input
              :id="`adjust-amount-${character.id}`"
              v-model.number="adjustAmount"
              type="number"
              min="1"
              :disabled="isDead"
              class="bg-input block w-20 rounded-lg px-3 py-2 text-fg"
            />
          </div>
          <BaseButton
            variant="cancel"
            :disabled="currentHp === 0 || isDead"
            @click="takeDamage(adjustAmount)"
          >
            Take Damage
          </BaseButton>
          <BaseButton
            variant="agree"
            :disabled="currentHp === maxHp || isDead"
            @click="heal(adjustAmount)"
          >
            Heal
          </BaseButton>
        </div>
      </div>

      <div class="mt-4 flex flex-wrap gap-2">
        <BaseButton
          v-if="!isDead"
          variant="danger"
          :loading="isUpdatingStatus"
          @click="handleMarkDead"
        >
          Mark Dead
        </BaseButton>
        <BaseButton v-else variant="primary" :loading="isUpdatingStatus" @click="handleRevive">
          Revive
        </BaseButton>
      </div>

      <p v-if="statusError" class="mt-2 text-sm text-danger">{{ statusError }}</p>

      <p class="mt-2 text-xs text-fg-subtle">
        Max HP is set by your character sheet. Current HP is saved locally for this campaign.
      </p>
    </div>
  </div>
</template>
