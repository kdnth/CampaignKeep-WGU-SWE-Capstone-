<script setup lang="ts">
import { useCharacterStore, type CreatePlayableCharacterRequest } from '@/stores/character'
import { useCampaignStore } from '@/stores/campaign'
import { useDndClassStore } from '@/stores/dndClass'
import { useSubraceStore } from '@/stores/subrace'
import { useRaceStore } from '@/stores/race'
import { useRoute, useRouter } from 'vue-router'
import { isAxiosError } from 'axios'
import { computed, onMounted, ref } from 'vue'
import StepNameRaceClass from '@/components/StepNameRaceClass.vue'
import StepSubrace from '@/components/StepSubrace.vue'
import StepAbilityScores from '@/components/StepAbilityScores.vue'
import StepBackgroundLanguages from '@/components/StepBackgroundLanguages.vue'
import StepSpells from '@/components/StepSpells.vue'
import StepStartingEquipment from '@/components/equipment/StepStartingEquipment.vue'
import StepReview from '@/components/StepReview.vue'
import { useInventoryStore } from '@/stores/inventory'

const HIGH_ELF_SUBRACE_ID = 1

const router = useRouter()
const route = useRoute()
const characterStore = useCharacterStore()
const campaignStore = useCampaignStore()
const raceStore = useRaceStore()
const subraceStore = useSubraceStore()
const dndClassStore = useDndClassStore()
const inventoryStore = useInventoryStore()

const currentStep = ref(1)
const submitting = ref(false)
const errorMessage = ref<string | null>(null)

const startingEquipment = ref({
  weaponItemId: null as number | null,
  armorItemId: null as number | null,
  shieldItemId: null as number | null,
})

const form = ref<CreatePlayableCharacterRequest>({
  name: '',
  raceId: 0,
  subraceId: null,
  backgroundId: null,
  classId: 0,
  languageIds: [],
  strength: 0,
  dexterity: 0,
  constitution: 0,
  intelligence: 0,
  wisdom: 0,
  charisma: 0,
  hitPoints: 0,
  armorClass: 0,
  spellIds: [],
})

const selectedRaceSubraces = computed(() => {
  return subraceStore.subraces.filter((r) => r.raceId === form.value.raceId)
})

const selectedClass = computed(() =>
  dndClassStore.classes.find((c) => c.id === form.value.classId),
)

const needsSpellStep = computed(() => {
  const isWizard = selectedClass.value?.index === 'wizard'
  const isHighElf = form.value.subraceId === HIGH_ELF_SUBRACE_ID
  return isWizard || isHighElf
})

const reviewStep = computed(() => (needsSpellStep.value ? 7 : 6))
const equipmentStep = computed(() => (needsSpellStep.value ? 6 : 5))

function nextStep() {
  if (currentStep.value === 1 && selectedRaceSubraces.value.length === 0) {
    currentStep.value = 3
  } else if (currentStep.value === 4) {
    currentStep.value = needsSpellStep.value ? 5 : equipmentStep.value
  } else {
    currentStep.value++
  }
}

function prevStep() {
  if (currentStep.value === reviewStep.value) {
    currentStep.value = equipmentStep.value
  } else if (currentStep.value === equipmentStep.value) {
    currentStep.value = needsSpellStep.value ? 5 : 4
  } else if (currentStep.value === 3 && selectedRaceSubraces.value.length === 0) {
    currentStep.value = 1
  } else {
    currentStep.value--
  }
}

function computeDerivedStats() {
  const dndClass = dndClassStore.classes.find((c) => c.id === form.value.classId)
  const conModifier = Math.floor((form.value.constitution - 10) / 2)
  const dexModifier = Math.floor((form.value.dexterity - 10) / 2)

  form.value.hitPoints = (dndClass?.hitDice ?? 8) + conModifier
  form.value.armorClass = 10 + dexModifier
}

const attachCampaignId = computed(() => {
  const id = route.query.attachCampaign
  if (!id || Array.isArray(id)) return null
  const parsed = Number(id)
  return Number.isFinite(parsed) ? parsed : null
})

async function handleSubmit() {
  computeDerivedStats()
  submitting.value = true
  errorMessage.value = null

  try {
    const character = await characterStore.createPlayableCharacter(form.value)
    if (startingEquipment.value.weaponItemId) {
      await inventoryStore.submitStartingEquipment(character.id, {
        weaponItemId: startingEquipment.value.weaponItemId,
        armorItemId: startingEquipment.value.armorItemId,
        shieldItemId: startingEquipment.value.shieldItemId,
      })
    }
    if (attachCampaignId.value) {
      await campaignStore.addPlayableCharacter(attachCampaignId.value, character.id)
      router.push({ name: 'campaignDetail', params: { id: attachCampaignId.value } })
    } else {
      router.push({ name: 'characterDetail', params: { id: character.id } })
    }
  } catch (err: unknown) {
    if (isAxiosError(err)) {
      errorMessage.value = err.response?.data?.message ?? 'Failed to create character'
    } else {
      errorMessage.value = 'An unexpected error occurred. Please try again.'
    }
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await Promise.all([
    raceStore.fetchRaces(),
    subraceStore.fetchSubraces(),
    dndClassStore.fetchClasses(),
  ])
})
</script>
<template>
  <div class="mx-auto p-6" :class="currentStep === 5 && needsSpellStep ? 'max-w-6xl' : 'max-w-2xl'">
    <h1 class="text-2xl mb-4">Create Character</h1>

    <StepNameRaceClass v-if="currentStep === 1" v-model:form="form" @next="nextStep" />
    <StepSubrace
      v-else-if="currentStep === 2"
      v-model:form="form"
      :subraces="selectedRaceSubraces"
      @next="nextStep"
      @back="prevStep"
    />
    <StepAbilityScores
      v-else-if="currentStep === 3"
      v-model:form="form"
      @next="nextStep"
      @back="prevStep"
    />
    <StepBackgroundLanguages
      v-else-if="currentStep === 4"
      v-model:form="form"
      @next="nextStep"
      @back="prevStep"
    />
    <StepSpells
      v-else-if="currentStep === 5 && needsSpellStep"
      :form="form"
      @next="nextStep"
      @back="prevStep"
    />
    <StepStartingEquipment
      v-else-if="currentStep === equipmentStep"
      v-model="startingEquipment"
      :class-id="form.classId"
      :strength="form.strength"
      @next="nextStep"
      @back="prevStep"
    />
    <StepReview
      v-else-if="currentStep === reviewStep"
      :form="form"
      :submitting="submitting"
      :error="errorMessage"
      @back="prevStep"
      @submit="handleSubmit"
    />
  </div>
</template>
