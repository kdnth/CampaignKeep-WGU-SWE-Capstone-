<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { PxChevronDown, PxChevronUp } from 'oh-vue-icons/icons'
import { OhVueIcon, addIcons } from 'oh-vue-icons'

addIcons(PxChevronDown, PxChevronUp)

const router = useRouter()
const authStore = useAuthStore()

const dropdownOpen = ref(false)

function toggleDropdown() {
  dropdownOpen.value = !dropdownOpen.value
}

function handleLogout() {
  authStore.logout()
  dropdownOpen.value = false
  router.push({ name: 'login' })
}
</script>

<template>
  <nav class="flex items-center justify-between px-8 py-4 bg-black">
    <!-- LOGO -->
    <RouterLink :to="{ name: 'home' }" class="text-3xl font-normal jacquard">
      <div class="flex gap-2 items-center">
        <img src="/favicon.ico" alt="Multi-colored 6-sided dice" class="w-8 h-8" />
        <div>
          <span class="text-white">Campaign</span>
          <span class="text-red-500">Keep</span>
        </div>
      </div>
    </RouterLink>

    <!-- NAV LINKS -->
    <div v-if="authStore.isAuthenticated" class="flex gap-6 absolute left-1/2 -translate-x-1/2">
      <RouterLink
        :to="{ name: 'home' }"
        class="text-neutral-300 hover:text-white transition-colors"
      >
        Home
      </RouterLink>
      <RouterLink
        :to="{ name: 'campaigns' }"
        class="text-neutral-300 hover:text-white transition-colors"
      >
        My Campaigns
      </RouterLink>
      <RouterLink
        :to="{ name: 'characters' }"
        class="text-neutral-300 hover:text-white transition-colors"
      >
        My Characters
      </RouterLink>
      <RouterLink
        :to="{ name: 'spells' }"
        class="text-neutral-300 hover:text-white transition-colors"
      >
        Spells
      </RouterLink>
    </div>

    <!-- AUTH -->
    <div class="flex items-center gap-3">
      <!-- LOGGED OUT -->
      <template v-if="!authStore.isAuthenticated">
        <RouterLink
          :to="{ name: 'login' }"
          class="text-neutral-300 hover:text-white transition-colors text-sm"
        >
          Login
        </RouterLink>
        <RouterLink
          :to="{ name: 'register' }"
          class="bg-green-800 hover:bg-green-900 text-white text-sm px-4 py-2 rounded-lg transition-colors"
        >
          Sign Up
        </RouterLink>
      </template>

      <!-- LOGGED IN -->
      <template v-else>
        <div class="relative">
          <button
            @click="toggleDropdown"
            class="flex items-center gap-2 text-neutral-300 hover:text-white transition-colors text-sm"
          >
            <span>{{ authStore.username }}</span>
            <OhVueIcon name="px-chevron-down" v-if="!dropdownOpen" class="w-4 h-4" />
            <OhVueIcon name="px-chevron-up" v-else class="w-4 h-4" />
          </button>

          <div
            v-if="dropdownOpen"
            class="absolute right-0 mt-2 w-40 bg-black border border-neutral-700 rounded-lg shadow-lg overflow-hidden"
          >
            <button
              @click="handleLogout"
              class="w-full text-left px-4 py-2 text-sm text-neutral-300 hover:bg-neutral-700 hover:text-white transition-colors"
            >
              Logout
            </button>
          </div>
        </div>
      </template>
    </div>
  </nav>
</template>

<style scoped>
.jacquard {
  font-family: 'Jacquard 24', serif;
}
</style>
