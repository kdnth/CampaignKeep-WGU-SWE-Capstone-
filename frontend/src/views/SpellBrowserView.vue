<script setup lang="ts">
import BackToLink from '@/components/BackToLink.vue'
import SpellBrowser from '@/components/spells/SpellBrowser.vue'
</script>

<template>
  <div class="flex w-full flex-col gap-4 p-8">
    <BackToLink page="home" link-name="Home" />
    <h1 class="text-3xl text-fg">Spell Browser</h1>
    <p class="text-fg-subtle">Browse the SRD spell catalog. Click a card to flip for details.</p>
    <SpellBrowser mode="browse" />
  </div>
</template>
