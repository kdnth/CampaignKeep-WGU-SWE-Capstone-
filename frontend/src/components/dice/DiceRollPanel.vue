<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import DiceFace from '@/components/dice/DiceFace.vue'
import type { CharacterResponse } from '@/stores/character'
import {
  DICE_TYPES,
  SKILL_OPTIONS,
  abilityModifier,
  formatModifier,
  performRoll,
  randomFaceForAnimation,
  type AbilityKey,
  type DiceRollOutcome,
  type DiceType,
  type RollMode,
} from '@/utils/diceRoller'
import { computed, onUnmounted, ref } from 'vue'

const props = defineProps<{
  mode: 'player' | 'master'
  character?: CharacterResponse | null
}>()

const diceType = ref<DiceType>('d20')
const quantity = ref(1)
const rollMode = ref<RollMode>('normal')
const selectedSkill = ref<string>('')
const customModifier = ref(0)

const isRolling = ref(false)
const displayFaces = ref<number[]>([])
const outcome = ref<DiceRollOutcome | null>(null)

let animationTimers: ReturnType<typeof setTimeout>[] = []

const isD20 = computed(() => diceType.value === 'd20')

const selectedSkillOption = computed(
  () => SKILL_OPTIONS.find((s) => s.name === selectedSkill.value) ?? null,
)

const skillModifier = computed(() => {
  if (props.mode !== 'player' || !props.character || !selectedSkillOption.value) return 0
  const ability = selectedSkillOption.value.ability
  return abilityModifier(props.character[ability])
})

const effectiveModifier = computed(() => {
  if (props.mode === 'master') return customModifier.value
  return skillModifier.value
})

const quantityDisabled = computed(
  () => isRolling.value || (isD20.value && rollMode.value !== 'normal'),
)

function clearTimers() {
  for (const t of animationTimers) clearTimeout(t)
  animationTimers = []
}

onUnmounted(clearTimers)

function onDiceTypeChange() {
  if (diceType.value !== 'd20') {
    rollMode.value = 'normal'
  }
}

function abilityLabel(ability: AbilityKey): string {
  return ability.slice(0, 3).toUpperCase()
}

