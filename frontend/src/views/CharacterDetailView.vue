<script setup lang="ts">
import BackToLink from '@/components/BackToLink.vue'
import BaseButton from '@/components/BaseButton.vue'
import CharacterAvatar from '@/components/CharacterAvatar.vue'
import CharacterStatusBadge from '@/components/character/CharacterStatusBadge.vue'
import ConfirmationModal from '@/components/ConfirmationModal.vue'
import SpellFlipCard from '@/components/spells/SpellFlipCard.vue'
import ItemsListPanel from '@/components/equipment/ItemsListPanel.vue'
import { useCharacterStore } from '@/stores/character'
import { useSpellStore } from '@/stores/spell'
import { useInventoryStore } from '@/stores/inventory'
import { formatDateStr } from '@/utils/dateUtil'
import { isAxiosError } from 'axios'
import { storeToRefs } from 'pinia'
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import StatLabel from '@/components/character/StatLabel.vue'

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
const spellStore = useSpellStore()
const inventoryStore = useInventoryStore()
const { activeCharacter, characterCampaigns } = storeToRefs(characterStore)

const isLoading = ref(true)
const errorMessage = ref<string | null>(null)
const showDeleteConfirm = ref(false)
const isDeleting = ref(false)
const isLoadingSpells = ref(false)
const isLoadingInventory = ref(false)

const inventoryEntries = computed(() =>
  (inventoryStore.inventory?.items ?? []).map((entry) => ({
    key: entry.inventoryItemId,
    item: entry.item,
    quantity: entry.quantity,
  })),
)

const isPlayable = computed(() => activeCharacter.value?.characterType === 'PC')
const typeLabel = computed(() => (isPlayable.value ? 'Playable Character' : 'NPC'))
const classLabel = computed(() => {
  if (!activeCharacter.value) return 'Not assigned'
  if (activeCharacter.value.characterType === 'NPC') return 'NPC'
  return activeCharacter.value.classNames[0] ?? 'Not assigned'
})

