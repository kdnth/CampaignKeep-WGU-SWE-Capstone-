<script setup lang="ts">
import BackToLink from '@/components/BackToLink.vue'
import BaseTabPanel from '@/components/BaseTabPanel.vue'
import AddCampaignCharacterPanel from '@/components/AddCampaignCharacterPanel.vue'
import CampaignInfoHero from '@/components/campaign/CampaignInfoHero.vue'
import CharacterOverviewPanel from '@/components/character/CharacterOverviewPanel.vue'
import CharacterStatsTab from '@/components/character/CharacterStatsTab.vue'
import CharacterAttacksPanel from '@/components/attacks/CharacterAttacksPanel.vue'
import CharacterEquipmentPanel from '@/components/equipment/CharacterEquipmentPanel.vue'
import StartingEquipmentModal from '@/components/equipment/StartingEquipmentModal.vue'
import PlayerNoteEditor from '@/components/notes/PlayerNoteEditor.vue'
import PlayerSessionLogsPanel from '@/components/sessionlogs/PlayerSessionLogsPanel.vue'
import CharacterSpellbookPanel from '@/components/spells/CharacterSpellbookPanel.vue'
import { useAuthStore } from '@/stores/auth'
import { useCampaignStore } from '@/stores/campaign'
import { useDndClassStore } from '@/stores/dndClass'
import { useInventoryStore } from '@/stores/inventory'
import { isAxiosError } from 'axios'
import { storeToRefs } from 'pinia'
import { computed, onMounted, ref, watch } from 'vue'

const props = defineProps<{
  campaignId: number
}>()

const authStore = useAuthStore()
const campaignStore = useCampaignStore()
const inventoryStore = useInventoryStore()
const dndClassStore = useDndClassStore()
const { activeCampaign, playableCharacters } = storeToRefs(campaignStore)

const isLoading = ref(true)
const errorMessage = ref<string | null>(null)
const showStartingEquipmentModal = ref(false)
const activeTab = ref('stats')

const playerCharacter = computed(() =>
  playableCharacters.value.find((c) => c.ownerId === authStore.userId),
)

const playerClassId = computed(() => {
  const className = playerCharacter.value?.classNames[0]
  if (!className) return null
  return dndClassStore.classes.find((c) => c.name === className)?.id ?? null
})

const tabs = computed(() => [
  { id: 'stats', label: 'Stats', disabled: !playerCharacter.value },
  { id: 'notes', label: 'Notes', disabled: !playerCharacter.value },
  { id: 'spells', label: 'Spells', disabled: !playerCharacter.value },
  { id: 'session-logs', label: 'Session Logs' },
  { id: 'equipment', label: 'Equipment', disabled: !playerCharacter.value },
  { id: 'attacks', label: 'Attacks', disabled: !playerCharacter.value },
  { id: 'roll', label: 'Roll', disabled: true },
])

async function checkStartingEquipment() {
  if (!playerCharacter.value) return
  try {
    const inventory = await inventoryStore.fetchInventory(playerCharacter.value.id)
    if (!inventory.startingEquipmentChosen && playerClassId.value) {
      showStartingEquipmentModal.value = true
    }
    syncCharacterFromInventory(inventory.armorClass, inventory.gold, inventory.startingEquipmentChosen)
  } catch {
    if (!playerCharacter.value.startingEquipmentChosen && playerClassId.value) {
      showStartingEquipmentModal.value = true
    }
  }
}

function syncCharacterFromInventory(armorClass: number, gold: number, startingEquipmentChosen: boolean) {
  if (!playerCharacter.value) return
  const idx = playableCharacters.value.findIndex((c) => c.id === playerCharacter.value!.id)
  if (idx !== -1) {
    playableCharacters.value[idx] = {
      ...playableCharacters.value[idx]!,
      armorClass,
      gold,
      startingEquipmentChosen,
    }
  }
}

