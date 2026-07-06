<script setup lang="ts">
import BackToLink from '@/components/BackToLink.vue'
import BaseButton from '@/components/BaseButton.vue'
import CompactCampaignMemberList from '@/components/CompactCampaignMemberList.vue'
import ConfirmationModal from '@/components/ConfirmationModal.vue'
import { useCampaignStore } from '@/stores/campaign'
import { isAxiosError } from 'axios'
import { storeToRefs } from 'pinia'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { computed } from 'vue'
import { formatDateStr } from '@/utils/dateUtil'

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

const router = useRouter()
const route = useRoute()
const campaignId = Number(route.params.id)

const campaignStore = useCampaignStore()
const { activeCampaign, members } = storeToRefs(campaignStore)

const isLoading = ref(true)
const errorMessage = ref<string | null>(null)

const pendingAction = ref<{
  type: 'remove' | 'finish'
  campaignId: number
} | null>(null)

const isConfirming = ref(false)

function requestDelete() {
  pendingAction.value = {
    type: 'remove',
    campaignId: campaignId,
  }
}

function requestFinish() {
  pendingAction.value = {
    type: 'finish',
    campaignId: campaignId,
  }
}

async function handleConfirm() {
  if (!pendingAction.value) return
  isConfirming.value = true
  try {
    if (pendingAction.value.type === 'remove') {
      await campaignStore.deleteCampaign(campaignId)
      router.push({ name: 'campaigns' })
    } else if (pendingAction.value.type === 'finish') {
      await campaignStore.finishCampaign(campaignId)
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
    await campaignStore.fetchCampaign(campaignId)
    await campaignStore.fetchMembers(campaignId)
  } catch (err: unknown) {
    if (isAxiosError(err)) {
      errorMessage.value = err.response?.data?.message ?? 'Failed to load campaign'
    } else {
      errorMessage.value = 'An unexpected error occurred. Please try again'
    }
  } finally {
    isLoading.value = false
  }
})
</script>
<template>
  <BackToLink page="campaigns" link-name="Campaigns" />
  <div>
    <p v-if="isLoading" class="text-neutral-300">Loading campaign...</p>
    <p v-else-if="errorMessage" class="text-red-500">{{ errorMessage }}</p>
    <div v-else-if="activeCampaign">
      <h1 class="text-white">{{ activeCampaign.title }}</h1>
      <h2 class="text-white">{{ activeCampaign.callerRole.toUpperCase() }}</h2>
      <h4 class="text-white">{{ formatDateStr(activeCampaign.createdOn) }}</h4>
      <p v-if="activeCampaign.description" class="text-white">{{ activeCampaign.description }}</p>
    </div>
    <div>
      <h4>Campaign Members</h4>
      <CompactCampaignMemberList :campaign-id="campaignId" :members="members" />

      <h6
        v-if="activeCampaign?.finishedOn"
        class="p-8 text-6xl flex justify-center text-neutral-400 font-extralight"
      >
        FINISHED
      </h6>
    </div>

    <div class="flex gap-2">
      <BaseButton
        v-if="activeCampaign?.callerRole === 'master'"
        variant="danger"
        @click="requestDelete"
        >Delete Campaign</BaseButton
      >
      <BaseButton
        v-if="activeCampaign?.callerRole === 'master' && !activeCampaign?.finishedOn"
        variant="primary"
        @click="requestFinish"
        >Finish Campaign</BaseButton
      >
    </div>
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
