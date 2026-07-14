<script setup lang="ts">
import CreateCampaignItemModal from '@/components/equipment/CreateCampaignItemModal.vue'
import ItemsListPanel from '@/components/equipment/ItemsListPanel.vue'
import { useInventoryStore } from '@/stores/inventory'
import { isAxiosError } from 'axios'
import { storeToRefs } from 'pinia'
import { onMounted, ref } from 'vue'

const props = defineProps<{
  campaignId: number
}>()

const inventoryStore = useInventoryStore()
const { campaignItems } = storeToRefs(inventoryStore)

const isLoading = ref(true)
const errorMessage = ref<string | null>(null)

async function loadItems() {
  isLoading.value = true
  errorMessage.value = null
  try {
    await inventoryStore.fetchCampaignItems(props.campaignId)
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to load campaign items')
      : 'Failed to load campaign items'
  } finally {
    isLoading.value = false
  }
}

onMounted(loadItems)
</script>

<template>
  <div class="space-y-4">
    <div class="flex flex-wrap items-center justify-between gap-3">
      <p class="text-sm text-neutral-400">
        Reference gear available to this campaign plus any custom items you create.
      </p>
      <CreateCampaignItemModal :campaign-id="campaignId" @created="loadItems" />
    </div>

    <p v-if="isLoading" class="text-neutral-400">Loading items...</p>
    <p v-else-if="errorMessage" class="text-red-400">{{ errorMessage }}</p>
    <ItemsListPanel v-else :items="campaignItems" empty-message="No items available yet." />
  </div>
</template>
