<script setup lang="ts">
import SpellFlipCard from '@/components/spells/SpellFlipCard.vue'
import {
  type MagicSchool,
  type SpellDetailResponse,
  type SpellSummaryResponse,
  useSpellStore,
} from '@/stores/spell'
import { isAxiosError } from 'axios'
import { computed, onMounted, ref, watch } from 'vue'

export interface SpellQuotas {
  cantrips: number
  levelOne: number
}

const props = withDefaults(
  defineProps<{
    mode?: 'browse' | 'spellbook' | 'select'
    characterId?: number
    classIndex?: string
    maxLevel?: number
    selectedIds?: number[]
    quotas?: SpellQuotas
    showToolbar?: boolean
    title?: string
    readonly?: boolean
    spellbookTabLabel?: string
  }>(),
  {
    mode: 'browse',
    selectedIds: () => [],
    showToolbar: true,
    readonly: false,
    spellbookTabLabel: 'My Spellbook',
  },
)

const emit = defineEmits<{
  'update:selectedIds': [ids: number[]]
}>()

const spellStore = useSpellStore()

const isLoading = ref(true)
const errorMessage = ref<string | null>(null)
const searchQuery = ref('')
const levelFilter = ref<number | 'all'>('all')
const schoolFilter = ref<MagicSchool | 'all'>('all')
const sortOrder = ref<'name' | 'level'>('level')
const showAll = ref(false)
const detailCache = ref<Record<number, SpellDetailResponse>>({})
const loadingSpellIds = ref<Set<number>>(new Set())
const actionLoadingId = ref<number | null>(null)
const viewMode = ref<'spellbook' | 'browse'>('spellbook')
const currentPage = ref(1)
const pageSize = 12

const schools: MagicSchool[] = [
  'abjuration',
  'conjuration',
  'divination',
  'enchantment',
  'evocation',
  'illusion',
  'necromancy',
  'transmutation',
]

const selectedIdSet = computed(() => new Set(props.selectedIds))

const cantripCount = computed(
  () =>
    props.selectedIds.filter((id) => {
      const spell = spellStore.spells.find((s) => s.id === id)
      return spell?.level === 0
    }).length,
)

const levelOneCount = computed(
  () =>
    props.selectedIds.filter((id) => {
      const spell = spellStore.spells.find((s) => s.id === id)
      return spell?.level === 1
    }).length,
)

const spellbookIds = computed(() => new Set(spellStore.characterSpells.map((s) => s.id)))

const displaySpells = computed(() => {
  if (props.mode === 'spellbook' && viewMode.value === 'spellbook') {
    return spellStore.characterSpells
  }
  return spellStore.spells
})

const filteredSpells = computed(() => {
  let result: (SpellSummaryResponse | SpellDetailResponse)[] = [...displaySpells.value]

  if (!showAll.value && props.classIndex && props.mode !== 'spellbook') {
    // Class filter applied server-side on fetch; client show-all bypasses re-fetch
  }

  if (searchQuery.value.trim()) {
    const query = searchQuery.value.trim().toLowerCase()
    result = result.filter((spell) => spell.name.toLowerCase().includes(query))
  }

  if (levelFilter.value !== 'all') {
    result = result.filter((spell) => spell.level === levelFilter.value)
  }

  if (schoolFilter.value !== 'all') {
    result = result.filter((spell) => spell.school === schoolFilter.value)
  }

  result = [...result].sort((a, b) => {
    if (sortOrder.value === 'name') {
      return a.name.localeCompare(b.name)
    }
    const levelCompare = a.level - b.level
    return levelCompare !== 0 ? levelCompare : a.name.localeCompare(b.name)
  })

  return result
})

const totalPages = computed(() => Math.max(1, Math.ceil(filteredSpells.value.length / pageSize)))

const paginatedSpells = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredSpells.value.slice(start, start + pageSize)
})

const pageStart = computed(() =>
  filteredSpells.value.length === 0 ? 0 : (currentPage.value - 1) * pageSize + 1,
)

const pageEnd = computed(() =>
  Math.min(currentPage.value * pageSize, filteredSpells.value.length),
)

const cardMode = computed(() => {
  if (props.readonly || props.mode === 'browse') return 'browse'
  if (props.mode === 'select') return 'select'
  return 'spellbook'
})

function goToPage(page: number) {
  currentPage.value = Math.min(Math.max(1, page), totalPages.value)
}

function getSpellForCard(spell: SpellSummaryResponse | SpellDetailResponse) {
  return detailCache.value[spell.id] ?? spell
}

