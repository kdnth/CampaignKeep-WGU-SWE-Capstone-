<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import BaseInput from '@/components/BaseInput.vue'
import BaseButton from '@/components/BaseButton.vue'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const token = computed(() => {
  const value = route.query.token
  return typeof value === 'string' ? value : ''
})

const password = ref('')
const passwordConfirm = ref('')
const passwordErr = ref(false)
const errorMessage = ref<string | null>(null)
const isLoading = ref(false)

async function handleSubmit() {
  errorMessage.value = null
  passwordErr.value = false

  if (!token.value) {
    errorMessage.value = 'This reset link is missing a token. Request a new one.'
    return
  }

  if (password.value !== passwordConfirm.value) {
    errorMessage.value = 'Passwords do not match'
    passwordErr.value = true
    return
  }

  isLoading.value = true
  try {
    await authStore.resetPassword(token.value, password.value)
    await router.push({ name: 'login', query: { reset: '1' } })
  } catch (err: unknown) {
    if (axios.isAxiosError(err)) {
      errorMessage.value =
        err.response?.data?.message ?? 'Invalid or expired reset link. Request a new one.'
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
      <h2 class="py-4 text-2xl font-medium text-fg sm:text-3xl">Reset password</h2>

      <div
        class="flex min-h-fit flex-col rounded-4xl border-2 border-border px-4 py-6 sm:px-8 sm:py-8 md:px-16"
      >
        <p v-if="!token" class="text-danger">
          This reset link is invalid.
          <RouterLink
            class="underline hover:text-fg-subtle"
            :to="{ name: 'forgotPassword' }"
          >
            Request a new one
          </RouterLink>
        </p>

        <form v-else @submit.prevent="handleSubmit">
          <div class="flex flex-col gap-4 sm:flex-row sm:justify-between sm:gap-6">
            <div class="flex min-w-0 flex-1 flex-col pt-4 pb-2">
              <label class="text-fg" for="password">New password</label>
              <BaseInput
                id="password"
                v-model="password"
                type="password"
                placeholder="Password"
                required
                minlength="8"
                autocomplete="new-password"
                :error="passwordErr"
              />
            </div>
            <div class="flex min-w-0 flex-1 flex-col pt-4 pb-2">
              <label class="text-fg" for="passwordConfirm">Confirm password</label>
              <BaseInput
                id="passwordConfirm"
                v-model="passwordConfirm"
                type="password"
                placeholder="Password"
                required
                minlength="8"
                autocomplete="new-password"
                :error="passwordErr"
              />
            </div>
          </div>

          <p v-if="errorMessage" class="mx-0.5 my-2 text-danger">{{ errorMessage }}</p>

          <div class="flex flex-col items-stretch gap-4 py-6 sm:py-8">
            <BaseButton
              class="!w-full sm:!w-fit sm:self-center"
              variant="submit"
              type="submit"
              :loading="isLoading"
            >
              {{ isLoading ? 'Updating...' : 'Update password' }}
            </BaseButton>

            <p class="text-center text-fg sm:text-left">
              <RouterLink
                class="text-fg underline hover:text-fg-subtle"
                :to="{ name: 'forgotPassword' }"
              >
                Request a new reset link
              </RouterLink>
            </p>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
