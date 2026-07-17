<script setup lang="ts">
import ItemCard from '@/components/equipment/ItemCard.vue'
import type { ItemDetailResponse } from '@/stores/inventory'
import { computed } from 'vue'

export interface InventoryListEntry {
  key: number | string
  item: ItemDetailResponse
  quantity?: number
}

const props = withDefaults(
  defineProps<{
    items?: ItemDetailResponse[]
    entries?: InventoryListEntry[]
    quantities?: Record<number, number>
    title?: string
    emptyMessage?: string
  }>(),
  {
    emptyMessage: 'No items to display.',
  },
)

const displayEntries = computed<InventoryListEntry[]>(() => {
  if (props.entries?.length) {
    return props.entries
  }
  return (props.items ?? []).map((item) => ({
    key: item.id,
    item,
    quantity: props.quantities?.[item.id],
  }))
})

const hasItems = computed(() => displayEntries.value.length > 0)
</script>

<template>
  <section class="space-y-4">
    <h2 v-if="title" class="text-xl font-semibold text-fg">{{ title }}</h2>
    <p v-if="!hasItems" class="text-fg-subtle">{{ emptyMessage }}</p>
    <div v-else class="grid grid-cols-[repeat(auto-fill,minmax(12rem,1fr))] gap-4">
      <ItemCard
        v-for="entry in displayEntries"
        :key="entry.key"
        :item="entry.item"
        :quantity="entry.quantity"
      />
    </div>
  </section>
</template>