async function ensureSpellDetail(spellId: number) {
  if (detailCache.value[spellId] || loadingSpellIds.value.has(spellId)) {
    return
  }
  loadingSpellIds.value.add(spellId)
  try {
    const detail = await spellStore.fetchSpell(spellId)
    detailCache.value[spellId] = detail
  } finally {
    loadingSpellIds.value.delete(spellId)
  }
}

async function loadSpells() {
  isLoading.value = true
  errorMessage.value = null
  try {
    const params: Record<string, string | number> = {}
    if (!showAll.value && props.classIndex) {
      params.classIndex = props.classIndex
    }
    if (!showAll.value && props.maxLevel !== undefined) {
      params.maxLevel = props.maxLevel
    }
    await spellStore.fetchSpells(params)
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to load spells')
      : 'An unexpected error occurred. Please try again.'
  } finally {
    isLoading.value = false
  }
}

async function loadCharacterSpells() {
  if (!props.characterId) return
  try {
    await spellStore.fetchCharacterSpells(props.characterId)
    for (const spell of spellStore.characterSpells) {
      detailCache.value[spell.id] = spell
    }
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to load spellbook')
      : 'An unexpected error occurred. Please try again.'
  }
}

async function handleCardFlip(spell: SpellSummaryResponse | SpellDetailResponse) {
  await ensureSpellDetail(spell.id)
}

function toggleSelect(spellId: number) {
  const spell = spellStore.spells.find((s) => s.id === spellId)
  if (!spell) return

  const next = [...props.selectedIds]
  const index = next.indexOf(spellId)

  if (index !== -1) {
    next.splice(index, 1)
    emit('update:selectedIds', next)
    return
  }

  if (props.quotas) {
    if (spell.level === 0 && cantripCount.value >= props.quotas.cantrips) return
    if (spell.level === 1 && levelOneCount.value >= props.quotas.levelOne) return
    if (spell.level > 1) return
  }

  next.push(spellId)
  emit('update:selectedIds', next)
}

function isSelectDisabled(spell: SpellSummaryResponse | SpellDetailResponse): boolean {
  if (props.mode !== 'select' || !props.quotas) return false
  if (selectedIdSet.value.has(spell.id)) return false
  if (spell.level === 0) return cantripCount.value >= props.quotas.cantrips
  if (spell.level === 1) return levelOneCount.value >= props.quotas.levelOne
  return true
}

async function handleAdd(spellId: number) {
  if (!props.characterId) return
  actionLoadingId.value = spellId
  errorMessage.value = null
  try {
    await spellStore.addSpellToCharacter(props.characterId, spellId)
    await ensureSpellDetail(spellId)
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to add spell')
      : 'An unexpected error occurred. Please try again.'
  } finally {
    actionLoadingId.value = null
  }
}

async function handleRemove(spellId: number) {
  if (!props.characterId) return
  actionLoadingId.value = spellId
  errorMessage.value = null
  try {
    await spellStore.removeSpellFromCharacter(props.characterId, spellId)
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to remove spell')
      : 'An unexpected error occurred. Please try again.'
  } finally {
    actionLoadingId.value = null
  }
}

watch(showAll, () => {
  currentPage.value = 1
  if (props.mode !== 'spellbook' || viewMode.value === 'browse') {
    loadSpells()
  }
})

watch(viewMode, async (mode) => {
  currentPage.value = 1
  if (mode === 'browse') {
    await loadSpells()
  }
})

watch([searchQuery, levelFilter, schoolFilter, sortOrder], () => {
  currentPage.value = 1
})

watch(totalPages, (pages) => {
  if (currentPage.value > pages) {
    currentPage.value = pages
  }
})

watch(
  () => props.characterId,
  async (characterId) => {
    if (characterId && props.mode === 'spellbook') {
      detailCache.value = {}
      currentPage.value = 1
      viewMode.value = 'spellbook'
      await loadCharacterSpells()
    }
  },
)

onMounted(async () => {
  if (props.mode === 'spellbook') {
    await loadCharacterSpells()
    if (spellStore.characterSpells.length === 0) {
      viewMode.value = 'browse'
      await loadSpells()
    }
  } else {
    await loadSpells()
  }
})
</script>

