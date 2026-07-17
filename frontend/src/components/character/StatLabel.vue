<script setup lang="ts">
defineProps<{
  stat: 'hp' | 'ac' | 'initiative' | 'speed'
  value: number
}>()
</script>
<template>
  <div class="text-fg">
    <div class="flex flex-col items-center">
      <h1 class="text-xs px-0.5 mx-1 uppercase border-b border-border-strong">
        {{ stat }}
      </h1>
      <p class="text-lg"><span v-if="stat == 'initiative'">+</span>{{ value }}</p>
    </div>
  </div>
</template>
