<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { isAxiosError } from 'axios'
import { useCampaignStore } from '@/stores/campaign'
import { useCharacterStore, type CharacterResponse } from '@/stores/character'
import BaseButton from '@/components/BaseButton.vue'

const props = defineProps<{
  campaignId: number
  characterType: 'pc' | 'npc'
}>()

const emit = defineEmits<{
  close: []
  added: []
}>()

const campaignStore = useCampaignStore()
const characterStore = useCharacterStore()

const isLoading = ref(true)
const isAdding = ref(false)
const errorMessage = ref<string | null>(null)
const selectedId = ref<number | null>(null)

const attachedIds = computed(() => {
  const attached =
    props.characterType === 'pc'
      ? campaignStore.playableCharacters
      : campaignStore.nonplayableCharacters
  return new Set(attached.map((c) => c.id))
})

const roster = computed(() =>
  props.characterType === 'pc'
    ? characterStore.playableCharacters
    : characterStore.nonplayableCharacters,
)

const availableCharacters = computed(() =>
  roster.value.filter((entry) => !attachedIds.value.has(entry.character.id)),
)

const typeLabel = computed(() => (props.characterType === 'pc' ? 'Playable Character' : 'NPC'))

function characterSubtitle(character: CharacterResponse) {
  if (character.characterType === 'NPC') {
    return `${character.raceName} - NPC`
  }
  const className = character.classNames[0] ?? 'Not assigned'
  return `${character.raceName} - ${className}`
}

async function handleAdd() {
  if (!selectedId.value) return
  isAdding.value = true
  errorMessage.value = null
  try {
    if (props.characterType === 'pc') {
      await campaignStore.addPlayableCharacter(props.campaignId, selectedId.value)
    } else {
      await campaignStore.addNonplayableCharacter(props.campaignId, selectedId.value)
    }
    emit('added')
    emit('close')
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to add character')
      : 'An unexpected error occurred. Please try again.'
  } finally {
    isAdding.value = false
  }
}

onMounted(async () => {
  try {
    if (props.characterType === 'pc') {
      await characterStore.fetchCurrentUserPlayableCharacters()
    } else {
      await characterStore.fetchCurrentUserNonplayableCharacters()
    }
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to load characters')
      : 'An unexpected error occurred. Please try again.'
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <Teleport to="body">
    <div
      class="fixed inset-0 bg-overlay flex items-center justify-center z-50"
      @click.self="emit('close')"
    >
      <div class="bg-surface rounded-xl p-6 w-full max-w-md flex flex-col gap-4">
        <h3 class="text-xl">Add {{ typeLabel }}</h3>

        <p v-if="isLoading" class="text-fg-subtle text-sm">Loading your characters...</p>
        <p v-else-if="availableCharacters.length === 0" class="text-fg-subtle text-sm">
          No available characters to add. Create a new one instead.
        </p>

        <div v-else class="flex flex-col gap-2 max-h-64 overflow-y-auto">
          <label
            v-for="entry in availableCharacters"
            :key="entry.character.id"
            class="flex items-center gap-3 rounded-lg border px-3 py-2 cursor-pointer hover:bg-surface-muted"
            :class="selectedId === entry.character.id ? 'border-accent bg-accent-muted' : 'border-border'"
          >
            <input
              v-model="selectedId"
              type="radio"
              name="character"
              :value="entry.character.id"
              class="shrink-0"
            />
            <div class="min-w-0">
              <p class="font-medium truncate">{{ entry.character.name }}</p>
              <p class="text-sm text-fg-subtle capitalize">{{ characterSubtitle(entry.character) }}</p>
            </div>
          </label>
        </div>

        <p v-if="errorMessage" class="text-danger text-sm">{{ errorMessage }}</p>

        <div class="flex justify-end gap-2">
          <BaseButton type="button" variant="danger" @click="emit('close')">Cancel</BaseButton>
          <BaseButton
            type="button"
            variant="primary"
            :loading="isAdding"
            :disabled="!selectedId || isLoading"
            @click="handleAdd"
          >
            Add to Campaign
          </BaseButton>
        </div>
      </div>
    </div>
  </Teleport>
</template>