<template>
  <div class="flex flex-col gap-4">
    <div v-if="title" class="flex flex-wrap items-center justify-between gap-3">
      <h2 class="text-xl text-fg">{{ title }}</h2>
      <div v-if="mode === 'spellbook'" class="flex gap-2">
        <button
          type="button"
          class="rounded-lg px-3 py-1 text-sm transition-colors"
          :class="
            viewMode === 'spellbook'
              ? 'bg-accent text-white'
              : 'bg-surface-muted text-fg-muted hover:text-fg'
          "
          @click="viewMode = 'spellbook'"
        >
          {{ spellbookTabLabel }}
        </button>
        <button
          type="button"
          class="rounded-lg px-3 py-1 text-sm transition-colors"
          :class="
            viewMode === 'browse'
              ? 'bg-accent text-white'
              : 'bg-surface-muted text-fg-muted hover:text-fg'
          "
          @click="viewMode = 'browse'"
        >
          Browse Spells
        </button>
      </div>
    </div>

    <div
      v-if="mode === 'select' && quotas"
      class="rounded-xl border border-border bg-surface p-4 text-sm text-fg-muted"
    >
      <p>
        Cantrips: {{ cantripCount }} / {{ quotas.cantrips }} · Level 1 spells: {{ levelOneCount }} /
        {{ quotas.levelOne }}
      </p>
    </div>

    <div v-if="showToolbar" class="flex flex-wrap items-end gap-4">
      <div class="flex min-w-48 flex-1 flex-col gap-1">
        <label class="text-sm text-fg-muted">Search</label>
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Search by name..."
          class="rounded-lg border border-border bg-surface-muted px-3 py-2 text-fg"
        />
      </div>

      <div class="flex flex-col gap-1">
        <label class="text-sm text-fg-muted">Level</label>
        <select
          v-model="levelFilter"
          class="rounded-lg border border-border bg-surface-muted px-3 py-2 text-fg"
        >
          <option value="all">All</option>
          <option :value="0">Cantrip</option>
          <option v-for="n in 9" :key="n" :value="n">Level {{ n }}</option>
        </select>
      </div>

      <div class="flex flex-col gap-1">
        <label class="text-sm text-fg-muted">School</label>
        <select
          v-model="schoolFilter"
          class="rounded-lg border border-border bg-surface-muted px-3 py-2 capitalize text-fg"
        >
          <option value="all">All</option>
          <option v-for="school in schools" :key="school" :value="school">
            {{ school }}
          </option>
        </select>
      </div>

      <div class="flex flex-col gap-1">
        <label class="text-sm text-fg-muted">Sort</label>
        <select
          v-model="sortOrder"
          class="rounded-lg border border-border bg-surface-muted px-3 py-2 text-fg"
        >
          <option value="level">Level</option>
          <option value="name">Name</option>
        </select>
      </div>

      <label
        v-if="classIndex && mode !== 'spellbook'"
        class="flex items-center gap-2 pb-2 text-sm text-fg-muted"
      >
        <input v-model="showAll" type="checkbox" class="rounded" />
        Show all
      </label>
    </div>

    <p v-if="spellStore.isSyncing" class="text-sm text-fg-subtle">
      Syncing spell catalog. This may take a moment...
    </p>
    <p v-if="isLoading" class="text-fg-muted">Loading spells...</p>
    <p v-else-if="errorMessage" class="text-danger">{{ errorMessage }}</p>
    <p v-else-if="filteredSpells.length === 0" class="text-fg-subtle">
      No spells match your filters.
    </p>

    <template v-else>
      <p class="text-sm text-fg-subtle">
        Showing {{ pageStart }}–{{ pageEnd }} of {{ filteredSpells.length }} spells
      </p>

      <div class="grid grid-cols-[repeat(auto-fill,minmax(16rem,1fr))] gap-4">
        <div v-for="spell in paginatedSpells" :key="spell.id" @click="handleCardFlip(spell)">
          <SpellFlipCard
            :spell="getSpellForCard(spell)"
            :mode="cardMode"
            :is-in-spellbook="
              mode === 'spellbook' && viewMode === 'spellbook' ? true : spellbookIds.has(spell.id)
            "
            :is-selected="selectedIdSet.has(spell.id)"
            :is-disabled="isSelectDisabled(spell)"
            :is-loading-action="actionLoadingId === spell.id"
            @toggle-select="toggleSelect(spell.id)"
            @add="handleAdd(spell.id)"
            @remove="handleRemove(spell.id)"
          />
        </div>
      </div>

      <div
        v-if="totalPages > 1"
        class="flex flex-wrap items-center justify-center gap-3 pt-2"
      >
        <button
          type="button"
          class="rounded-lg border border-border bg-surface-muted px-3 py-1.5 text-sm text-fg-muted transition-colors hover:bg-surface-muted disabled:cursor-not-allowed disabled:opacity-40"
          :disabled="currentPage === 1"
          @click="goToPage(currentPage - 1)"
        >
          Previous
        </button>
        <span class="text-sm text-fg-muted">
          Page {{ currentPage }} of {{ totalPages }}
        </span>
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
