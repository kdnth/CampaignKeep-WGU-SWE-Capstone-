<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { BRAND_LOGOS, useThemeStore } from '@/stores/theme'
import { PxChevronDown, PxChevronUp, PxClose, PxMenu } from 'oh-vue-icons/icons'
import { OhVueIcon, addIcons } from 'oh-vue-icons'

addIcons(PxChevronDown, PxChevronUp, PxClose, PxMenu)

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const themeStore = useThemeStore()

const logoSrc = computed(() => BRAND_LOGOS[themeStore.resolved])

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
  <nav class="relative flex items-center justify-between bg-chrome px-4 py-4 sm:px-8">
    <!-- LOGO -->
    <RouterLink :to="{ name: 'home' }" class="jacquard shrink-0 text-2xl font-normal sm:text-3xl">
      <div class="flex items-center gap-2">
        <img :src="logoSrc" alt="Multi-colored 6-sided dice" class="h-7 w-7 sm:h-8 sm:w-8" />
        <div>
          <span class="text-fg">Campaign</span>
          <span class="text-brand">Keep</span>
        </div>
      </div>
    </RouterLink>

    <!-- DESKTOP NAV LINKS -->
    <div
      v-if="authStore.isAuthenticated"
      class="absolute left-1/2 hidden -translate-x-1/2 gap-6 md:flex"
    >
      <RouterLink :to="{ name: 'campaigns' }" class="text-fg-muted transition-colors hover:text-fg">
        My Campaigns
      </RouterLink>
      <RouterLink
        :to="{ name: 'characters' }"
        class="text-fg-muted transition-colors hover:text-fg"
      >
        My Characters
      </RouterLink>
      <RouterLink :to="{ name: 'spells' }" class="text-fg-muted transition-colors hover:text-fg">
        Spells
      </RouterLink>
    </div>

    <!-- DESKTOP AUTH -->
    <div class="hidden items-center gap-3 md:flex">
      <!-- LOGGED OUT -->
      <template v-if="!authStore.isAuthenticated">
        <RouterLink
          :to="{ name: 'login' }"
          class="text-sm text-fg-muted transition-colors hover:text-fg"
        >
          Login
        </RouterLink>
        <RouterLink
          :to="{ name: 'register' }"
          class="rounded-lg bg-success px-4 py-2 text-sm text-white transition-colors hover:bg-success-hover"
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
            class="flex items-center gap-2 text-sm text-fg-muted transition-colors hover:text-fg"
          >
            <span>{{ authStore.username }}</span>
            <OhVueIcon name="px-chevron-down" v-if="!dropdownOpen" class="h-4 w-4" />
            <OhVueIcon name="px-chevron-up" v-else class="h-4 w-4" />
          </button>

          <div
            v-if="dropdownOpen"
            class="absolute right-0 mt-2 w-40 overflow-hidden rounded-lg border border-border bg-chrome shadow-lg"
          >
            <RouterLink
              :to="{ name: 'settings' }"
              class="block px-4 py-2 text-sm text-fg-muted transition-colors hover:bg-surface-muted hover:text-fg"
            >
              Settings
            </RouterLink>
            <button
              type="button"
              @click="handleLogout"
              class="w-full px-4 py-2 text-left text-sm text-fg-muted transition-colors hover:bg-surface-muted hover:text-fg"
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
      class="p-1 text-fg-muted transition-colors hover:text-fg md:hidden"
      :aria-expanded="mobileMenuOpen"
      aria-controls="mobile-nav-menu"
      aria-label="Toggle navigation menu"
      @click="toggleMobileMenu"
    >
      <OhVueIcon :name="mobileMenuOpen ? 'px-close' : 'px-menu'" class="h-6 w-6" />
    </button>

    <!-- MOBILE MENU -->
    <div
      v-if="mobileMenuOpen"
      id="mobile-nav-menu"
      class="absolute top-full right-0 left-0 z-50 border-t border-border bg-chrome shadow-lg md:hidden"
    >
      <div class="flex flex-col gap-1 px-4 py-3">
        <template v-if="authStore.isAuthenticated">
          <RouterLink
            :to="{ name: 'home' }"
            class="rounded-lg px-3 py-2.5 text-fg-muted transition-colors hover:bg-surface-muted hover:text-fg"
            @click="closeMobileMenu"
          >
            Home
          </RouterLink>
          <RouterLink
            :to="{ name: 'campaigns' }"
            class="rounded-lg px-3 py-2.5 text-fg-muted transition-colors hover:bg-surface-muted hover:text-fg"
            @click="closeMobileMenu"
          >
            My Campaigns
          </RouterLink>
          <RouterLink
            :to="{ name: 'characters' }"
            class="rounded-lg px-3 py-2.5 text-fg-muted transition-colors hover:bg-surface-muted hover:text-fg"
            @click="closeMobileMenu"
          >
            My Characters
          </RouterLink>
          <RouterLink
            :to="{ name: 'spells' }"
            class="rounded-lg px-3 py-2.5 text-fg-muted transition-colors hover:bg-surface-muted hover:text-fg"
            @click="closeMobileMenu"
          >
            Spells
          </RouterLink>

          <div class="my-2 border-t border-border" />

          <p class="px-3 py-1 text-sm text-fg-subtle">{{ authStore.username }}</p>
          <RouterLink
            :to="{ name: 'settings' }"
            class="rounded-lg px-3 py-2.5 text-fg-muted transition-colors hover:bg-surface-muted hover:text-fg"
            @click="closeMobileMenu"
          >
            Settings
          </RouterLink>
          <button
            type="button"
            class="w-full rounded-lg px-3 py-2.5 text-left text-fg-muted transition-colors hover:bg-surface-muted hover:text-fg"
            @click="handleLogout"
          >
            Logout
          </button>
        </template>

        <template v-else>
          <RouterLink
            :to="{ name: 'login' }"
            class="rounded-lg px-3 py-2.5 text-fg-muted transition-colors hover:bg-surface-muted hover:text-fg"
            @click="closeMobileMenu"
          >
            Login
          </RouterLink>
          <RouterLink
            :to="{ name: 'register' }"
            class="rounded-lg bg-success px-3 py-2.5 text-center text-white transition-colors hover:bg-success-hover"
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
