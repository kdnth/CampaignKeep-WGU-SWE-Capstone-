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
  <RouterLink :to="{ name: page }" class="text-purple-500 hover:underline hover:text-purple-300"
    ><OhVueIcon name="px-arrow-left" /> Back to {{ linkName }}</RouterLink
  >
</template>
