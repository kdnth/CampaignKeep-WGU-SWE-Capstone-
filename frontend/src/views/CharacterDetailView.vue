<script setup lang="ts">
import BackToLink from '@/components/BackToLink.vue'
import BaseButton from '@/components/BaseButton.vue'
import CharacterAvatar from '@/components/CharacterAvatar.vue'
import CharacterStatusBadge from '@/components/character/CharacterStatusBadge.vue'
import ConfirmationModal from '@/components/ConfirmationModal.vue'
import { useCharacterStore } from '@/stores/character'
import { formatDateStr } from '@/utils/dateUtil'
import { isAxiosError } from 'axios'
import { storeToRefs } from 'pinia'
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'

const abilities = [
  'strength',
  'dexterity',
  'constitution',
  'intelligence',
  'wisdom',
  'charisma',
] as const

const router = useRouter()
const route = useRoute()
const characterId = Number(route.params.id)

const characterStore = useCharacterStore()
const { activeCharacter, characterCampaigns } = storeToRefs(characterStore)

const isLoading = ref(true)
const errorMessage = ref<string | null>(null)
const showDeleteConfirm = ref(false)
const isDeleting = ref(false)

const isPlayable = computed(() => activeCharacter.value?.characterType === 'PC')
const typeLabel = computed(() => (isPlayable.value ? 'Playable Character' : 'NPC'))
const classLabel = computed(() => {
  if (!activeCharacter.value) return 'Not assigned'
  if (activeCharacter.value.characterType === 'NPC') return 'NPC'
  return activeCharacter.value.classNames[0] ?? 'Not assigned'
})

function formatModifier(mod: number) {
  return mod >= 0 ? `+${mod}` : `${mod}`
}

function requestDelete() {
  showDeleteConfirm.value = true
}

async function handleDelete() {
  isDeleting.value = true
  try {
    await characterStore.deleteCharacter(characterId)
    router.push({ name: 'characters' })
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to delete character')
      : 'An unexpected error occurred. Please try again.'
    showDeleteConfirm.value = false
  } finally {
    isDeleting.value = false
  }
}

onMounted(async () => {
  try {
    await characterStore.fetchCharacter(characterId)
    await characterStore.fetchCharacterCampaigns(characterId)
  } catch (err: unknown) {
    if (isAxiosError(err)) {
      errorMessage.value = err.response?.data?.message ?? 'Failed to load character'
    } else {
      errorMessage.value = 'An unexpected error occurred. Please try again.'
    }
  } finally {
    isLoading.value = false
  }
})
</script>
<template>
  <div class="flex w-full flex-col gap-4 p-8">
    <BackToLink page="characters" link-name="My Characters" />

    <p v-if="isLoading" class="text-neutral-300">Loading character...</p>
    <p v-else-if="errorMessage && !activeCharacter" class="text-red-500">{{ errorMessage }}</p>

    <template v-else-if="activeCharacter">
      <div class="flex flex-wrap items-start gap-6">
        <div class="rounded-xl bg-purple-400 p-4">
          <CharacterAvatar
            :race-name="activeCharacter.raceName"
            :class-name="classLabel"
            size="lg"
          />
        </div>
        <div class="flex flex-1 flex-wrap items-start justify-between gap-4">
          <div>
            <p class="text-sm uppercase tracking-wide text-purple-400">{{ typeLabel }}</p>
            <h1 class="text-3xl text-white">{{ activeCharacter.name }}</h1>
            <p class="text-neutral-300 capitalize">
              {{ activeCharacter.raceName }}
              <span v-if="activeCharacter.subraceName">({{ activeCharacter.subraceName }})</span>
            </p>
          </div>
          <CharacterStatusBadge :status="activeCharacter.status" />
        </div>
      </div>

      <div class="grid gap-4 md:grid-cols-2">
        <section class="space-y-2 rounded-2xl border-2 border-white p-6 text-white">
          <h2 class="text-xl">Identity</h2>
          <p v-if="isPlayable && activeCharacter.classNames.length">
            <span class="text-neutral-300">Class:</span>
            {{ activeCharacter.classNames.join(', ') }}
          </p>
          <p v-if="activeCharacter.backgroundName">
            <span class="text-neutral-300">Background:</span> {{ activeCharacter.backgroundName }}
          </p>
          <p>
            <span class="text-neutral-300">Languages:</span>
            {{ activeCharacter.languageNames.join(', ') || 'None' }}
          </p>
        </section>

        <section class="space-y-2 rounded-2xl border-2 border-white p-6 text-white">
          <h2 class="text-xl">Combat</h2>
          <p><span class="text-neutral-300">Hit Points:</span> {{ activeCharacter.hitPoints }}</p>
          <p><span class="text-neutral-300">Armor Class:</span> {{ activeCharacter.armorClass }}</p>
          <p>
            <span class="text-neutral-300">Initiative:</span>
            {{ formatModifier(activeCharacter.initiativeBonus) }}
          </p>
          <p><span class="text-neutral-300">Speed:</span> {{ activeCharacter.speed }} ft.</p>
        </section>
      </div>

      <section class="rounded-2xl border-2 border-white p-6 text-white">
        <h2 class="mb-4 text-xl">Ability Scores</h2>
        <div class="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-6">
          <div v-for="ability in abilities" :key="ability" class="text-center">
            <p class="text-sm uppercase text-neutral-300">{{ ability.slice(0, 3) }}</p>
            <p class="text-2xl">{{ activeCharacter[ability] }}</p>
            <p class="text-sm text-neutral-400">
              {{ formatModifier(Math.floor((activeCharacter[ability] - 10) / 2)) }}
            </p>
          </div>
        </div>
      </section>

      <section class="rounded-2xl border-2 border-white p-6 text-white">
        <h2 class="mb-2 text-xl">Campaigns</h2>
        <p v-if="characterCampaigns.length === 0" class="text-neutral-300">
          Not assigned to any campaigns.
        </p>
        <ul v-else class="flex flex-wrap gap-2">
          <li v-for="campaign in characterCampaigns" :key="campaign.id">
            <RouterLink
              :to="{ name: 'campaignDetail', params: { id: campaign.id } }"
              class="rounded-lg bg-purple-200 px-3 py-1 text-sm font-semibold text-purple-800 hover:bg-purple-300"
            >
              {{ campaign.title }}
            </RouterLink>
          </li>
        </ul>
      </section>

      <p class="text-sm text-neutral-400">
        Created {{ formatDateStr(activeCharacter.createdOn) }} · Updated
        {{ formatDateStr(activeCharacter.updatedOn) }}
      </p>

      <div class="flex gap-2">
        <BaseButton variant="danger" @click="requestDelete">Delete Character</BaseButton>
      </div>

      <p v-if="errorMessage" class="text-red-500">{{ errorMessage }}</p>
    </template>

    <ConfirmationModal
      v-if="showDeleteConfirm"
      title="Delete Character"
      message="Are you sure you want to delete this character? This action cannot be undone."
      variant="danger"
      :is-loading="isDeleting"
      @confirm="handleDelete"
      @cancel="showDeleteConfirm = false"
    />
  </div>
</template>
