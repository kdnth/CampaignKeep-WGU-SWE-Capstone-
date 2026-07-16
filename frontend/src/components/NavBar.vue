<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { PxChevronDown, PxChevronUp, PxClose, PxMenu } from 'oh-vue-icons/icons'
import { OhVueIcon, addIcons } from 'oh-vue-icons'

addIcons(PxChevronDown, PxChevronUp, PxClose, PxMenu)

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const dropdownOpen = ref(false)
const mobileMenuOpen = ref(false)

function toggleDropdown() {
  dropdownOpen.value = !dropdownOpen.value
}

function toggleMobileMenu() {
  mobileMenuOpen.value = !mobileMenuOpen.value
  // Avoid two open menus competing for attention on resize edge cases
  if (mobileMenuOpen.value) dropdownOpen.value = false
}

function closeMobileMenu() {
  mobileMenuOpen.value = false
}

function handleLogout() {
  authStore.logout()
  dropdownOpen.value = false
  mobileMenuOpen.value = false
  router.push({ name: 'login' })
}

// Close the drawer after navigation so the next page isn't covered
watch(
  () => route.fullPath,
  () => {
    closeMobileMenu()
    dropdownOpen.value = false
  },
)
</script>

<template>
  <nav class="relative flex items-center justify-between px-4 sm:px-8 py-4 bg-black">
    <!-- LOGO -->
    <RouterLink :to="{ name: 'home' }" class="text-2xl sm:text-3xl font-normal jacquard shrink-0">
      <div class="flex gap-2 items-center">
        <img src="/favicon.ico" alt="Multi-colored 6-sided dice" class="w-7 h-7 sm:w-8 sm:h-8" />
        <div>
          <span class="text-white">Campaign</span>
          <span class="text-red-500">Keep</span>
        </div>
      </div>
    </RouterLink>

    <!-- DESKTOP NAV LINKS -->
    <div
      v-if="authStore.isAuthenticated"
      class="hidden md:flex gap-6 absolute left-1/2 -translate-x-1/2"
    >
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

    <!-- DESKTOP AUTH -->
    <div class="hidden md:flex items-center gap-3">
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
            type="button"
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
              type="button"
              @click="handleLogout"
              class="w-full text-left px-4 py-2 text-sm text-neutral-300 hover:bg-neutral-700 hover:text-white transition-colors"
            >
              Logout
            </button>
          </div>
        </div>
      </template>
    </div>

    <!-- MOBILE HAMBURGER -->
    <button
      type="button"
      class="md:hidden text-neutral-300 hover:text-white transition-colors p-1"
      :aria-expanded="mobileMenuOpen"
      aria-controls="mobile-nav-menu"
      aria-label="Toggle navigation menu"
      @click="toggleMobileMenu"
    >
      <OhVueIcon :name="mobileMenuOpen ? 'px-close' : 'px-menu'" class="w-6 h-6" />
    </button>

    <!-- MOBILE MENU -->
    <div
      v-if="mobileMenuOpen"
      id="mobile-nav-menu"
      class="absolute top-full left-0 right-0 z-50 md:hidden bg-black border-t border-neutral-800 shadow-lg"
    >
      <div class="flex flex-col px-4 py-3 gap-1">
        <template v-if="authStore.isAuthenticated">
          <RouterLink
            :to="{ name: 'home' }"
            class="px-3 py-2.5 text-neutral-300 hover:text-white hover:bg-neutral-800 rounded-lg transition-colors"
            @click="closeMobileMenu"
          >
            Home
          </RouterLink>
          <RouterLink
            :to="{ name: 'campaigns' }"
            class="px-3 py-2.5 text-neutral-300 hover:text-white hover:bg-neutral-800 rounded-lg transition-colors"
            @click="closeMobileMenu"
          >
            My Campaigns
          </RouterLink>
          <RouterLink
            :to="{ name: 'characters' }"
            class="px-3 py-2.5 text-neutral-300 hover:text-white hover:bg-neutral-800 rounded-lg transition-colors"
            @click="closeMobileMenu"
          >
            My Characters
          </RouterLink>
          <RouterLink
            :to="{ name: 'spells' }"
            class="px-3 py-2.5 text-neutral-300 hover:text-white hover:bg-neutral-800 rounded-lg transition-colors"
            @click="closeMobileMenu"
          >
            Spells
          </RouterLink>

          <div class="my-2 border-t border-neutral-800" />

          <p class="px-3 py-1 text-sm text-neutral-500">{{ authStore.username }}</p>
          <button
            type="button"
            class="w-full text-left px-3 py-2.5 text-neutral-300 hover:text-white hover:bg-neutral-800 rounded-lg transition-colors"
            @click="handleLogout"
          >
            Logout
          </button>
        </template>

        <template v-else>
          <RouterLink
            :to="{ name: 'login' }"
            class="px-3 py-2.5 text-neutral-300 hover:text-white hover:bg-neutral-800 rounded-lg transition-colors"
            @click="closeMobileMenu"
          >
            Login
          </RouterLink>
          <RouterLink
            :to="{ name: 'register' }"
            class="px-3 py-2.5 text-center bg-green-800 hover:bg-green-900 text-white rounded-lg transition-colors"
            @click="closeMobileMenu"
          >
            Sign Up
          </RouterLink>
        </template>
      </div>
    </div>
  </nav>
</template>

<style scoped>
.jacquard {
  font-family: 'Jacquard 24', serif;
}
</style>
