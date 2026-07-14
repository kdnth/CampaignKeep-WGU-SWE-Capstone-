<script setup lang="ts">
import { ref, watch } from 'vue'

export interface TabItem {
  id: string
  label: string
  disabled?: boolean
}

const props = defineProps<{
  tabs: TabItem[]
  defaultTab?: string
}>()

const activeTab = ref(props.defaultTab ?? props.tabs[0]?.id ?? '')

watch(
  () => props.defaultTab,
  (tab) => {
    if (tab) activeTab.value = tab
  },
)
</script>

<template>
  <div class="flex flex-col gap-4">
    <div class="flex flex-wrap gap-1 border-b border-neutral-600">
      <button
        v-for="tab in tabs"
        :key="tab.id"
        type="button"
        class="px-4 py-2 text-sm font-medium rounded-t-lg transition-colors"
        :class="
          activeTab === tab.id
            ? 'bg-purple-600 text-white'
            : tab.disabled
              ? 'text-neutral-500 cursor-not-allowed'
              : 'text-neutral-300 hover:text-white hover:bg-neutral-700'
        "
        :disabled="tab.disabled"
        @click="!tab.disabled && (activeTab = tab.id)"
      >
        {{ tab.label }}
      </button>
    </div>

    <div v-for="tab in tabs" :key="tab.id">
      <slot v-if="activeTab === tab.id" :name="tab.id" />
    </div>
  </div>
</template>
