<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import SpellBrowser, { type SpellQuotas } from '@/components/spells/SpellBrowser.vue'
import type { CreatePlayableCharacterRequest } from '@/stores/character'
import { useDndClassStore } from '@/stores/dndClass'
import { useSpellStore } from '@/stores/spell'
import { computed } from 'vue'

const HIGH_ELF_SUBRACE_ID = 1

const props = defineProps<{
  form: CreatePlayableCharacterRequest
}>()

defineEmits(['next', 'back'])

const dndClassStore = useDndClassStore()
const spellStore = useSpellStore()

const selectedClass = computed(() =>
  dndClassStore.classes.find((c) => c.id === props.form.classId),
)

const isWizard = computed(() => selectedClass.value?.index === 'wizard')
const isHighElf = computed(() => props.form.subraceId === HIGH_ELF_SUBRACE_ID)

const quotas = computed<SpellQuotas | undefined>(() => {
  if (isWizard.value) {
    return {
      cantrips: isHighElf.value ? 4 : 3,
      levelOne: 6,
    }
  }
  if (isHighElf.value) {
    return { cantrips: 1, levelOne: 0 }
  }
  return undefined
})

const cantripCount = computed(() => countSpellsByLevel(0))
const levelOneCount = computed(() => countSpellsByLevel(1))

const canContinue = computed(() => {
  if (!quotas.value) return true
  const ids = props.form.spellIds ?? []
  if (isWizard.value) {
    return (
      cantripCount.value === quotas.value.cantrips &&
      levelOneCount.value === quotas.value.levelOne &&
      ids.length === quotas.value.cantrips + quotas.value.levelOne
    )
  }
  return ids.length === 1 && cantripCount.value === 1 && levelOneCount.value === 0
})

function countSpellsByLevel(level: number): number {
  const ids = props.form.spellIds ?? []
  return ids.filter((id) => spellStore.spells.find((s) => s.id === id)?.level === level).length
}

function updateSpellIds(ids: number[]) {
  props.form.spellIds = ids
}
</script>

<template>
  <h2 class="my-4 text-4xl text-white">Choose Spells</h2>
  <p class="mb-4 text-neutral-300">
    <template v-if="isWizard">
      Select {{ quotas?.cantrips }} cantrips and {{ quotas?.levelOne }} level-1 spells for your
      spellbook.
    </template>
    <template v-else-if="isHighElf">
      Select 1 wizard cantrip for your High Elf racial trait.
    </template>
  </p>

  <SpellBrowser
    mode="select"
    class-index="wizard"
    :max-level="isWizard ? 1 : 0"
    :selected-ids="form.spellIds ?? []"
    :quotas="quotas"
    @update:selected-ids="updateSpellIds"
  />

  <div class="flex justify-between pt-6">
    <BaseButton variant="cancel" @click="$emit('back')">Back</BaseButton>
    <BaseButton variant="primary" :disabled="!canContinue" @click="$emit('next')">
      Next
    </BaseButton>
  </div>
</template>
