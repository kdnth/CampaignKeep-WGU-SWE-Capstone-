<script setup lang="ts">
import CreateCampaignItemModal from '@/components/equipment/CreateCampaignItemModal.vue'
import ItemsListPanel from '@/components/equipment/ItemsListPanel.vue'
import {
  formatRarityLabel,
  useInventoryStore,
  type ItemDetailResponse,
  type ItemRarity,
  type ItemType,
} from '@/stores/inventory'
import { isAxiosError } from 'axios'
import { storeToRefs } from 'pinia'
import { computed, onMounted, ref, watch } from 'vue'

const props = defineProps<{
  campaignId: number
}>()

const inventoryStore = useInventoryStore()
const { campaignItems } = storeToRefs(inventoryStore)

const isLoading = ref(true)
const errorMessage = ref<string | null>(null)

const searchQuery = ref('')
const typeFilter = ref<ItemType | 'all'>('all')
const rarityFilter = ref<ItemRarity | 'all'>('all')
const magicalFilter = ref<'all' | 'magical' | 'mundane'>('all')
const sortOrder = ref<'name-asc' | 'name-desc' | 'value-asc' | 'value-desc' | 'rarity'>('name-asc')
const currentPage = ref(1)
const pageSize = 10

const itemTypes: ItemType[] = ['weapon', 'armor', 'tool', 'gear', 'jewelry', 'clothing']
const rarities: ItemRarity[] = ['common', 'uncommon', 'rare', 'very_rare', 'legendary', 'artifact']

const rarityRank: Record<ItemRarity, number> = {
  common: 0,
  uncommon: 1,
  rare: 2,
  very_rare: 3,
  legendary: 4,
  artifact: 5,
}

const filteredItems = computed(() => {
  let result: ItemDetailResponse[] = [...campaignItems.value]

  if (searchQuery.value.trim()) {
    const query = searchQuery.value.trim().toLowerCase()
    result = result.filter(
      (item) =>
        item.name.toLowerCase().includes(query) ||
        (item.description?.toLowerCase().includes(query) ?? false),
    )
  }

  if (typeFilter.value !== 'all') {
    result = result.filter((item) => item.itemType === typeFilter.value)
  }

  if (rarityFilter.value !== 'all') {
    result = result.filter((item) => item.rarity === rarityFilter.value)
  }

  if (magicalFilter.value === 'magical') {
    result = result.filter((item) => item.isMagical)
  } else if (magicalFilter.value === 'mundane') {
    result = result.filter((item) => !item.isMagical)
  }

  result = [...result].sort((a, b) => {
    switch (sortOrder.value) {
      case 'name-desc':
        return b.name.localeCompare(a.name)
      case 'value-asc':
        return a.effectiveValue - b.effectiveValue || a.name.localeCompare(b.name)
      case 'value-desc':
        return b.effectiveValue - a.effectiveValue || a.name.localeCompare(b.name)
      case 'rarity':
        return rarityRank[a.rarity] - rarityRank[b.rarity] || a.name.localeCompare(b.name)
      case 'name-asc':
      default:
        return a.name.localeCompare(b.name)
    }
  })

  return result
})

const totalPages = computed(() => Math.max(1, Math.ceil(filteredItems.value.length / pageSize)))

const paginatedItems = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredItems.value.slice(start, start + pageSize)
})

const pageStart = computed(() =>
  filteredItems.value.length === 0 ? 0 : (currentPage.value - 1) * pageSize + 1,
)

const pageEnd = computed(() => Math.min(currentPage.value * pageSize, filteredItems.value.length))

watch([searchQuery, typeFilter, rarityFilter, magicalFilter, sortOrder], () => {
  currentPage.value = 1
})

watch(totalPages, (pages) => {
  if (currentPage.value > pages) {
    currentPage.value = pages
  }
})

function goToPage(page: number) {
  currentPage.value = Math.min(Math.max(1, page), totalPages.value)
}

function formatTypeLabel(type: ItemType): string {
  return type.charAt(0).toUpperCase() + type.slice(1)
}

async function loadItems() {
  isLoading.value = true
  errorMessage.value = null
  try {
    await inventoryStore.fetchCampaignItems(props.campaignId)
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to load campaign items')
      : 'Failed to load campaign items'
  } finally {
    isLoading.value = false
  }
}

onMounted(loadItems)
</script>