const showSpellbook = computed(() => {
  if (!isPlayable.value || !activeCharacter.value) return false
  const isWizard = activeCharacter.value.classNames.some((name) => name.toLowerCase() === 'wizard')
  const isHighElf = activeCharacter.value.subraceName?.toLowerCase() === 'high elf'
  return isWizard || isHighElf
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
    await Promise.all([
      characterStore.fetchCharacter(characterId),
      characterStore.fetchCharacterCampaigns(characterId),
    ])

    isLoadingInventory.value = true
    try {
      await inventoryStore.fetchInventory(characterId)
    } catch {
      // inventory may be empty
    } finally {
      isLoadingInventory.value = false
    }

    const character = activeCharacter.value
    if (character?.characterType === 'PC') {
      const isWizard = character.classNames.some((name) => name.toLowerCase() === 'wizard')
      const isHighElf = character.subraceName?.toLowerCase() === 'high elf'
      if (isWizard || isHighElf) {
        isLoadingSpells.value = true
        try {
          await spellStore.fetchCharacterSpells(characterId)
        } catch {
          // spellbook may be empty for new characters
        } finally {
          isLoadingSpells.value = false
        }
      }
    }
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

    <p v-if="isLoading" class="text-fg-muted">Loading character...</p>
    <p v-else-if="errorMessage && !activeCharacter" class="text-danger">{{ errorMessage }}</p>

    <template v-else-if="activeCharacter">
      <div class="flex flex-wrap items-start gap-6">
        <div class="flex flex-col items-center">
          <div class="rounded-xl bg-accent p-4">
            <CharacterAvatar
              :race-name="activeCharacter.raceName"
              :class-name="classLabel"
              size="lg"
            />
          </div>
        </div>
        <div class="flex flex-1 flex-wrap items-start justify-between gap-4">
          <div>
            <p class="text-sm uppercase tracking-wide text-accent">{{ typeLabel }}</p>
            <h1 class="text-3xl text-fg">
              {{ activeCharacter.name }}
            </h1>
            <p class="text-fg-muted capitalize">
              {{ activeCharacter.raceName }}
              <span v-if="activeCharacter.subraceName">({{ activeCharacter.subraceName }})</span>
              <span v-if="activeCharacter.classNames.length > 0">
                • {{ activeCharacter.classNames.join(', ') }}</span
              >
              <span> • {{ activeCharacter.backgroundName }}</span>
            </p>
            <p class="text-fg-muted capitalize italic">
              {{ activeCharacter.languageNames.join(', ') || '' }}
            </p>
            <div
              class="text-md my-1 py-1 border-t border-border-strong flex justify-between items-start"
            >
              <StatLabel stat="hp" :value="activeCharacter.hitPoints"> </StatLabel>
              <StatLabel stat="ac" :value="activeCharacter.armorClass" />
              <StatLabel stat="initiative" :value="activeCharacter.initiativeBonus" />
              <StatLabel stat="speed" :value="activeCharacter.speed" />
            </div>
          </div>
          <CharacterStatusBadge :status="activeCharacter.status" />
        </div>
      </div>

      <section class="rounded-2xl border-2 border-border-strong p-6 text-fg">
        <h2 class="mb-4 text-xl">Ability Scores</h2>
        <div class="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-6">
          <div v-for="ability in abilities" :key="ability" class="text-center">
            <p class="text-sm uppercase text-fg-muted">{{ ability.slice(0, 3) }}</p>
            <p class="text-2xl">{{ activeCharacter[ability] }}</p>
            <p class="text-sm text-fg-subtle">
              {{ formatModifier(Math.floor((activeCharacter[ability] - 10) / 2)) }}
            </p>
          </div>
        </div>
      </section>

      <section class="rounded-2xl border-2 border-border-strong p-6 text-fg">
        <h2 class="mb-4 text-xl">Inventory</h2>
        <p v-if="isLoadingInventory" class="text-fg-muted">Loading inventory...</p>
        <ItemsListPanel v-else :entries="inventoryEntries" empty-message="No items in inventory." />
        <p v-if="inventoryStore.inventory" class="mt-3 text-sm text-fg-subtle">
          Weight: {{ inventoryStore.inventory.totalWeight }} /
          {{ inventoryStore.inventory.carryingCapacity }} lb. · Gold:
          {{ inventoryStore.inventory.gold }} gp
        </p>
      </section>

      <section v-if="showSpellbook" class="rounded-2xl border-2 border-border-strong p-6 text-fg">
        <h2 class="mb-4 text-xl">Spellbook</h2>
        <p v-if="isLoadingSpells" class="text-fg-muted">Loading spellbook...</p>
        <p v-else-if="spellStore.characterSpells.length === 0" class="text-fg-subtle">
          No spells in spellbook yet.
        </p>
        <div v-else class="grid grid-cols-[repeat(auto-fill,minmax(16rem,1fr))] gap-4">
          <SpellFlipCard
            v-for="spell in spellStore.characterSpells"
            :key="spell.id"
            :spell="spell"
            mode="browse"
          />
        </div>
      </section>

      <section class="rounded-2xl border-2 border-border-strong p-6 text-fg">
        <h2 class="mb-2 text-xl">Campaigns</h2>
        <p v-if="characterCampaigns.length === 0" class="text-fg-muted">
          Not assigned to any campaigns.
        </p>
        <ul v-else class="flex flex-wrap gap-2">
          <li v-for="campaign in characterCampaigns" :key="campaign.id">
            <RouterLink
              :to="{ name: 'campaignDetail', params: { id: campaign.id } }"
              class="rounded-lg bg-accent-muted px-3 py-1 text-sm font-semibold text-accent hover:bg-accent-hover"
            >
              {{ campaign.title }}
            </RouterLink>
          </li>
        </ul>
      </section>

      <p class="text-sm text-fg-subtle">
        Created {{ formatDateStr(activeCharacter.createdOn) }} · Updated
        {{ formatDateStr(activeCharacter.updatedOn) }}
      </p>

      <div class="flex gap-2">
        <BaseButton variant="danger" @click="requestDelete">Delete Character</BaseButton>
      </div>

      <p v-if="errorMessage" class="text-danger">{{ errorMessage }}</p>
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
