<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useBackgroundStore } from '@/stores/background'
import { useLanguageStore } from '@/stores/language'
import type { CharacterFormBase } from '@/stores/character'
import BaseButton from '@/components/BaseButton.vue'

const props = defineProps<{ form: CharacterFormBase }>()
const emit = defineEmits(['update:form', 'next', 'back'])

const backgroundStore = useBackgroundStore()
const languageStore = useLanguageStore()

onMounted(async () => {
  await Promise.all([
    backgroundStore.backgrounds.length === 0
      ? backgroundStore.fetchBackgrounds()
      : Promise.resolve(),
    languageStore.languages.length === 0 ? languageStore.fetchLanguages() : Promise.resolve(),
  ])
})

const backgroundId = computed({
  get: () => props.form.backgroundId,
  set: (value) => emit('update:form', { ...props.form, backgroundId: value }),
})

function toggleLanguage(id: number, checked: boolean) {
  const ids = props.form.languageIds ?? []
  const languageIds = checked ? [...ids, id] : ids.filter((languageId) => languageId !== id)
  emit('update:form', { ...props.form, languageIds })
}
</script>

<template>
  <h2 class="text-4xl text-white my-4">Background &amp; Languages</h2>
  <div class="space-y-4 text-white border-2 border-white rounded-2xl p-8">
    <div class="space-y-1">
      <h3 class="text-sm text-neutral-300">Background</h3>
      <div
        v-for="background in backgroundStore.backgrounds"
        :key="background.id"
        class="flex items-center justify-between"
      >
        <span>{{ background.name }}</span>
        <input
          type="radio"
          name="background"
          class="mx-2"
          :value="background.id"
          v-model="backgroundId"
        />
      </div>
    </div>

    <div class="space-y-1">
      <h3 class="text-sm text-neutral-300">Languages</h3>
      <div
        v-for="language in languageStore.languages"
        :key="language.id"
        class="flex items-center justify-between"
      >
        <span>{{ language.name }}</span>
        <input
          type="checkbox"
          class="mx-2"
          :checked="form.languageIds?.includes(language.id)"
          @change="toggleLanguage(language.id, ($event.target as HTMLInputElement).checked)"
        />
      </div>
    </div>
  </div>
  <div class="flex justify-between pt-4">
    <BaseButton variant="cancel" @click="$emit('back')">Back</BaseButton>
    <BaseButton variant="primary" @click="$emit('next')">Next</BaseButton>
  </div>
</template>
