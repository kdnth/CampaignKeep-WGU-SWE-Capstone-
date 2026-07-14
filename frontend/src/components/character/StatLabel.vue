<script setup lang="ts">
defineProps<{
  stat: 'hp' | 'ac' | 'initiative' | 'speed'
  value: number
}>()
</script>
<template>
  <div class="text-white">
    <div class="flex flex-col items-center">
      <h1 class="text-xs uppercase border-b border-white">
        {{ stat }}
      </h1>
      <p class="text-lg"><span v-if="stat == 'initiative'">+</span>{{ value }}</p>
    </div>
  </div>
</template>
