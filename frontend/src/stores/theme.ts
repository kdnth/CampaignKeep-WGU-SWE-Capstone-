import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

export type ThemePreference = 'system' | 'light' | 'dark'
export type ResolvedTheme = 'light' | 'dark'

const STORAGE_KEY = 'theme'

// Single source for theme-dependent brand assets so the navbar and
// favicon can never drift apart.
export const BRAND_LOGOS: Record<ResolvedTheme, string> = {
  light: '/brand/logo-light.png',
  dark: '/brand/logo-dark.png',
}

function readStoredPreference(): ThemePreference {
  const stored = localStorage.getItem(STORAGE_KEY)
  if (stored === 'light' || stored === 'dark' || stored === 'system') {
    return stored
  }
  return 'system'
}

function prefersDark(): boolean {
  return window.matchMedia('(prefers-color-scheme: dark)').matches
}

function resolveTheme(preference: ThemePreference): ResolvedTheme {
  if (preference === 'light') return 'light'
  if (preference === 'dark') return 'dark'
  return prefersDark() ? 'dark' : 'light'
}

function applyDomTheme(resolved: ResolvedTheme) {
  document.documentElement.classList.toggle('dark', resolved === 'dark')

  // Keep the tab icon in sync with the app theme. A <link media="..."> favicon
  // only tracks the OS, so it would ignore the manual override from Settings.
  const favicon = document.getElementById('app-favicon') as HTMLLinkElement | null
  if (favicon) {
    favicon.href = BRAND_LOGOS[resolved]
  }
}

export const useThemeStore = defineStore('theme', () => {
  const preference = ref<ThemePreference>(readStoredPreference())
  const resolved = ref<ResolvedTheme>(resolveTheme(preference.value))

  const isDark = computed(() => resolved.value === 'dark')

  function apply() {
    resolved.value = resolveTheme(preference.value)
    applyDomTheme(resolved.value)
  }

  function setPreference(next: ThemePreference) {
    preference.value = next
    localStorage.setItem(STORAGE_KEY, next)
    apply()
  }

  let mediaQuery: MediaQueryList | null = null

  function init() {
    apply()

    // explicit light/dark choice should not be overridden by the OS so resolve only when preference is system
    mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
    mediaQuery.addEventListener('change', () => {
      if (preference.value === 'system') {
        apply()
      }
    })
  }

  return {
    preference,
    resolved,
    isDark,
    setPreference,
    init,
  }
})
