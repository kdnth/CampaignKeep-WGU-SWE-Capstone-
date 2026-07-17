<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { isAxiosError } from 'axios'
import CharacterCard from '@/components/CharacterCard.vue'
import { useCharacterStore } from '@/stores/character'
import { useRouter } from 'vue-router'

const characterStore = useCharacterStore()
const { playableCharacters, nonplayableCharacters } = storeToRefs(characterStore)
const characters = computed(() => [...playableCharacters.value, ...nonplayableCharacters.value])
const router = useRouter()

const isLoading = ref(true)
const errorMessage = ref<string | null>(null)

onMounted(async () => {
  try {
    await characterStore.fetchCurrentUserNonplayableCharacters()
    await characterStore.fetchCurrentUserPlayableCharacters()
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to load characters')
      : 'An unexpected error occurred. Please try again.'
  } finally {
    isLoading.value = false
  }
})

function handleClick(id: number) {
  router.push({ name: 'characterDetail', params: { id } })
}
</script>

<template>
  <div class="flex flex-col gap-3">
    <div class="flex items-center justify-between">
      <h4 class="text-3xl text-fg">My Characters</h4>
      <RouterLink :to="{ name: 'characters' }" class="text-sm text-accent hover:underline">
        View all
      </RouterLink>
    </div>

    <p v-if="isLoading" class="text-fg-muted">Loading campaigns...</p>
    <p v-else-if="errorMessage" class="text-danger">{{ errorMessage }}</p>
    <p
      v-else-if="nonplayableCharacters.length === 0 && playableCharacters.length === 0"
      class="text-fg-muted"
    >
      No characters yet. Create one to get started.
    </p>

    <div
      class="grid grid-cols-[repeat(auto-fill,minmax(18rem,1fr))] grid-rows-[repeat(1, 1fr)] max-h-64 gap-4 overflow-hidden"
    >
      <CharacterCard
        v-for="entry in characters"
        :key="entry.character.id"
        :name="entry.character.name"
        :raceName="entry.character.raceName"
        :className="
          entry.character.characterType === 'NPC'
            ? 'NPC'
            : (entry.character.classNames[0] ?? 'Not assigned')
        "
        :created-on="entry.character.createdOn"
        :campaigns="entry.campaignNames"
        @click="handleClick(entry.character.id)"
      />
    </div>
  </div>
</template>
