<script setup lang="ts">
import CampaignMasterDetailView from '@/views/CampaignMasterDetailView.vue'
import CampaignPlayerDetailView from '@/views/CampaignPlayerDetailView.vue'
import { useCampaignStore } from '@/stores/campaign'
import { isAxiosError } from 'axios'
import { storeToRefs } from 'pinia'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const campaignId = Number(route.params.id)

const campaignStore = useCampaignStore()
const { activeCampaign } = storeToRefs(campaignStore)

const isLoading = ref(true)
const errorMessage = ref<string | null>(null)

onMounted(async () => {
  try {
    await campaignStore.fetchCampaign(campaignId)
    await campaignStore.fetchMembers(campaignId)
  } catch (err: unknown) {
    if (isAxiosError(err)) {
      errorMessage.value = err.response?.data?.message ?? 'Failed to load campaign'
    } else {
      errorMessage.value = 'An unexpected error occurred. Please try again.'
    }
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <p v-if="isLoading" class="p-8 text-neutral-300">Loading campaign...</p>
  <p v-else-if="errorMessage" class="p-8 text-red-500">{{ errorMessage }}</p>
  <CampaignMasterDetailView
    v-else-if="activeCampaign?.callerRole === 'master'"
    :campaign-id="campaignId"
  />
  <CampaignPlayerDetailView v-else-if="activeCampaign" :campaign-id="campaignId" />
</template>
