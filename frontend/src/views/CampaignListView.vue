<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import CampaignCard from '@/components/CampaignCard.vue'
import { useCampaignStore } from '@/stores/campaign'
import { isAxiosError } from 'axios'
import { computed, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import BackToLink from '@/components/BackToLink.vue'

const campaignStore = useCampaignStore()
const { campaigns } = storeToRefs(campaignStore)
const router = useRouter()

const isLoading = ref(true)
const errorMessage = ref<string | null>(null)

const searchQuery = ref('')
const statusFilter = ref<'all' | 'active' | 'finished'>('all')
const roleFilter = ref<'all' | 'master' | 'player'>('all')
const sortOrder = ref<'newest' | 'oldest'>('newest')

const filteredCampaigns = computed(() => {
  let result = campaigns.value

  if (searchQuery.value.trim() !== '') {
    const query = searchQuery.value.trim().toLowerCase()
    result = result.filter((c) => c.title.toLowerCase().includes(query))
  }
  if (statusFilter.value === 'active') {
    result = result.filter((c) => c.finishedOn === null)
  } else if (statusFilter.value === 'finished') {
    result = result.filter((c) => c.finishedOn !== null)
  }

  if (roleFilter.value !== 'all') {
    result = result.filter((c) => c.callerRole === roleFilter.value)
  }

  result = [...result].sort((a, b) => {
    const dateA = new Date(a.createdOn).getTime()
    const dateB = new Date(b.createdOn).getTime()
    return sortOrder.value === 'newest' ? dateB - dateA : dateA - dateB
  })

  return result
})

onMounted(async () => {
  errorMessage.value = null
  isLoading.value = true

  try {
    await campaignStore.fetchCampaigns()
  } catch (err: unknown) {
    if (isAxiosError(err)) {
      errorMessage.value =
        err.response?.data?.message ??
        `Failed to load campaigns (${err.response?.status ?? 'network error'})`
    } else {
      errorMessage.value = 'Failed to load campaigns. Please try again.'
    }
  } finally {
    isLoading.value = false
  }
})

function handleClick(id: number) {
  router.push({
    name: 'campaignDetail',
    params: { id: id },
  })
}
</script>
<template>
  <div class="flex w-full flex-col gap-4 p-8">
    <BackToLink page="home" link-name="Home" />
    <p v-if="isLoading" class="text-neutral-300">Loading campaigns...</p>
    <p v-else-if="errorMessage" class="text-red-500">{{ errorMessage }}</p>
    <p v-else-if="campaigns.length === 0" class="text-neutral-300">
      No campaigns yet. Create one to get started.
    </p>

    <div class="flex gap-4 flex-wrap">
      <input
        id="search-query"
        v-model="searchQuery"
        type="text"
        placeholder="Search campaigns..."
        class="rounded-lg px-3 py-2"
      />

      <div class="relative">
        <select
          id="status-filter"
          v-model="statusFilter"
          class="appearance-none rounded-lg pl-3 pr-8 py-2 w-full"
        >
          <option value="all">All statuses</option>
          <option value="active">Active</option>
          <option value="finished">Finished</option>
        </select>
      </div>

      <div class="relative">
        <select
          id="role-filter"
          v-model="roleFilter"
          class="appearance-none rounded-lg pl-3 pr-8 py-2 w-full"
        >
          <option value="all">All roles</option>
          <option value="master">Master</option>
          <option value="player">Player</option>
        </select>
      </div>

      <div class="relative">
        <select
          id="sort-order"
          v-model="sortOrder"
          class="appearance-none rounded-lg pl-3 pr-8 py-2 w-full"
        >
          <option value="newest">Newest first</option>
          <option value="oldest">Oldest first</option>
        </select>
      </div>
    </div>

    <div class="grid grid-cols-[repeat(auto-fill,minmax(18rem,1fr))] gap-4">
      <CampaignCard
        v-for="campaign in filteredCampaigns"
        :key="campaign.id"
        :title="campaign.title"
        :created-on="campaign.createdOn"
        :finished-on="campaign?.finishedOn || null"
        :description="campaign.description"
        :role="campaign.callerRole"
        @click="handleClick(campaign.id)"
      />
    </div>
    <BaseButton variant="primary" @click="router.push({ name: 'createCampaign' })"
      >Create New</BaseButton
    >
  </div>
</template>
