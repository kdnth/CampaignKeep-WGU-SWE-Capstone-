<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { isAxiosError } from 'axios'
import { useCampaignStore } from '@/stores/campaign'
import CampaignCard from '@/components/CampaignCard.vue'
import { useRouter } from 'vue-router'

const campaignStore = useCampaignStore()
const { campaigns } = storeToRefs(campaignStore)
const router = useRouter()

const isLoading = ref(true)
const errorMessage = ref<string | null>(null)

onMounted(async () => {
  try {
    await campaignStore.fetchCampaigns()
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to load campaigns')
      : 'An unexpected error occurred. Please try again.'
  } finally {
    isLoading.value = false
  }
})

function handleClick(id: number) {
  router.push({ name: 'campaignDetail', params: { id } })
}
</script>

<template>
  <div class="flex flex-col gap-3">
    <div class="flex items-center justify-between">
      <h3 class="text-white text-lg">Your Campaigns</h3>
      <RouterLink :to="{ name: 'campaigns' }" class="text-sm text-purple-400 hover:underline">
        View all
      </RouterLink>
    </div>

    <p v-if="isLoading" class="text-neutral-300">Loading campaigns...</p>
    <p v-else-if="errorMessage" class="text-red-500">{{ errorMessage }}</p>
    <p v-else-if="campaigns.length === 0" class="text-neutral-300">
      No campaigns yet. Create one to get started.
    </p>

    <div
      class="grid grid-cols-[repeat(auto-fill,minmax(18rem,1fr))] grid-rows-[repeat(1, 1fr)] max-h-64 gap-4 overflow-hidden"
    >
      <CampaignCard
        v-for="campaign in campaigns"
        :key="campaign.id"
        :title="campaign.title"
        :created-on="campaign.createdOn"
        :finished-on="campaign?.finishedOn || null"
        :description="campaign.description"
        :role="campaign.callerRole"
        class="shrink-0 w-72"
        @click="handleClick(campaign.id)"
      />
    </div>
  </div>
</template>
