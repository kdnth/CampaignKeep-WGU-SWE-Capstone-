<script setup lang="ts">
import CharacterAvatar from '@/components/CharacterAvatar.vue'
import CharacterStatusBadge from '@/components/character/CharacterStatusBadge.vue'
import BaseButton from '@/components/BaseButton.vue'
import { useCurrentHp } from '@/composables/useCurrentHp'
import { useCharacterStatusSyncFromProps } from '@/composables/useCharacterStatusSync'
import type { CharacterResponse } from '@/stores/character'
import { resolveDisplayStatus } from '@/utils/characterStatus'
import { isAxiosError } from 'axios'
import { computed, ref } from 'vue'

const abilities = [
  'strength',
  'dexterity',
  'constitution',
  'intelligence',
  'wisdom',
  'charisma',
] as const

const props = withDefaults(
  defineProps<{
    character: CharacterResponse
    showStats?: boolean
    campaignId?: number
    useLocalHpForStatus?: boolean
    editableHp?: boolean
  }>(),
  { showStats: false, useLocalHpForStatus: false, editableHp: false },
)

const statusError = ref<string | null>(null)

const classLabel = computed(() => {
  if (props.character.characterType === 'NPC') return 'NPC'
  return props.character.classNames[0] ?? 'Not assigned'
})

const hpState =
  props.campaignId !== undefined && (props.editableHp || props.useLocalHpForStatus)
    ? useCurrentHp(props.campaignId, props.character.id, props.character.hitPoints)
    : null

const statusSync = hpState ? useCharacterStatusSyncFromProps(props, hpState.currentHp) : null

const effectiveCurrentHp = computed(() => hpState?.currentHp.value)

const isDead = computed(() => props.character.status === 'dead')

const displayStatus = computed(() =>
  resolveDisplayStatus(props.character.status, effectiveCurrentHp.value),
)

function formatModifier(score: number) {
  const mod = Math.floor((score - 10) / 2)
  return mod >= 0 ? `+${mod}` : `${mod}`
}

function formatInitiative(mod: number) {
  return mod >= 0 ? `+${mod}` : `${mod}`
}

const displayHp = computed(() => {
  if (effectiveCurrentHp.value !== undefined) {
    return `${effectiveCurrentHp.value} / ${props.character.hitPoints}`
  }
  return String(props.character.hitPoints)
})

const isUpdatingStatus = computed(() => statusSync?.isUpdatingStatus.value ?? false)

function handleHpInput(event: Event) {
  if (!hpState || isDead.value) return
  const value = Number((event.target as HTMLInputElement).value)
  if (Number.isFinite(value)) hpState.setCurrentHp(value)
}

async function handleMarkDead() {
  if (!statusSync) return
  statusError.value = null
  try {
    await statusSync.markDead()
  } catch (err: unknown) {
    statusError.value = isAxiosError(err)
      ? (err.response?.data?.message ?? err.message)
      : err instanceof Error
        ? err.message
        : 'Failed to update status'
  }
}

async function handleRevive() {
  if (!statusSync) return
  statusError.value = null
  try {
    await statusSync.revive()
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
  <div class="flex flex-col gap-4 rounded-2xl border-2 border-white bg-neutral-900 p-6 sm:flex-row">
    <div class="flex shrink-0 justify-center sm:justify-start">
      <div class="rounded-xl bg-purple-400 p-4">
        <CharacterAvatar :race-name="character.raceName" :class-name="classLabel" size="lg" />
      </div>
    </div>

    <div class="min-w-0 flex-1 space-y-3 text-white">
      <div class="flex flex-wrap items-start justify-between gap-2">
        <div>
          <h2 class="text-2xl font-semibold">{{ character.name }}</h2>
          <p class="text-sm capitalize text-neutral-300">
            {{ character.raceName }}
            <span v-if="character.subraceName">({{ character.subraceName }})</span>
            - {{ classLabel }} - Lvl. 1
          </p>
        </div>
        <CharacterStatusBadge :status="displayStatus" />
      </div>

      <div class="grid grid-cols-2 gap-2 sm:grid-cols-3 md:grid-cols-6">
        <div v-for="ability in abilities" :key="ability" class="text-center">
          <p class="text-xs uppercase text-neutral-400">{{ ability.slice(0, 3) }}</p>
          <p class="text-lg font-medium">{{ character[ability] }}</p>
          <p class="text-xs text-neutral-500">{{ formatModifier(character[ability]) }}</p>
        </div>
      </div>

      <div
        v-if="showStats || editableHp"
        class="space-y-3 border-t border-neutral-700 pt-3 text-sm"
      >
        <div class="flex flex-wrap gap-4">
          <p><span class="text-neutral-400">AC:</span> {{ character.armorClass }}</p>
          <p v-if="!editableHp">
            <span class="text-neutral-400">HP:</span> {{ displayHp }}
          </p>
          <p><span class="text-neutral-400">Speed:</span> {{ character.speed }} ft.</p>
          <p>
            <span class="text-neutral-400">Initiative:</span>
            {{ formatInitiative(character.initiativeBonus) }}
          </p>
        </div>

        <div v-if="editableHp && hpState" class="flex flex-wrap items-end gap-3">
          <div>
            <label :for="`npc-hp-${character.id}`" class="text-neutral-400">Hit Points</label>
            <div class="flex items-center gap-2">
              <input
                :id="`npc-hp-${character.id}`"
                type="number"
                :min="0"
                :max="character.hitPoints"
                :value="hpState.currentHp.value"
                :disabled="isDead"
                class="block w-24 rounded-lg px-3 py-2 text-black"
                @input="handleHpInput"
              />
              <span class="text-neutral-400">/ {{ character.hitPoints }}</span>
            </div>
          </div>

          <BaseButton
            v-if="!isDead"
            variant="danger"
            :loading="isUpdatingStatus"
            @click="handleMarkDead"
          >
            Mark Dead
          </BaseButton>
          <BaseButton
            v-else
            variant="primary"
            :loading="isUpdatingStatus"
            @click="handleRevive"
          >
            Revive
          </BaseButton>
        </div>

        <p v-if="statusError" class="text-red-400">{{ statusError }}</p>
      </div>
    </div>
  </div>
</template>
