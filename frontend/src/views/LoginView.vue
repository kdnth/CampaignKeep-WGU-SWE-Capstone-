<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import axios from 'axios'
import BaseInput from '@/components/BaseInput.vue'
import BaseButton from '@/components/BaseButton.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const usernameErr = ref(false)
const passwordErr = ref(false)

const identifier = ref('')
const password = ref('')
const errorMessage = ref<string | null>(null)
const isLoading = ref(false)

const successMessage = computed(() =>
  route.query.reset === '1' ? 'Password updated. You can sign in with your new password.' : null,
)

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
  <div class="flex w-screen justify-center p-2">
    <div class="md:w-3/4 lg:w-1/2">
      <h2 class="py-4 text-3xl font-medium text-fg">Login</h2>

      <!-- Form Card div -->
      <div class="flex min-h-fit flex-col rounded-4xl border-2 border-border px-16 py-8">
        <form @submit.prevent="handleSubmit">
          <!-- Username field -->
          <div class="flex flex-col pt-4">
            <label class="text-fg" for="identifier">Username</label>
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
            <div class="flex items-baseline justify-between gap-4">
              <label class="text-fg" for="password">Password</label>
            </div>
            <BaseInput
              id="password"
              v-model="password"
              type="password"
              placeholder="Password"
              required
              autocomplete="current-password"
              :error="!!passwordErr"
            />
            <RouterLink
              class="text-sm pt-2 text-fg-subtle underline hover:text-fg"
              :to="{ name: 'forgotPassword' }"
            >
              Forgot password?
            </RouterLink>
          </div>

          <!-- err / success -->
          <p v-if="errorMessage" class="mx-0.5 pt-2 text-danger">{{ errorMessage }}</p>
          <p v-else-if="successMessage" class="mx-0.5 pt-2 text-success">{{ successMessage }}</p>

          <!-- submit -->
          <div
            class="flex flex-col items-start py-8 sm:flex-row sm:items-center sm:justify-between"
          >
            <!-- Register link -->
            <p class="my-4 flex gap-2 text-fg">
              Don't have an account?
              <RouterLink class="text-fg underline hover:text-fg-subtle" :to="{ name: 'register' }">
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
