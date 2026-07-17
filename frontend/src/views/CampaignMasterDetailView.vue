<script setup lang="ts">
import BackToLink from '@/components/BackToLink.vue'
import BaseButton from '@/components/BaseButton.vue'
import BaseTabPanel from '@/components/BaseTabPanel.vue'
import AddCampaignCharacterPanel from '@/components/AddCampaignCharacterPanel.vue'
import CampaignInfoHero from '@/components/campaign/CampaignInfoHero.vue'
import CharacterOverviewPanel from '@/components/character/CharacterOverviewPanel.vue'
import CompactCampaignMemberList from '@/components/CompactCampaignMemberList.vue'
import ConfirmationModal from '@/components/ConfirmationModal.vue'
import MasterNotesPanel from '@/components/notes/MasterNotesPanel.vue'
import MasterSessionLogsPanel from '@/components/sessionlogs/MasterSessionLogsPanel.vue'
import MasterSpellsPanel from '@/components/spells/MasterSpellsPanel.vue'
import GrantItemPanel from '@/components/equipment/GrantItemPanel.vue'
import CampaignItemsPanel from '@/components/equipment/CampaignItemsPanel.vue'
import DiceRollPanel from '@/components/dice/DiceRollPanel.vue'
import { useCampaignStore } from '@/stores/campaign'
import { isAxiosError } from 'axios'
import { storeToRefs } from 'pinia'
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps<{
  campaignId: number
}>()

const router = useRouter()
const campaignStore = useCampaignStore()
const { activeCampaign, members, playableCharacters, nonplayableCharacters } =
  storeToRefs(campaignStore)

const isLoading = ref(true)
const errorMessage = ref<string | null>(null)

const pendingAction = ref<{
  type: 'remove' | 'finish'
  campaignId: number
} | null>(null)

const isConfirming = ref(false)

const confirmTitle = computed(() => {
  switch (pendingAction.value?.type) {
    case 'remove':
      return 'Delete Campaign'
    case 'finish':
      return 'Finish Campaign'
    default:
      return ''
  }
})

const confirmMessage = computed(() => {
  switch (pendingAction.value?.type) {
    case 'remove':
      return 'Are you sure you want to remove all members and delete this campaign? This action cannot be undone.'
    case 'finish':
      return 'Mark this campaign as finished? You can still view it, but it will no longer be active.'
    default:
      return ''
  }
})

const tabs = [
  { id: 'pcs', label: 'Player Characters' },
  { id: 'npcs', label: 'NPCs' },
  { id: 'spells', label: 'Spells' },
  { id: 'notes', label: 'Notes' },
  { id: 'session-logs', label: 'Session Logs' },
  { id: 'members', label: 'Campaign Members' },
  { id: 'campaign-items', label: 'Campaign Items' },
  { id: 'roll', label: 'Roll' },
]

function requestDelete() {
  pendingAction.value = { type: 'remove', campaignId: props.campaignId }
}

function requestFinish() {
  pendingAction.value = { type: 'finish', campaignId: props.campaignId }
}

async function handleConfirm() {
  if (!pendingAction.value) return
  isConfirming.value = true
  try {
    if (pendingAction.value.type === 'remove') {
      await campaignStore.deleteCampaign(props.campaignId)
      router.push({ name: 'campaigns' })
    } else if (pendingAction.value.type === 'finish') {
      await campaignStore.finishCampaign(props.campaignId)
    }
    pendingAction.value = null
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Action failed')
      : 'An unexpected error occured. Please try again.'
  } finally {
    isConfirming.value = false
  }
}

onMounted(async () => {
  try {
    await Promise.all([
      campaignStore.fetchPlayableCharacters(props.campaignId),
      campaignStore.fetchNonplayableCharacters(props.campaignId),
    ])
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to load campaign data')
      : 'An unexpected error occurred. Please try again.'
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <div class="flex w-full flex-col gap-6 p-8">
    <BackToLink page="campaigns" link-name="Campaigns" />

    <p v-if="isLoading" class="text-fg-muted">Loading campaign...</p>
    <p v-else-if="errorMessage && !activeCampaign" class="text-danger">{{ errorMessage }}</p>

    <template v-else-if="activeCampaign">
      <CampaignInfoHero :campaign="activeCampaign" />

      <BaseTabPanel :tabs="tabs" default-tab="pcs">
        <template #pcs>
          <div v-if="playableCharacters.length === 0" class="text-fg-subtle">
            No player characters in this campaign.
          </div>
          <div v-else class="grid gap-4">
            <div v-for="character in playableCharacters" :key="character.id">
              <CharacterOverviewPanel :character="character" show-stats />
              <GrantItemPanel
                :campaign-id="campaignId"
                :character-id="character.id"
                :character-name="character.name"
                character-type="PC"
              />
            </div>
          </div>
        </template>

        <template #npcs>
          <AddCampaignCharacterPanel :campaign-id="campaignId" character-type="npc" />
          <div v-if="nonplayableCharacters.length === 0" class="mt-4 text-fg-subtle">
            No NPCs in this campaign.
          </div>
          <div v-else class="mt-4 grid gap-4">
            <div v-for="character in nonplayableCharacters" :key="character.id">
              <CharacterOverviewPanel
                :character="character"
                show-stats
                :campaign-id="campaignId"
                editable-hp
              />
              <GrantItemPanel
                :campaign-id="campaignId"
                :character-id="character.id"
                :character-name="character.name"
                character-type="NPC"
              />
            </div>
          </div>
        </template>

        <template #notes>
          <MasterNotesPanel :campaign-id="campaignId" />
        </template>

        <template #session-logs>
          <MasterSessionLogsPanel :campaign-id="campaignId" />
        </template>

        <template #spells>
          <MasterSpellsPanel />
        </template>

        <template #members>
          <CompactCampaignMemberList :campaign-id="campaignId" :members="members" />
        </template>
        <template #campaign-items>
          <CampaignItemsPanel :campaign-id="campaignId" />
        </template>
        <template #roll>
          <DiceRollPanel mode="master" />
        </template>
      </BaseTabPanel>

      <div class="flex gap-2">
        <BaseButton variant="danger" @click="requestDelete">Delete Campaign</BaseButton>
        <BaseButton v-if="!activeCampaign.finishedOn" variant="primary" @click="requestFinish">
          Finish Campaign
        </BaseButton>
      </div>

      <p v-if="errorMessage" class="text-danger">{{ errorMessage }}</p>
    </template>

    <ConfirmationModal
      v-if="pendingAction"
      :title="confirmTitle"
      :message="confirmMessage"
      :variant="pendingAction.type === 'remove' ? 'danger' : 'primary'"
      :is-loading="isConfirming"
      @confirm="handleConfirm"
      @cancel="pendingAction = null"
    />
  </div>
</template>
