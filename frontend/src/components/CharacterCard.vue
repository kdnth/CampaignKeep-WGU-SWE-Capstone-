<script setup lang="ts">
import CharacterAvatar from '@/components/CharacterAvatar.vue'

defineProps<{
  name: string
  raceName: string
  className: string
  campaigns: string[]
}>()
</script>

<template>
  <div
    class="w-full h-64 grid grid-rows-5 bg-surface cursor-pointer rounded-xl overflow-hidden transition-colors hover:border-4 hover:border-accent"
  >
    <div class="row-span-3 bg-accent flex justify-center items-center">
      <CharacterAvatar :race-name="raceName" :class-name="className" size="md" />
    </div>
    <div class="justify-start row-span-2 p-2">
      <div class="min-w-0 flex flex-col">
        <h2 class="text-xl truncate">{{ name }}</h2>
        <h4 class="text-sm capitalize">{{ raceName }} - {{ className }} - Lvl. 1</h4>
        <div class="flex flex-wrap gap-1 mt-1 max-h-12 overflow-hidden">
          <span
            v-for="(campaign, index) in campaigns"
            :key="index"
            class="inline-block max-w-32 truncate bg-accent-muted text-accent text-xs font-semibold px-2.5 py-0.5 rounded-lg"
            :title="campaign"
          >
            {{ campaign }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
