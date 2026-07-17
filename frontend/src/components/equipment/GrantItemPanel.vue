<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import { useInventoryStore } from '@/stores/inventory'
import { isAxiosError } from 'axios'
import { computed, onMounted, ref } from 'vue'

const props = defineProps<{
  campaignId: number
  characterId: number
  characterName: string
  characterType?: 'PC' | 'NPC'
}>()

const inventoryStore = useInventoryStore()
const selectedItemId = ref<number | null>(null)
const quantity = ref(1)
const isGranting = ref(false)
const errorMessage = ref<string | null>(null)
const successMessage = ref<string | null>(null)

const items = computed(() => inventoryStore.campaignItems)

async function handleGrant() {
  if (!selectedItemId.value) return
  isGranting.value = true
  errorMessage.value = null
  successMessage.value = null
  try {
    await inventoryStore.grantItem(
      props.campaignId,
      props.characterId,
      selectedItemId.value,
      quantity.value,
      props.characterType ?? 'PC',
    )
    successMessage.value = `Item granted to ${props.characterName}.`
    selectedItemId.value = null
    quantity.value = 1
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to grant item')
      : 'Failed to grant item'
  } finally {
    isGranting.value = false
  }
}

onMounted(() => {
  inventoryStore.fetchCampaignItems(props.campaignId)
})
</script>

<template>
  <div class="mt-3 rounded-lg border border-border p-3 text-sm text-fg">
    <p class="mb-2 font-medium">Grant item to {{ characterName }}</p>
    <div class="flex flex-wrap items-end gap-3">
      <div>
        <label class="text-fg-subtle">Item</label>
        <select
          v-model="selectedItemId"
          class="bg-input block min-w-48 rounded-lg px-3 py-2 text-fg"
        >
          <option :value="null">Select item...</option>
          <option v-for="item in items" :key="item.id" :value="item.id">
            {{ item.name }} ({{ item.itemType }})
          </option>
        </select>
      </div>
      <div>
        <label class="text-fg-subtle">Qty</label>
        <input
          v-model.number="quantity"
          type="number"
          min="1"
          class="bg-input block w-20 rounded-lg px-3 py-2 text-fg"
        />
      </div>
      <BaseButton
        variant="primary"
        :disabled="!selectedItemId"
        :loading="isGranting"
        @click="handleGrant"
      >
        Grant
      </BaseButton>
    </div>
    <p v-if="successMessage" class="mt-2 text-success">{{ successMessage }}</p>
    <p v-if="errorMessage" class="mt-2 text-danger">{{ errorMessage }}</p>
  </div>
</template>
