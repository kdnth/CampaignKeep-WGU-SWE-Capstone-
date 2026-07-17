<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import { useInventoryStore, type EquipmentPackResponse } from '@/stores/inventory'
import { isAxiosError } from 'axios'
import { onMounted, ref } from 'vue'

const props = defineProps<{
  modelValue: string | null
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string | null]
  next: []
  back: []
}>()

const inventoryStore = useInventoryStore()
const packs = ref<EquipmentPackResponse[]>([])
const isLoading = ref(true)
const errorMessage = ref<string | null>(null)
const selectedIndex = ref<string | null>(props.modelValue)

function selectPack(index: string) {
  selectedIndex.value = index
  emit('update:modelValue', index)
}

function clearPack() {
  selectedIndex.value = null
  emit('update:modelValue', null)
}

function handleNext() {
  emit('next')
}

onMounted(async () => {
  try {
    packs.value = await inventoryStore.fetchEquipmentPacks()
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to load equipment packs')
      : 'Failed to load equipment packs'
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <div class="space-y-6 text-fg">
    <h2 class="text-xl font-semibold">Starting Pack</h2>
    <p class="text-sm text-fg-subtle">
      Choose an equipment pack. Contents are loaded from the D&amp;D 5e API and added to this NPC's
      inventory.
    </p>

    <p v-if="isLoading" class="text-fg-subtle">Loading packs...</p>
    <p v-else-if="errorMessage" class="text-danger">{{ errorMessage }}</p>

    <div v-else class="space-y-3">
      <label
        v-for="pack in packs"
        :key="pack.index"
        class="flex cursor-pointer flex-col gap-2 rounded-lg border border-border px-4 py-3 hover:bg-surface-muted"
        :class="selectedIndex === pack.index ? 'border-accent bg-surface-muted' : ''"
      >
        <div class="flex items-center gap-3">
          <input
            type="radio"
            name="starting-pack"
            :value="pack.index"
            :checked="selectedIndex === pack.index"
            @change="selectPack(pack.index)"
          />
          <div>
            <p class="font-medium">{{ pack.name }}</p>
            <p class="text-xs text-fg-subtle">{{ pack.costGp }} gp</p>
          </div>
        </div>
        <ul class="ml-8 list-disc text-xs text-fg-subtle">
          <li v-for="content in pack.contents" :key="content.apiIndex">
            {{ content.name }}
            <span v-if="content.quantity > 1">×{{ content.quantity }}</span>
          </li>
        </ul>
      </label>

      <label
        class="flex cursor-pointer items-center gap-3 rounded-lg border border-border px-4 py-3 hover:bg-surface-muted"
        :class="selectedIndex === null ? 'border-accent bg-surface-muted' : ''"
      >
        <input
          type="radio"
          name="starting-pack"
          :checked="selectedIndex === null"
          @change="clearPack"
        />
        <span>No pack</span>
      </label>
    </div>

    <div class="flex gap-3">
      <BaseButton variant="secondary" @click="emit('back')">Back</BaseButton>
      <BaseButton variant="primary" @click="handleNext">Continue</BaseButton>
    </div>
  </div>
</template>