async function handleRoll() {
  if (isRolling.value) return
  clearTimers()
  outcome.value = null
  isRolling.value = true

  const result = performRoll({
    diceType: diceType.value,
    quantity: quantity.value,
    mode: rollMode.value,
    modifier: effectiveModifier.value,
    skillName: selectedSkillOption.value?.name ?? null,
    abilityKey: selectedSkillOption.value?.ability ?? null,
  })

  const dieCount = result.dice.length
  displayFaces.value = Array.from({ length: dieCount }, () =>
    randomFaceForAnimation(diceType.value),
  )

  const durationMs = 1400
  const tickMs = 70
  const start = Date.now()

  await new Promise<void>((resolve) => {
    const tick = () => {
      const elapsed = Date.now() - start
      if (elapsed >= durationMs) {
        displayFaces.value = result.dice.map((d) => d.face)
        resolve()
        return
      }

      const progress = elapsed / durationMs
      displayFaces.value = Array.from({ length: dieCount }, () =>
        randomFaceForAnimation(diceType.value),
      )
      const nextDelay = tickMs + Math.floor(progress * 90)
      animationTimers.push(setTimeout(tick, nextDelay))
    }
    animationTimers.push(setTimeout(tick, tickMs))
  })

  outcome.value = result
  isRolling.value = false
}
</script>
``
<template>
  <div class="space-y-6 rounded-2xl border-2 border-white p-6 text-white">
    <div>
      <h2 class="text-xl font-semibold">Dice Roller</h2>
      <p class="text-sm text-neutral-400">
        <template v-if="mode === 'player'">
          Optionally pick a skill. Only the linked ability modifier is added for now.
        </template>
        <template v-else> Add a custom modifier for checks, saves, or contested rolls. </template>
      </p>
    </div>

    <div class="flex flex-wrap items-end gap-4">
      <div>
        <label class="text-sm text-neutral-400" for="dice-type">Dice</label>
        <select
          id="dice-type"
          v-model="diceType"
          class="mt-1 block rounded-lg py-2 text-black"
          :disabled="isRolling"
          @change="onDiceTypeChange"
        >
          <option v-for="type in DICE_TYPES" :key="type" :value="type">{{ type }}</option>
        </select>
      </div>

      <div>
        <label class="text-sm text-neutral-400" for="dice-qty">Quantity</label>
        <input
          id="dice-qty"
          v-model.number="quantity"
          type="number"
          min="1"
          max="10"
          class="mt-1 block w-20 rounded-lg px-3 py-2 text-black"
          :disabled="quantityDisabled"
        />
      </div>

      <div>
        <p class="text-sm text-neutral-400">Advantage</p>
        <div class="mt-1 flex flex-wrap gap-2">
          <BaseButton
            variant="secondary"
            :disabled="!isD20 || isRolling"
            :class="rollMode === 'normal' ? 'ring-4 ring-purple-400' : ''"
            @click="rollMode = 'normal'"
          >
            Normal
          </BaseButton>
          <BaseButton
            variant="secondary"
            :disabled="!isD20 || isRolling"
            :class="rollMode === 'advantage' ? 'ring-4 ring-green-400' : ''"
            @click="rollMode = 'advantage'"
          >
            Advantage
          </BaseButton>
          <BaseButton
            variant="secondary"
            :disabled="!isD20 || isRolling"
            :class="rollMode === 'disadvantage' ? 'ring-4 ring-red-400' : ''"
            @click="rollMode = 'disadvantage'"
          >
            Disadvantage
          </BaseButton>
        </div>
        <p v-if="!isD20" class="mt-1 text-xs text-neutral-500">
          Advantage / disadvantage is only available on d20 rolls.
        </p>
      </div>
    </div>

    <div v-if="mode === 'player'" class="max-w-xs">
      <label class="text-sm text-neutral-400" for="skill-select">Skill (optional)</label>
      <select
        id="skill-select"
        v-model="selectedSkill"
        class="mt-1 block w-full rounded-lg px-4 py-2 text-black"
        :disabled="isRolling || !character"
      >
        <option value="">None</option>
        <option v-for="skill in SKILL_OPTIONS" :key="skill.name" :value="skill.name">
          {{ skill.name }} ({{ abilityLabel(skill.ability) }})
        </option>
      </select>
      <p v-if="selectedSkillOption && character" class="mt-1 text-xs text-neutral-400">
        {{ selectedSkillOption.name }} uses
        {{ abilityLabel(selectedSkillOption.ability) }}
        ({{ formatModifier(skillModifier) }}) proficiency not applied yet.
      </p>
    </div>

    <div v-else class="max-w-xs">
      <label class="text-sm text-neutral-400" for="custom-mod">Custom modifier</label>
      <input
        id="custom-mod"
        v-model.number="customModifier"
        type="number"
        class="mt-1 block w-28 rounded-lg px-3 py-2 text-black"
        :disabled="isRolling"
      />
    </div>

    <BaseButton variant="primary" :loading="isRolling" :disabled="isRolling" @click="handleRoll">
      Roll
    </BaseButton>

    <div v-if="displayFaces.length" class="space-y-4 border-t border-neutral-700 pt-4">
      <div class="flex flex-wrap gap-4">
        <div
          v-for="(face, index) in displayFaces"
          :key="index"
          class="flex flex-col items-center gap-1"
        >
          <DiceFace
            :dice-type="diceType"
            :value="face"
            :dimmed="!!outcome && !outcome.dice[index]?.kept"
            size="lg"
          />
          <p
            v-if="outcome"
            class="text-xs"
            :class="
              outcome.dice[index]?.kept ? 'text-neutral-300' : 'text-neutral-500 line-through'
            "
          >
            {{ outcome.dice[index]?.value }}
          </p>
        </div>
      </div>

      <div v-if="outcome && !isRolling" class="space-y-1 text-sm">
        <p v-if="outcome.skillName">
          <span class="text-neutral-400">Skill:</span>
          {{ outcome.skillName }}
          <span v-if="outcome.abilityKey">({{ abilityLabel(outcome.abilityKey) }})</span>
        </p>
        <p v-if="outcome.mode !== 'normal'" class="capitalize text-neutral-300">
          Rolled with {{ outcome.mode }}
        </p>
        <p>
          <span class="text-neutral-400">Dice:</span>
          {{ outcome.diceTotal }}
          <span v-if="outcome.modifier !== 0">
            {{ formatModifier(outcome.modifier) }} modifier
          </span>
        </p>
        <p class="text-2xl font-semibold">Total: {{ outcome.total }}</p>
      </div>
      <p v-else-if="isRolling" class="text-neutral-400">Rolling...</p>
    </div>
  </div>
</template>
