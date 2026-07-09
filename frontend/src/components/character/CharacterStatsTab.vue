<script setup lang="ts">
import CharacterHpPanel from '@/components/character/CharacterHpPanel.vue'

defineProps<{
  character: import('@/stores/character').CharacterResponse
  campaignId: number
}>()
</script>

<template>
  <CharacterHpPanel :character="character" :campaign-id="campaignId" />
</template>
