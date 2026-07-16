<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import axios from 'axios'
import BaseInput from '@/components/BaseInput.vue'
import BaseButton from '@/components/BaseButton.vue'

const router = useRouter()
const authStore = useAuthStore()

const usernameErr = ref(false)
const passwordErr = ref(false)

const identifier = ref('')
const password = ref('')
const errorMessage = ref<string | null>(null)
const isLoading = ref(false)

async function handleSubmit() {
  errorMessage.value = null
  isLoading.value = true
  usernameErr.value = false
  passwordErr.value = false

  try {
    await authStore.login(identifier.value, password.value)
    router.push({ name: 'home' })
  } catch (err: unknown) {
    if (axios.isAxiosError(err)) {
      errorMessage.value = err.response?.data?.message ?? 'Invalid username or password'

      if (errorMessage.value?.toLowerCase().includes('username')) {
        usernameErr.value = true
        passwordErr.value = true
      }
    } else {
      errorMessage.value = 'An unexpected error occurred. Please try again.'
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="p-2 w-screen flex justify-center">
    <div class="lg:w-1/2 md:w-3/4">
      <h2 class="text-3xl py-4 font-medium text-white">Login</h2>

      <!-- Form Card div -->
      <div class="flex flex-col px-16 py-8 min-h-fit border-2 border-neutral-200 rounded-4xl">
        <form @submit.prevent="handleSubmit">
          <!-- Username field -->
          <div class="flex flex-col pt-4">
            <label class="text-white" for="identifier">Username</label>
            <BaseInput
              id="identifier"
              v-model="identifier"
              type="text"
              placeholder="Username or email"
              required
              autocomplete="username"
              :error="!!usernameErr"
            />
          </div>

          <!-- PW field -->
          <div class="flex flex-col pt-4">
            <label class="text-white" for="password">Password</label>
            <BaseInput
              id="password"
              v-model="password"
              type="password"
              placeholder="Password"
              required
              autocomplete="current-password"
              :error="!!passwordErr"
            />
          </div>

          <!-- err message -->
          <p v-if="errorMessage" class="text-red-600 mx-0.5 pt-2">{{ errorMessage }}</p>

          <!-- submit -->
          <div class="flex flex-col items-start sm:flex-row sm:items-center sm:justify-between py-8">
            <!-- Register link -->
            <p class="text-white my-4 flex gap-2">
              Don't have an account?
              <RouterLink
                class="text-white underline hover:text-neutral-400"
                :to="{ name: 'register' }"
              >
                Create one</RouterLink
              >
            </p>
            <BaseButton variant="submit" type="submit" :loading="isLoading">
              {{ isLoading ? 'Logging in...' : 'Login' }}
            </BaseButton>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
