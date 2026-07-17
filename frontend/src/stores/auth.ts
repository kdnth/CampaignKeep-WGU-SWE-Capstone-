import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import apiClient from '@/api/axios'

export interface UserResponse {
  userId: number
  username: string
  email: string
  emailVerified: boolean
}

export interface MessageResponse {
  message: string
}

const TOKEN_ = 'token'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const userId = ref<number | null>(null)
  const username = ref<string | null>(null)
  const email = ref<string | null>(null)
  const emailVerified = ref<boolean | null>(null)

  const isAuthenticated = computed(() => !!token.value)
  const needsEmailVerification = computed(
    () => isAuthenticated.value && emailVerified.value === false,
  )

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem(TOKEN_, newToken)
  }

  function clearAuth() {
    token.value = null
    userId.value = null
    username.value = null
    email.value = null
    emailVerified.value = null
    localStorage.removeItem(TOKEN_)
  }

  async function register(usernameInput: string, emailInput: string, password: string) {
    const response = await apiClient.post('/auth/register', {
      username: usernameInput,
      email: emailInput,
      password,
    })
    setToken(response.data.token)
    await fetchCurrentUser()
  }

  async function login(identifier: string, password: string) {
    const response = await apiClient.post('/auth/login', {
      identifier,
      password,
    })
    setToken(response.data.token)
    await fetchCurrentUser()
  }

  async function fetchCurrentUser() {
    const response = await apiClient.get<UserResponse>('/users/me')
    userId.value = response.data.userId
    username.value = response.data.username
    email.value = response.data.email
    emailVerified.value = response.data.emailVerified
  }

  async function forgotPassword(emailInput: string) {
    const response = await apiClient.post<MessageResponse>('/auth/forgot-password', {
      email: emailInput,
    })
    return response.data
  }

  async function resetPassword(resetToken: string, newPassword: string) {
    const response = await apiClient.post<MessageResponse>('/auth/reset-password', {
      token: resetToken,
      newPassword,
    })
    return response.data
  }

  async function verifyEmail(verifyToken: string) {
    const response = await apiClient.post<MessageResponse>('/auth/verify-email', {
      token: verifyToken,
    })
    if (isAuthenticated.value) {
      await fetchCurrentUser()
    }
    return response.data
  }

  async function resendVerificationEmail() {
    const response = await apiClient.post<MessageResponse>('/users/me/verification-email')
    return response.data
  }

  function logout() {
    clearAuth()
  }

  return {
    token,
    userId,
    username,
    email,
    emailVerified,
    isAuthenticated,
    needsEmailVerification,
    register,
    login,
    logout,
    fetchCurrentUser,
    forgotPassword,
    resetPassword,
    verifyEmail,
    resendVerificationEmail,
  }
})
