<script setup lang="ts">
import CompactCampaignList from '@/components/CompactCampaignList.vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
</script>

<template>
  <div class="px-8 h-full">
    <h1 class="text-6xl text-white py-24">Welcome, {{ authStore.username }}.</h1>

    <div class="w-full overflow-hidden">
      <h4 class="text-3xl text-white">My Campaigns</h4>
      <CompactCampaignList />
    </div>
  </div>
</template>
