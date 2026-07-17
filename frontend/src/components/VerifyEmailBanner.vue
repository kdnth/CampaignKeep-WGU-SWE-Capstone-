<script setup lang="ts">
import { onUnmounted, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import axios from 'axios'

const authStore = useAuthStore()

const isLoading = ref(false)
const message = ref<string | null>(null)
const isError = ref(false)
const cooldownSeconds = ref(0)

let cooldownTimer: ReturnType<typeof setInterval> | null = null

function startCooldown(seconds = 60) {
  cooldownSeconds.value = seconds
  if (cooldownTimer) clearInterval(cooldownTimer)
  cooldownTimer = setInterval(() => {
    cooldownSeconds.value -= 1
    if (cooldownSeconds.value <= 0 && cooldownTimer) {
      clearInterval(cooldownTimer)
      cooldownTimer = null
    }
  }, 1000)
}

async function resend() {
  message.value = null
  isError.value = false
  isLoading.value = true

  try {
    const result = await authStore.resendVerificationEmail()
    message.value = result.message
    startCooldown(60)
  } catch (err: unknown) {
    isError.value = true
    if (axios.isAxiosError(err)) {
      message.value = err.response?.data?.message ?? 'Could not send verification email.'
      if (err.response?.status === 429) {
        startCooldown(60)
      }
    } else {
      message.value = 'An unexpected error occurred. Please try again.'
    }
  } finally {
    isLoading.value = false
  }
}

onUnmounted(() => {
  if (cooldownTimer) clearInterval(cooldownTimer)
})
</script>

<template>
  <div
    v-if="authStore.needsEmailVerification"
    class="flex flex-col gap-2 border-b border-border bg-warning-muted px-4 py-3 sm:flex-row sm:items-center sm:justify-between sm:px-8"
    role="status"
  >
    <div class="min-w-0">
      <p class="text-sm text-fg">
        Please verify your email
        <span v-if="authStore.email" class="text-fg-muted">({{ authStore.email }})</span>
        to secure your account.
      </p>
      <p
        v-if="message"
        class="mt-1 text-sm"
        :class="isError ? 'text-danger' : 'text-fg-subtle'"
      >
        {{ message }}
      </p>
    </div>

    <button
      type="button"
      class="shrink-0 cursor-pointer rounded-lg border border-border bg-surface px-4 py-2 text-sm text-fg transition-colors hover:bg-surface-muted disabled:cursor-not-allowed disabled:opacity-60"
      :disabled="isLoading || cooldownSeconds > 0"
      @click="resend"
    >
      <template v-if="isLoading">Sending…</template>
      <template v-else-if="cooldownSeconds > 0">Resend in {{ cooldownSeconds }}s</template>
      <template v-else>Resend email</template>
    </button>
  </div>
</template>
