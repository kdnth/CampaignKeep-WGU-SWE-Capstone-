<script setup lang="ts">
import BackToLink from '@/components/BackToLink.vue'
import { useThemeStore, type ThemePreference } from '@/stores/theme'

const themeStore = useThemeStore()

const options: { value: ThemePreference; label: string }[] = [
  {
    value: 'system',
    label: 'System',
  },
  {
    value: 'light',
    label: 'Light',
  },
  {
    value: 'dark',
    label: 'Dark',
  },
]
</script>

<template>
  <div class="flex w-full flex-col gap-4 p-8">
    <BackToLink page="home" link-name="Home" />
    <h1 class="text-3xl text-fg">Settings</h1>
    <p class="text-fg-subtle">Manage your account and preferences.</p>

    <section class="mt-4 w-full flex flex-col gap-3 rounded-2xl bg-surface p-6">
      <h2 class="text-xl text-fg">Theme</h2>

      <div class="mt-2 flex flex-col gap-2" role="radiogroup" aria-label="Color theme">
        <label
          v-for="option in options"
          :key="option.value"
          class="flex cursor-pointer items-start gap-3 rounded-xl border border-border px-4 py-3 transition-colors hover:bg-surface-muted"
          :class="themeStore.preference === option.value ? 'border-accent bg-accent-muted' : ''"
        >
          <input
            type="radio"
            name="theme"
            class="mt-1"
            :value="option.value"
            :checked="themeStore.preference === option.value"
            @change="themeStore.setPreference(option.value)"
          />
          <span>
            <span class="block font-medium text-fg">{{ option.label }}</span>
          </span>
        </label>
      </div>

      <p class="mt-2 text-xs text-fg-subtle">
        Active theme:
        <span class="capitalize text-fg-muted">{{ themeStore.resolved }}</span>
        <template v-if="themeStore.preference === 'system'"> (from system)</template>
      </p>
    </section>
  </div>
</template>
