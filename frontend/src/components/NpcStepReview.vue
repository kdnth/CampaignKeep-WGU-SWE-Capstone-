<script setup lang="ts">
import { computed } from 'vue'
import { useRaceStore } from '@/stores/race'
import { useSubraceStore } from '@/stores/subrace'
import { useBackgroundStore } from '@/stores/background'
import { useLanguageStore } from '@/stores/language'
import type { CreateNonplayableCharacterRequest } from '@/stores/character'
import BaseButton from '@/components/BaseButton.vue'

const props = defineProps<{
  form: CreateNonplayableCharacterRequest
  submitting: boolean
  error: string | null
}>()
defineEmits(['back', 'submit'])

const raceStore = useRaceStore()
const subraceStore = useSubraceStore()
const backgroundStore = useBackgroundStore()
const languageStore = useLanguageStore()

const raceName = computed(
  () => raceStore.races.find((r) => r.id === props.form.raceId)?.name ?? '-',
)
const subraceName = computed(
  () => subraceStore.subraces.find((s) => s.id === props.form.subraceId)?.name ?? '-',
)
const backgroundName = computed(
  () => backgroundStore.backgrounds.find((b) => b.id === props.form.backgroundId)?.name ?? '-',
)
const languageNames = computed(
  () =>
    languageStore.languages
      .filter((l) => props.form.languageIds?.includes(l.id))
      .map((l) => l.name)
      .join(', ') || '-',
)

const abilities = [
  'strength',
  'dexterity',
  'constitution',
  'intelligence',
  'wisdom',
  'charisma',
] as const
</script>

<template>
  <h2 class="text-4xl text-white my-4">Review</h2>
  <div class="space-y-4 text-white border-2 border-white rounded-2xl p-8">
    <div class="space-y-1">
      <p><span class="text-neutral-300">Name:</span> {{ form.name }}</p>
      <p><span class="text-neutral-300">Race:</span> {{ raceName }}</p>
      <p v-if="form.subraceId"><span class="text-neutral-300">Subrace:</span> {{ subraceName }}</p>
      <p v-if="form.backgroundId">
        <span class="text-neutral-300">Background:</span> {{ backgroundName }}
      </p>
      <p><span class="text-neutral-300">Languages:</span> {{ languageNames }}</p>
    </div>

    <div class="space-y-1">
      <p v-for="ability in abilities" :key="ability" class="capitalize">
        {{ ability }}: {{ form[ability] }}
      </p>
    </div>

    <p v-if="error" class="text-red-400 text-sm">{{ error }}</p>
  </div>
  <div class="flex justify-between pt-4">
    <BaseButton variant="cancel" :disabled="submitting" @click="$emit('back')">Back</BaseButton>
    <BaseButton
      variant="primary"
      :loading="submitting"
      :disabled="submitting"
      @click="$emit('submit')"
    >
      Create NPC
    </BaseButton>
  </div>
</template>
