<script setup lang="ts">
import BackToLink from '@/components/BackToLink.vue'
import BaseTabPanel from '@/components/BaseTabPanel.vue'
import AddCampaignCharacterPanel from '@/components/AddCampaignCharacterPanel.vue'
import CampaignInfoHero from '@/components/campaign/CampaignInfoHero.vue'
import CharacterOverviewPanel from '@/components/character/CharacterOverviewPanel.vue'
import CharacterStatsTab from '@/components/character/CharacterStatsTab.vue'
import PlayerNoteEditor from '@/components/notes/PlayerNoteEditor.vue'
import PlayerSessionLogsPanel from '@/components/sessionlogs/PlayerSessionLogsPanel.vue'
import CharacterSpellbookPanel from '@/components/spells/CharacterSpellbookPanel.vue'
import { useAuthStore } from '@/stores/auth'
import { useCampaignStore } from '@/stores/campaign'
import { isAxiosError } from 'axios'
import { storeToRefs } from 'pinia'
import { computed, onMounted, ref } from 'vue'

const props = defineProps<{
  campaignId: number
}>()

const authStore = useAuthStore()
const campaignStore = useCampaignStore()
const { activeCampaign, playableCharacters } = storeToRefs(campaignStore)

const isLoading = ref(true)
const errorMessage = ref<string | null>(null)

const playerCharacter = computed(() =>
  playableCharacters.value.find((c) => c.ownerId === authStore.userId),
)

const tabs = computed(() => [
  { id: 'stats', label: 'Stats', disabled: !playerCharacter.value },
  { id: 'notes', label: 'Notes', disabled: !playerCharacter.value },
  { id: 'spells', label: 'Spells', disabled: !playerCharacter.value },
  { id: 'session-logs', label: 'Session Logs' },
  { id: 'equipment', label: 'Equipment', disabled: true },
  { id: 'attacks', label: 'Attacks', disabled: true },
  { id: 'roll', label: 'Roll', disabled: true },
])

onMounted(async () => {
  try {
    await campaignStore.fetchPlayableCharacters(props.campaignId)
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to load campaign characters')
      : 'An unexpected error occurred. Please try again.'
  } finally {
    isLoading.value = false
  }
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
        />
      </section>
      <section v-else class="rounded-2xl border-2 border-dashed border-neutral-600 p-8 text-center">
        <p class="mb-4 text-neutral-400">No character attached to this campaign.</p>
        <div class="flex justify-center">
          <AddCampaignCharacterPanel :campaign-id="campaignId" character-type="pc" />
        </div>
      </section>

      <BaseTabPanel :tabs="tabs" default-tab="stats">
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
          <p class="text-neutral-400">Coming soon.</p>
        </template>
        <template #attacks>
          <p class="text-neutral-400">Coming soon.</p>
        </template>
        <template #roll>
          <p class="text-neutral-400">Coming soon.</p>
        </template>
      </BaseTabPanel>
    </template>
  </div>
</template>
