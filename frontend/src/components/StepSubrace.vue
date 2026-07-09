<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useSubraceStore, type SubraceSummaryResponse } from '@/stores/subrace'
import type { CharacterFormBase } from '@/stores/character'
import BaseButton from '@/components/BaseButton.vue'

const props = defineProps<{
  form: CharacterFormBase
  subraces?: SubraceSummaryResponse[]
}>()
const emit = defineEmits(['update:form', 'next', 'back'])

const subraceStore = useSubraceStore()

onMounted(async () => {
  if (subraceStore.subraces.length === 0) await subraceStore.fetchSubraces()
})

const subraces = computed(() => subraceStore.subraces.filter((s) => s.raceId === props.form.raceId))

const subraceId = computed({
  get: () => props.form.subraceId,
  set: (value) => emit('update:form', { ...props.form, subraceId: value }),
})
</script>

<template>
  <h2 class="text-4xl text-white my-4">Subrace</h2>
  <div class="space-y-4 text-white border-2 border-white rounded-2xl p-8">
    <div class="flex items-center justify-between">
      <span>None</span>
      <input type="radio" name="subrace" class="mx-2" :value="null" v-model="subraceId" />
    </div>
    <div v-for="subrace in subraces" :key="subrace.id" class="flex items-center justify-between">
      <span>{{ subrace.name }}</span>
      <input type="radio" name="subrace" class="mx-2" :value="subrace.id" v-model="subraceId" />
    </div>
  </div>
  <div class="flex justify-between pt-4">
    <BaseButton variant="cancel" @click="$emit('back')">Back</BaseButton>
    <BaseButton variant="primary" @click="$emit('next')">Next</BaseButton>
  </div>
</template>
