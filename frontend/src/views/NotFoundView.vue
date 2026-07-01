<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import { useRouter } from 'vue-router'

const router = useRouter()
</script>
<template>
  <div>
    <h1>404</h1>
    <p>Page not found.</p>
    <BaseButton @click="router.push({ name: 'home' })">Back to Home</BaseButton>
  </div>
</template>
