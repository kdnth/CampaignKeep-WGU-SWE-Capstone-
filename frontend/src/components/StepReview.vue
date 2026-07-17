<script setup lang="ts">
import { computed } from 'vue'
import { useRaceStore } from '@/stores/race'
import { useSubraceStore } from '@/stores/subrace'
import { useDndClassStore } from '@/stores/dndClass'
import { useBackgroundStore } from '@/stores/background'
import { useLanguageStore } from '@/stores/language'
import { useSpellStore } from '@/stores/spell'
import type { CreatePlayableCharacterRequest } from '@/stores/character'
import BaseButton from '@/components/BaseButton.vue'

const props = defineProps<{
  form: CreatePlayableCharacterRequest
  submitting: boolean
  error: string | null
}>()
defineEmits(['back', 'submit'])

const raceStore = useRaceStore()
const subraceStore = useSubraceStore()
const dndClassStore = useDndClassStore()
const backgroundStore = useBackgroundStore()
const languageStore = useLanguageStore()
const spellStore = useSpellStore()

const raceName = computed(
  () => raceStore.races.find((r) => r.id === props.form.raceId)?.name ?? '-',
)
const subraceName = computed(
  () => subraceStore.subraces.find((s) => s.id === props.form.subraceId)?.name ?? '-',
)
const className = computed(
  () => dndClassStore.classes.find((c) => c.id === props.form.classId)?.name ?? '-',
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
const spellNames = computed(() => {
  const ids = props.form.spellIds ?? []
  if (ids.length === 0) return '-'
  return ids
    .map((id) => spellStore.spells.find((s) => s.id === id)?.name ?? `Spell #${id}`)
    .join(', ')
})

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
  <h2 class="text-4xl text-fg my-4">Review</h2>
  <div class="space-y-4 text-fg border-2 border-border-strong rounded-2xl p-8">
    <div class="space-y-1">
      <p><span class="text-fg-muted">Name:</span> {{ form.name }}</p>
      <p><span class="text-fg-muted">Race:</span> {{ raceName }}</p>
      <p v-if="form.subraceId"><span class="text-fg-muted">Subrace:</span> {{ subraceName }}</p>
      <p><span class="text-fg-muted">Class:</span> {{ className }}</p>
      <p v-if="form.backgroundId">
        <span class="text-fg-muted">Background:</span> {{ backgroundName }}
      </p>
      <p><span class="text-fg-muted">Languages:</span> {{ languageNames }}</p>
      <p v-if="(form.spellIds?.length ?? 0) > 0">
        <span class="text-fg-muted">Spells:</span> {{ spellNames }}
      </p>
    </div>

    <div class="space-y-1">
      <p v-for="ability in abilities" :key="ability" class="capitalize">
        {{ ability }}: {{ form[ability] }}
      </p>
    </div>

    <p v-if="error" class="text-danger text-sm">{{ error }}</p>
  </div>
  <div class="flex justify-between pt-4">
    <BaseButton variant="cancel" :disabled="submitting" @click="$emit('back')">Back</BaseButton>
    <BaseButton
      variant="primary"
      :loading="submitting"
      :disabled="submitting"
      @click="$emit('submit')"
    >
      Create Character
    </BaseButton>
  </div>
</template>
