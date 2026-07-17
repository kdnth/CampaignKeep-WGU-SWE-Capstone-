<script setup lang="ts">
import { PxArrowLeft } from 'oh-vue-icons/icons'
import { OhVueIcon, addIcons } from 'oh-vue-icons'
defineProps<{
  page: string
  linkName: string
}>()

addIcons(PxArrowLeft)
</script>
<template>
  <RouterLink :to="{ name: page }" class="text-accent hover:underline hover:text-accent-hover"
    ><OhVueIcon name="px-arrow-left" /> Back to {{ linkName }}</RouterLink
  >
</template>