function handleEquipmentUpdated() {
  if (!playerCharacter.value || !inventoryStore.inventory) return
  syncCharacterFromInventory(
    inventoryStore.inventory.armorClass,
    inventoryStore.inventory.gold,
    inventoryStore.inventory.startingEquipmentChosen,
  )
}

function handleGoldUpdated(gold: number) {
  if (!playerCharacter.value) return
  const idx = playableCharacters.value.findIndex((c) => c.id === playerCharacter.value!.id)
  if (idx !== -1) {
    playableCharacters.value[idx] = { ...playableCharacters.value[idx]!, gold }
  }
}

async function handleStartingEquipmentCompleted() {
  showStartingEquipmentModal.value = false
  activeTab.value = 'equipment'
  await checkStartingEquipment()
}

onMounted(async () => {
  try {
    await Promise.all([
      campaignStore.fetchPlayableCharacters(props.campaignId),
      dndClassStore.fetchClasses(),
    ])
    await checkStartingEquipment()
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to load campaign characters')
      : 'An unexpected error occurred. Please try again.'
  } finally {
    isLoading.value = false
  }
})

watch(playerCharacter, async (character) => {
  if (character) await checkStartingEquipment()
})
</script>

<template>
  <div class="flex w-full flex-col gap-6 p-8">
    <BackToLink page="campaigns" link-name="Campaigns" />

    <p v-if="isLoading" class="text-neutral-300">Loading campaign...</p>
    <p v-else-if="errorMessage" class="text-red-500">{{ errorMessage }}</p>

    <template v-else-if="activeCampaign">
      <CampaignInfoHero :campaign="activeCampaign" />

      <section v-if="playerCharacter">
        <CharacterOverviewPanel
          :character="playerCharacter"
          :campaign-id="campaignId"
          use-local-hp-for-status
          editable-gold
          :gold="inventoryStore.inventory?.gold ?? playerCharacter.gold ?? 0"
          @gold-updated="handleGoldUpdated"
        />
      </section>
      <section v-else class="rounded-2xl border-2 border-dashed border-neutral-600 p-8 text-center">
        <p class="mb-4 text-neutral-400">No character attached to this campaign.</p>
        <div class="flex justify-center">
          <AddCampaignCharacterPanel :campaign-id="campaignId" character-type="pc" />
        </div>
      </section>

      <BaseTabPanel :tabs="tabs" :default-tab="activeTab">
        <template #stats>
          <CharacterStatsTab
            v-if="playerCharacter"
            :character="playerCharacter"
            :campaign-id="campaignId"
          />
        </template>
        <template #notes>
          <PlayerNoteEditor
            v-if="playerCharacter"
            :campaign-id="campaignId"
            :character-id="playerCharacter.id"
            :character-name="playerCharacter.name"
          />
        </template>
        <template #spells>
          <CharacterSpellbookPanel
            v-if="playerCharacter"
            :character-id="playerCharacter.id"
          />
        </template>
        <template #session-logs>
          <PlayerSessionLogsPanel :campaign-id="campaignId" />
        </template>
        <template #equipment>
          <CharacterEquipmentPanel
            v-if="playerCharacter"
            :character-id="playerCharacter.id"
            :strength="playerCharacter.strength"
            @equipment-updated="handleEquipmentUpdated"
          />
        </template>
        <template #attacks>
          <CharacterAttacksPanel
            v-if="playerCharacter"
            :character-id="playerCharacter.id"
          />
        </template>
        <template #roll>
          <p class="text-neutral-400">Coming soon.</p>
        </template>
      </BaseTabPanel>
    </template>

    <StartingEquipmentModal
      v-if="showStartingEquipmentModal && playerCharacter && playerClassId"
      :character-id="playerCharacter.id"
      :class-id="playerClassId"
      :strength="playerCharacter.strength"
      @completed="handleStartingEquipmentCompleted"
    />
  </div>
</template>
