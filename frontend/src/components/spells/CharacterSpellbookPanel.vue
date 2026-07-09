<script setup lang="ts">
import SpellBrowser from '@/components/spells/SpellBrowser.vue'

defineProps<{
  characterId: number
}>()
</script>

<template>
  <SpellBrowser mode="spellbook" :character-id="characterId" title="Spells" />
</template>
