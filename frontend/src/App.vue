<script setup lang="ts">
import NavBar from '@/components/NavBar.vue'
import { useAuthStore } from './stores/auth'
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const authStore = useAuthStore()

onMounted(async () => {
  if (authStore.isAuthenticated) {
    try {
      await authStore.fetchCurrentUser()
    } catch {
      authStore.logout()
      router.push({ name: 'login' })
    }
  }
})
</script>

<template>
  <NavBar />
  <RouterView />
</template>
