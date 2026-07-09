<script setup lang="ts">
import { isAxiosError } from 'axios'
import { computed, ref } from 'vue'
import CampaignMemberCard from '@/components/CampaignMemberCard.vue'
import BaseButton from '@/components/BaseButton.vue'
import AddMemberModal from '@/components/AddMemberModal.vue'
import { useAuthStore } from '@/stores/auth'
import { useCampaignStore, type CampaignMemberResponse } from '@/stores/campaign'
import ConfirmationModal from './ConfirmationModal.vue'

const props = defineProps<{
  campaignId: number
  members: CampaignMemberResponse[]
}>()

const campaignStore = useCampaignStore()
const authStore = useAuthStore()

const confirmTitle = computed(() => {
  switch (pendingAction.value?.type) {
    case 'remove':
      return 'Remove Member'
    case 'promote':
      return 'Promote to Master'
    case 'demote':
      return 'Demote to Player'
    case 'leave':
      return 'Leave Campaign'
    default:
      return ''
  }
})

const confirmMessage = computed(() => {
  switch (pendingAction.value?.type) {
    case 'remove':
      return 'Are you sure you want to remove this member from the campaign?'
    case 'promote':
      return 'Promote this player to game master?'
    case 'demote':
      return 'Demote this master to player?'
    case 'leave':
      return 'Are you sure you want to leave this campaign?'
    default:
      return ''
  }
})

const errorMessage = ref<string | null>(null)
const showAddModal = ref(false)

const pendingAction = ref<{
  type: 'remove' | 'promote' | 'demote' | 'leave'
  userId: number
} | null>(null)
const isConfirming = ref(false)

function requestRemove(userId: number) {
  pendingAction.value = { type: 'remove', userId }
}

function requestChangeRole(userId: number, newRole: 'master' | 'player') {
  pendingAction.value = {
    type: newRole === 'master' ? 'promote' : 'demote',
    userId,
  }
}

async function handleConfirm() {
  if (!pendingAction.value) return
  const targetUserId = pendingAction.value.userId
  const actionType = pendingAction.value.type
  isConfirming.value = true
  try {
    if (actionType === 'remove' || actionType === 'leave') {
      await campaignStore.removeMember(props.campaignId, targetUserId)
    } else {
      const role = actionType === 'promote' ? 'master' : 'player'
      await campaignStore.changeRole(props.campaignId, targetUserId, { role })
      if (targetUserId === authStore.userId && actionType === 'demote') {
        await campaignStore.fetchCampaign(props.campaignId)
      }
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
</script>

<template>
  <div class="flex flex-col gap-3">
    <div class="flex items-center justify-between">
      <h3 class="text-white text-lg">Members</h3>
      <BaseButton variant="primary" class="text-sm" @click="showAddModal = true"
        >Add Member</BaseButton
      >
    </div>

    <p v-if="errorMessage" class="text-red-500 text-sm">{{ errorMessage }}</p>

    <CampaignMemberCard
      v-for="member in members"
      :key="member.userId"
      :member="member"
      @remove="requestRemove"
      @change-role="requestChangeRole"
    />

    <ConfirmationModal
      v-if="pendingAction"
      :title="confirmTitle"
      :message="confirmMessage"
      :variant="
        pendingAction.type === 'remove' || pendingAction.type === 'leave' ? 'danger' : 'primary'
      "
      :is-loading="isConfirming"
      @confirm="handleConfirm"
      @cancel="pendingAction = null"
    />

    <AddMemberModal v-if="showAddModal" :campaign-id="campaignId" @close="showAddModal = false" />
  </div>
</template>
