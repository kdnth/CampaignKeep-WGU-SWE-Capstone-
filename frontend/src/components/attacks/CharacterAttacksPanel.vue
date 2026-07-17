<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import { useInventoryStore, type CharacterAttackResponse } from '@/stores/inventory'
import { formatModifier, rollDamage } from '@/utils/diceRoller'
import { isAxiosError } from 'axios'
import { onMounted, ref } from 'vue'

const props = defineProps<{
  characterId: number
}>()

const inventoryStore = useInventoryStore()
const isLoading = ref(true)
const errorMessage = ref<string | null>(null)
const attackList = ref<CharacterAttackResponse[]>([])
const attackCounts = ref<Record<number, number>>({})
const rollResults = ref<Record<number, string>>({})

function getAttackCount(attack: CharacterAttackResponse) {
  return attackCounts.value[attack.inventoryItemId] ?? 1
}

function setAttackCount(attack: CharacterAttackResponse, value: number) {
  attackCounts.value[attack.inventoryItemId] = Math.max(1, value)
}

function rollAttack(attack: CharacterAttackResponse) {
  const count = getAttackCount(attack)
  const result = rollDamage(attack.damageDice, count, attack.damageBonus)
  const bonusPart =
    attack.damageBonus !== 0
      ? ` + ${formatModifier(attack.damageBonus)} x ${count} = ${result.total}`
      : ` = ${result.total}`
  rollResults.value[attack.inventoryItemId] =
    `${count} attack${count > 1 ? 's' : ''} → [${result.rolls.join(', ')}]${bonusPart} ${attack.damageType}`
}

onMounted(async () => {
  try {
    attackList.value = await inventoryStore.fetchAttacks(props.characterId)
    for (const attack of attackList.value) {
      attackCounts.value[attack.inventoryItemId] = 1
    }
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to load attacks')
      : 'Failed to load attacks'
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <div class="space-y-6 rounded-2xl border-2 border-border-strong p-6 text-fg">
    <p v-if="isLoading" class="text-fg-subtle">Loading attacks...</p>
    <p v-else-if="errorMessage" class="text-danger">{{ errorMessage }}</p>
    <p v-else-if="attackList.length === 0" class="text-fg-subtle">
      No equipped weapons. Equip a weapon in the Equipment tab to see attacks here.
    </p>

    <div v-else class="space-y-4">
      <div
        v-for="attack in attackList"
        :key="attack.inventoryItemId"
        class="rounded-lg border border-border p-4"
      >
        <h3 class="text-lg font-semibold">{{ attack.weaponName }}</h3>
        <p class="text-sm text-fg-subtle capitalize">
          {{ attack.equippedSlot.replace('_', ' ') }} · {{ attack.weaponRange }}
          <span v-if="attack.range"> · {{ attack.range }} ft.</span>
        </p>
        <div class="mt-2 flex flex-wrap gap-4 text-sm">
          <p>
            <span class="text-fg-subtle">Attack:</span>
            {{ formatModifier(attack.attackBonus) }}
          </p>
          <p>
            <span class="text-fg-subtle">Damage:</span>
            {{ attack.damageDice }} {{ formatModifier(attack.damageBonus) }}
            {{ attack.damageType }}
          </p>
        </div>

        <div class="mt-4 flex flex-wrap items-end gap-3">
          <div>
            <label
              :for="`attacks-${attack.inventoryItemId}`"
              class="text-sm text-fg-subtle"
            >
              Number of attacks
            </label>
            <input
              :id="`attacks-${attack.inventoryItemId}`"
              type="number"
              min="1"
              :value="getAttackCount(attack)"
              class="bg-input block w-24 rounded-lg px-3 py-2 text-fg"
              @input="
                setAttackCount(
                  attack,
                  Number(($event.target as HTMLInputElement).value),
                )
              "
            />
          </div>
          <BaseButton variant="primary" @click="rollAttack(attack)">Roll Damage</BaseButton>
        </div>

        <p v-if="rollResults[attack.inventoryItemId]" class="mt-3 text-success">
          {{ rollResults[attack.inventoryItemId] }}
        </p>
      </div>
    </div>
  </div>
</template>
