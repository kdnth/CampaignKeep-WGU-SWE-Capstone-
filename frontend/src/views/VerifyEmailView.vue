<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import axios from 'axios'

const route = useRoute()
const authStore = useAuthStore()

const token = computed(() => {
  const value = route.query.token
  return typeof value === 'string' ? value : ''
})

const status = ref<'loading' | 'success' | 'error'>('loading')
const message = ref('')

onMounted(async () => {
  if (!token.value) {
    status.value = 'error'
    message.value = 'This verification link is missing a token.'
    return
  }

  try {
    const result = await authStore.verifyEmail(token.value)
    status.value = 'success'
    message.value = result.message
  } catch (err: unknown) {
    status.value = 'error'
    if (axios.isAxiosError(err)) {
      message.value =
        err.response?.data?.message ?? 'This verification link is invalid or expired.'
    } else {
      message.value = 'An unexpected error occurred. Please try again.'
    }
  }
})
</script>

<template>
  <div class="flex w-full max-w-full justify-center p-2">
    <div class="w-full px-2 sm:w-3/4 sm:px-0 lg:w-1/2">
      <h2 class="py-4 text-2xl font-medium text-fg sm:text-3xl">Verify email</h2>

      <div
        class="flex min-h-fit flex-col gap-4 rounded-4xl border-2 border-border px-4 py-6 sm:px-8 sm:py-8 md:px-16"
      >
        <p v-if="status === 'loading'" class="text-fg-subtle">Verifying your email…</p>
        <p v-else-if="status === 'success'" class="text-success">{{ message }}</p>
        <p v-else class="text-danger">{{ message }}</p>

        <p v-if="status !== 'loading'" class="text-fg">
          <template v-if="authStore.isAuthenticated">
            <RouterLink class="underline hover:text-fg-subtle" :to="{ name: 'home' }">
              Continue to home
            </RouterLink>
            <span v-if="status === 'error'">
              — or use the banner to resend a verification email.
            </span>
          </template>
          <template v-else>
            <RouterLink class="underline hover:text-fg-subtle" :to="{ name: 'login' }">
              Log in
            </RouterLink>
            <span v-if="status === 'error'">
              — then resend a verification email from the banner.
            </span>
          </template>
        </p>
      </div>
    </div>
  </div>
</template>
