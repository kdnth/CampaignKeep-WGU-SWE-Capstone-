<script setup lang="ts">
import { reactive } from 'vue'
import BaseButton from '@/components/BaseButton.vue'

const props = defineProps<{ form: Record<string, unknown> }>()
const emit = defineEmits(['update:form', 'next', 'back'])

const abilities = [
  'strength',
  'dexterity',
  'constitution',
  'intelligence',
  'wisdom',
  'charisma',
] as const

const rolled = reactive<Record<string, boolean>>(
  Object.fromEntries(abilities.map((a) => [a, false])),
)

function rollDie() {
  return Math.floor(Math.random() * 6) + 1
}

function roll4d6DropLowest(): number {
  const rolls = [rollDie(), rollDie(), rollDie(), rollDie()]
  rolls.sort((a, b) => a - b)
  rolls.shift() // drops the lowest
  return rolls.reduce((sum, r) => sum + r, 0)
}

function rollAbility(ability: (typeof abilities)[number]) {
  props.form[ability] = roll4d6DropLowest()
  rolled[ability] = true
  emit('update:form', props.form)
}

const allRolled = () => abilities.every((a) => rolled[a])
</script>

<template>
  <h2 class="text-4xl text-fg my-4">Ability Scores</h2>
  <div class="space-y-4 text-fg border-2 border-border-strong rounded-2xl p-8">
    <p class="text-sm text-fg-muted">Roll each ability. Reroll as many times as you like.</p>

    <div v-for="ability in abilities" :key="ability" class="flex items-center justify-between">
      <span class="capitalize w-32">{{ ability }}</span>
      <span class="w-12 text-center text-lg">{{ form[ability] || '-' }}</span>
      <BaseButton variant="secondary" @click="rollAbility(ability)">
        {{ rolled[ability] ? 'Reroll' : 'Roll' }}
      </BaseButton>
    </div>
  </div>
  <div class="flex justify-between pt-4">
    <BaseButton variant="cancel" @click="$emit('back')">Back</BaseButton>
    <BaseButton variant="primary" :disabled="!allRolled()" @click="$emit('next')">Next</BaseButton>
  </div>
</template>
