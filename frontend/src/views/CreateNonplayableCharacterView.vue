<script setup lang="ts">
import { useCharacterStore, type CreateNonplayableCharacterRequest } from '@/stores/character'
import { useCampaignStore } from '@/stores/campaign'
import { useInventoryStore } from '@/stores/inventory'
import { useSubraceStore } from '@/stores/subrace'
import { useRaceStore } from '@/stores/race'
import { useRoute, useRouter } from 'vue-router'
import { isAxiosError } from 'axios'
import { computed, onMounted, ref } from 'vue'
import StepNameRace from '@/components/StepNameRace.vue'
import StepSubrace from '@/components/StepSubrace.vue'
import StepAbilityScores from '@/components/StepAbilityScores.vue'
import StepBackgroundLanguages from '@/components/StepBackgroundLanguages.vue'
import StepNpcStartingPack from '@/components/equipment/StepNpcStartingPack.vue'
import NpcStepReview from '@/components/NpcStepReview.vue'

const router = useRouter()
const route = useRoute()
const characterStore = useCharacterStore()
const campaignStore = useCampaignStore()
const inventoryStore = useInventoryStore()
const raceStore = useRaceStore()
const subraceStore = useSubraceStore()

const currentStep = ref(1)
const submitting = ref(false)
const errorMessage = ref<string | null>(null)
const startingPackIndex = ref<string | null>(null)

const form = ref<CreateNonplayableCharacterRequest>({
  name: '',
  raceId: 0,
  subraceId: null,
  backgroundId: null,
  languageIds: [],
  strength: 0,
  dexterity: 0,
  constitution: 0,
  intelligence: 0,
  wisdom: 0,
  charisma: 0,
  hitPoints: 0,
  armorClass: 0,
})

const selectedRaceSubraces = computed(() => {
  return subraceStore.subraces.filter((r) => r.raceId === form.value.raceId)
})

function nextStep() {
  if (currentStep.value === 1 && selectedRaceSubraces.value.length === 0) {
    currentStep.value = 3
  } else {
    currentStep.value++
  }
}

function prevStep() {
  if (currentStep.value === 3 && selectedRaceSubraces.value.length === 0) {
    currentStep.value = 1
  } else {
    currentStep.value--
  }
}

function computeDerivedStats() {
  const conModifier = Math.floor((form.value.constitution - 10) / 2)
  const dexModifier = Math.floor((form.value.dexterity - 10) / 2)

  form.value.hitPoints = 8 + conModifier
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
    const character = await characterStore.createNonplayableCharacter(form.value)
    if (startingPackIndex.value) {
      await inventoryStore.submitStartingPack(character.id, startingPackIndex.value)
    }
    if (attachCampaignId.value) {
      await campaignStore.addNonplayableCharacter(attachCampaignId.value, character.id)
      router.push({ name: 'campaignDetail', params: { id: attachCampaignId.value } })
    } else {
      router.push({ name: 'characterDetail', params: { id: character.id } })
    }
  } catch (err: unknown) {
    if (isAxiosError(err)) {
      errorMessage.value = err.response?.data?.message ?? 'Failed to create NPC'
    } else {
      errorMessage.value = 'An unexpected error occurred. Please try again.'
    }
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await Promise.all([raceStore.fetchRaces(), subraceStore.fetchSubraces()])
})
</script>
<template>
  <div class="mx-auto max-w-2xl p-6">
    <h1 class="mb-4 text-2xl">Create NPC</h1>

    <StepNameRace v-if="currentStep === 1" v-model:form="form" @next="nextStep" />
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
    <StepNpcStartingPack
      v-else-if="currentStep === 5"
      v-model="startingPackIndex"
      @next="nextStep"
      @back="prevStep"
    />
    <NpcStepReview
      v-else-if="currentStep === 6"
      :form="form"
      :submitting="submitting"
      :error="errorMessage"
      @back="prevStep"
      @submit="handleSubmit"
    />
  </div>
</template>
