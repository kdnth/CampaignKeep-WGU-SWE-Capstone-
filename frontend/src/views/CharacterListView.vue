<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import CharacterCard from '@/components/CharacterCard.vue'
import { useCharacterStore } from '@/stores/character'
import { isAxiosError } from 'axios'
import { computed, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import BackToLink from '@/components/BackToLink.vue'

const characterStore = useCharacterStore()
const { nonplayableCharacters, playableCharacters } = storeToRefs(characterStore)

const router = useRouter()

const isLoading = ref(true)
const errorMessage = ref<string | null>(null)

const searchQuery = ref('')
const typeFilter = ref<'all' | 'npc' | 'playable'>('all')
const sortOrder = ref<'newest' | 'oldest'>('newest')

const filteredCharacters = computed(() => {
  let result = [...nonplayableCharacters.value, ...playableCharacters.value]

  if (typeFilter.value === 'npc') {
    result = nonplayableCharacters.value
  } else if (typeFilter.value === 'playable') {
    result = playableCharacters.value
  }
  if (searchQuery.value.trim() !== '') {
    const query = searchQuery.value.trim().toLowerCase()
    result = result.filter((c) => c.character.name.toLowerCase().includes(query))
  }

  result = [...result].sort((a, b) => {
    const dateA = new Date(a.character.createdOn).getTime()
    const dateB = new Date(b.character.createdOn).getTime()
    return sortOrder.value === 'newest' ? dateB - dateA : dateA - dateB
  })

  return result
})

onMounted(async () => {
  errorMessage.value = null
  isLoading.value = true

  try {
    await characterStore.fetchCurrentUserPlayableCharacters()
    await characterStore.fetchCurrentUserNonplayableCharacters()
  } catch (err: unknown) {
    if (isAxiosError(err)) {
      errorMessage.value =
        err.response?.data?.message ??
        `Failed to load characters (${err.response?.status ?? 'network error'})`
    } else {
      errorMessage.value = 'Failed to load characters. Please try again.'
    }
  } finally {
    isLoading.value = false
  }
})

function handleClick(id: number) {
  router.push({
    name: 'characterDetail',
    params: { id: id },
  })
}
</script>
<template>
  <div class="flex w-full flex-col gap-4 p-8">
    <BackToLink page="home" link-name="Home" />
    <p v-if="isLoading" class="text-fg-muted">Loading characters...</p>
    <p v-else-if="errorMessage" class="text-danger">{{ errorMessage }}</p>
    <div
      v-else-if="nonplayableCharacters.length + playableCharacters.length === 0"
      class="text-fg-muted"
    >
      <p>No characters yet. Create one to get started.</p>
    </div>

    <div class="flex gap-4 flex-wrap">
      <input
        id="search-query"
        v-model="searchQuery"
        type="text"
        placeholder="Search characters..."
        class="rounded-lg px-3 py-2"
      />

      <div class="relative">
        <select
          id="status-filter"
          v-model="typeFilter"
          class="appearance-none rounded-lg pl-3 pr-8 py-2 w-full"
        >
          <option value="all">All</option>
          <option value="npc">NPC</option>
          <option value="playable">Playable</option>
        </select>
      </div>

      <div class="relative">
        <select
          id="sort-order"
          v-model="sortOrder"
          class="appearance-none rounded-lg pl-3 pr-8 py-2 w-full"
        >
          <option value="newest">Newest first</option>
          <option value="oldest">Oldest first</option>
        </select>
      </div>
    </div>

    <div class="grid grid-cols-[repeat(auto-fill,minmax(18rem,1fr))] gap-4">
      <CharacterCard
        v-for="entry in filteredCharacters"
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
    <div class="flex gap-2">
      <BaseButton variant="primary" @click="router.push({ name: 'createPlayableCharacter' })">
        Create Playable
      </BaseButton>
      <BaseButton variant="secondary" @click="router.push({ name: 'createNonplayableCharacter' })">
        Create NPC
      </BaseButton>
    </div>
  </div>
</template>
