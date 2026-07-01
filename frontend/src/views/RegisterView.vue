<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import BaseInput from '@/components/BaseInput.vue'
import BaseButton from '@/components/BaseButton.vue'
import axios from 'axios'

const router = useRouter()
const authStore = useAuthStore()

const usernameErr = ref(false)
const passwordErr = ref(false)
const emailErr = ref(false)

const usernameInput = ref('')
const email = ref('')
const password = ref('')
const passwordConfirm = ref('')
const errorMessage = ref<string | null>(null)
const isLoading = ref(false)

async function handleSubmit() {
  errorMessage.value = null
  isLoading.value = true

  usernameErr.value = false
  emailErr.value = false
  passwordErr.value = false

  if (password.value != passwordConfirm.value) {
    errorMessage.value = 'Passwords do not match'
    passwordErr.value = true
    isLoading.value = false
  } else {
    try {
      await authStore.register(usernameInput.value, email.value, password.value)
      router.push({ name: 'home' })
    } catch (err: unknown) {
      if (axios.isAxiosError(err)) {
        errorMessage.value = err.response?.data?.message ?? 'Username or email already registered'
        if (errorMessage.value?.toLowerCase().includes('username')) {
          usernameErr.value = true
        }
        if (errorMessage.value?.toLowerCase().includes('email')) {
          emailErr.value = true
        }
      } else {
        errorMessage.value = 'An unexpected error occurred. Please try again.'
      }
    } finally {
      isLoading.value = false
    }
  }
}
</script>

<template>
  <div class="p-2 w-screen flex justify-center">
    <div class="lg:w-1/2 md:w-3/4">
      <h2 class="text-3xl py-4 font-medium text-white">Create an account</h2>

      <div class="flex flex-col px-16 py-8 min-h-fit border-2 border-gray-200 rounded-4xl">
        <form @submit.prevent="handleSubmit">
          <div class="flex flex-col pt-4 pb-2">
            <!-- username field -->
            <label class="text-white" for="usernameInput">Choose a username</label>
            <BaseInput
              id="usernameInput"
              v-model="usernameInput"
              type="text"
              placeholder="Username"
              required
              autocomplete="username"
              :error="!!usernameErr"
            />
          </div>
          <!-- email field -->
          <div class="flex flex-col pt-4 pb-2">
            <label class="text-white" for="email">Enter your email</label>
            <BaseInput
              id="email"
              v-model="email"
              type="text"
              placeholder="Email"
              required
              autocomplete="email"
              :error="!!emailErr"
            />
          </div>
          <!-- PW field -->
          <div class="flex justify-between">
            <div class="flex flex-col pt-4 pb-2">
              <label class="text-white" for="password">Password</label>
              <BaseInput
                id="password"
                v-model="password"
                type="password"
                placeholder="Password"
                required
                autocomplete="password"
                :error="!!passwordErr"
              />
            </div>
            <!-- Confirm PW -->
            <div class="flex flex-col pt-4 pb-2">
              <label class="text-white" for="passwordConfirm">Confirm password</label>
              <BaseInput
                id="passwordConfirm"
                v-model="passwordConfirm"
                type="password"
                placeholder="Password"
                required
                autocomplete="password"
                :error="!!passwordErr"
              />
            </div>
          </div>
          <!-- ERR MSG -->
          <p v-if="errorMessage" class="text-red-500 mx-0.5 my-2">{{ errorMessage }}</p>

          <!-- submit -->
          <div class="flex justify-center">
            <BaseButton type="submit" :loading="isLoading">
              {{ isLoading ? 'Creating account...' : 'Create account' }}
            </BaseButton>
          </div>
        </form>

        <p class="text-white my-4 flex flex-col">
          Already have an account?
          <RouterLink class="text-white underline hover:text-gray-400" :to="{ name: 'login' }"
            >Log in</RouterLink
          >
        </p>
      </div>
    </div>
  </div>
</template>
