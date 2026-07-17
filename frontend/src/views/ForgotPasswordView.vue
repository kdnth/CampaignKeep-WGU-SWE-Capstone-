<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import BaseInput from '@/components/BaseInput.vue'
import BaseButton from '@/components/BaseButton.vue'
import axios from 'axios'

const authStore = useAuthStore()

const email = ref('')
const errorMessage = ref<string | null>(null)
const successMessage = ref<string | null>(null)
const isLoading = ref(false)

async function handleSubmit() {
  errorMessage.value = null
  successMessage.value = null
  isLoading.value = true

  try {
    const result = await authStore.forgotPassword(email.value)
    successMessage.value = result.message
  } catch (err: unknown) {
    if (axios.isAxiosError(err)) {
      errorMessage.value =
        err.response?.data?.message ?? 'Something went wrong. Please try again.'
    } else {
      errorMessage.value = 'An unexpected error occurred. Please try again.'
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="flex w-full max-w-full justify-center p-2">
    <div class="w-full px-2 sm:w-3/4 sm:px-0 lg:w-1/2">
      <h2 class="py-4 text-2xl font-medium text-fg sm:text-3xl">Forgot password</h2>

      <div
        class="flex min-h-fit flex-col rounded-4xl border-2 border-border px-4 py-6 sm:px-8 sm:py-8 md:px-16"
      >
        <p class="text-fg-subtle">
          Enter the email for your account. If it exists, we'll send a reset link.
        </p>

        <form class="mt-4" @submit.prevent="handleSubmit">
          <div class="flex flex-col pt-2 pb-2">
            <label class="text-fg" for="email">Email</label>
            <BaseInput
              id="email"
              v-model="email"
              type="email"
              placeholder="Email"
              required
              autocomplete="email"
            />
          </div>

          <p v-if="errorMessage" class="mx-0.5 my-2 text-danger">{{ errorMessage }}</p>
          <p v-if="successMessage" class="mx-0.5 my-2 text-success">{{ successMessage }}</p>

          <div class="flex flex-col items-stretch gap-4 py-6 sm:py-8">
            <BaseButton
              class="!w-full sm:!w-fit sm:self-center"
              variant="submit"
              type="submit"
              :loading="isLoading"
              :disabled="!!successMessage"
            >
              {{ isLoading ? 'Sending...' : 'Send reset link' }}
            </BaseButton>

            <p class="flex flex-col text-center text-fg sm:flex-row sm:gap-2 sm:text-left">
              Remembered it?
              <RouterLink class="text-fg underline hover:text-fg-subtle" :to="{ name: 'login' }">
                Log in
              </RouterLink>
            </p>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
