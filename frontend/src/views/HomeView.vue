<script setup lang="ts">
import CompactCampaignList from '@/components/CompactCampaignList.vue'
import CompactCharacterList from '@/components/CompactCharacterList.vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
</script>

<template>
  <div class="px-8 pb-40">
    <h1 class="text-6xl text-fg py-24">Welcome, {{ authStore.username }}.</h1>

    <div class="w-full overflow-hidden my-8 py-4">
      <CompactCampaignList />
    </div>
    <div class="w-full overflow-hidden my-8 py-4">
      <CompactCharacterList />
    </div>
  </div>
</template>
