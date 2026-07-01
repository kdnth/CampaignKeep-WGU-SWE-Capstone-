<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const router = useRouter()
const authStore = useAuthStore()

function handleLogout() {
  authStore.logout()
  router.push({ name: 'login' })
}
</script>

<template>
  <div>
    <h1 class="text-white">Welcome, {{ authStore.username }}</h1>
    <BaseButton
      class="w-fit py-2.5 px-8 my-4 bg-white text-black rounded-lg cursor-pointer disabled:opacity-60 disabled:cursor-not-allowed hover:bg-gray-500 hover:text-white hover:border-2 hover:border-white"
      @click="handleLogout"
    >
      Logout
    </BaseButton>
  </div>
</template>