<template>
  <div class="space-y-4">
    <div class="flex flex-wrap items-center justify-between gap-3">
      <p class="text-sm text-fg-subtle">
        Reference gear available to this campaign plus any custom items you create.
      </p>
      <CreateCampaignItemModal :campaign-id="campaignId" @created="loadItems" />
    </div>

    <div class="flex flex-wrap items-end gap-3">
      <div class="flex min-w-[12rem] flex-1 flex-col gap-1">
        <label class="text-sm text-fg-muted" for="item-search">Search</label>
        <input
          id="item-search"
          v-model="searchQuery"
          type="text"
          placeholder="Search items..."
          class="rounded-lg border border-border bg-surface-muted px-3 py-2 text-fg"
        />
      </div>

      <div class="flex flex-col gap-1">
        <label class="text-sm text-fg-muted" for="item-type-filter">Type</label>
        <select
          id="item-type-filter"
          v-model="typeFilter"
          class="rounded-lg border border-border bg-surface-muted pr-8 py-2 capitalize text-fg"
        >
          <option value="all">All types</option>
          <option v-for="type in itemTypes" :key="type" :value="type">
            {{ formatTypeLabel(type) }}
          </option>
        </select>
      </div>

      <div class="flex flex-col gap-1">
        <label class="text-sm text-fg-muted" for="item-rarity-filter">Rarity</label>
        <select
          id="item-rarity-filter"
          v-model="rarityFilter"
          class="rounded-lg border border-border bg-surface-muted pl-2 pr-8 py-2 text-fg"
        >
          <option value="all">All rarities</option>
          <option v-for="rarity in rarities" :key="rarity" :value="rarity">
            {{ formatRarityLabel(rarity) }}
          </option>
        </select>
      </div>

      <div class="flex flex-col gap-1">
        <label class="text-sm text-fg-muted" for="item-magical-filter">Magic</label>
        <select
          id="item-magical-filter"
          v-model="magicalFilter"
          class="rounded-lg border border-border bg-surface-muted px-3 py-2 text-fg"
        >
          <option value="all">All</option>
          <option value="magical">Magical</option>
          <option value="mundane">Mundane</option>
        </select>
      </div>

      <div class="flex flex-col gap-1">
        <label class="text-sm text-fg-muted" for="item-sort">Sort</label>
        <select
          id="item-sort"
          v-model="sortOrder"
          class="rounded-lg border border-border bg-surface-muted px-3 py-2 text-fg"
        >
          <option value="name-asc">Name (A–Z)</option>
          <option value="name-desc">Name (Z–A)</option>
          <option value="value-asc">Value (low–high)</option>
          <option value="value-desc">Value (high–low)</option>
          <option value="rarity">Rarity</option>
        </select>
      </div>
    </div>

    <p v-if="isLoading" class="text-fg-subtle">Loading items...</p>
    <p v-else-if="errorMessage" class="text-danger">{{ errorMessage }}</p>
    <p v-else-if="filteredItems.length === 0" class="text-fg-subtle">
      {{ campaignItems.length === 0 ? 'No items available yet.' : 'No items match your filters.' }}
    </p>
    <template v-else>
      <p class="text-sm text-fg-subtle">
        Showing {{ pageStart }}–{{ pageEnd }} of {{ filteredItems.length }} items
      </p>

      <ItemsListPanel :items="paginatedItems" />

      <div v-if="totalPages > 1" class="flex flex-wrap items-center justify-center gap-3 pt-2">
        <button
          type="button"
          class="rounded-lg border border-border bg-surface-muted px-3 py-1.5 text-sm text-fg-muted transition-colors hover:bg-surface-muted disabled:cursor-not-allowed disabled:opacity-40"
          :disabled="currentPage === 1"
          @click="goToPage(currentPage - 1)"
        >
          Previous
        </button>
        <span class="text-sm text-fg-muted"> Page {{ currentPage }} of {{ totalPages }} </span>
        <button
          type="button"
          class="rounded-lg border border-border bg-surface-muted px-3 py-1.5 text-sm text-fg-muted transition-colors hover:bg-surface-muted disabled:cursor-not-allowed disabled:opacity-40"
          :disabled="currentPage === totalPages"
          @click="goToPage(currentPage + 1)"
        >
          Next
        </button>
      </div>
    </template>
  </div>
</template>
