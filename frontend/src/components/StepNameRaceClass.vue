<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRaceStore } from '@/stores/race'
import { useDndClassStore } from '@/stores/dndClass'
import type { CreatePlayableCharacterRequest } from '@/stores/character'
import BaseButton from '@/components/BaseButton.vue'

const props = defineProps<{ form: CreatePlayableCharacterRequest }>()
const emit = defineEmits(['update:form', 'next'])

const raceStore = useRaceStore()
const dndClassStore = useDndClassStore()

onMounted(async () => {
  if (raceStore.races.length === 0) await raceStore.fetchRaces()
  if (dndClassStore.classes.length === 0) await dndClassStore.fetchClasses()
})

const name = computed({
  get: () => props.form.name,
  set: (value) => emit('update:form', { ...props.form, name: value }),
})

const raceId = computed({
  get: () => props.form.raceId,
  set: (value) => emit('update:form', { ...props.form, raceId: value }),
})

const classId = computed({
  get: () => props.form.classId,
  set: (value) => emit('update:form', { ...props.form, classId: value }),
})

const canProceed = () => name.value.trim().length > 0 && raceId.value > 0 && classId.value > 0
</script>

<template>
  <h2 class="text-4xl text-white my-4">Basic Character Information</h2>
  <div class="space-y-4 text-white border-2 border-white rounded-2xl p-8">
    <div class="space-y-1">
      <label for="name">Name</label>
      <input
        class="text-black rounded-lg mx-2 cursor-auto"
        id="name"
        v-model="name"
        placeholder="Character Name"
      />
    </div>

    <div class="space-y-1">
      <label for="race">Race</label>
      <select class="rounded-lg mx-2 text-neutral-800" id="race" v-model.number="raceId">
        <option :value="0" disabled>Select a race</option>
        <option v-for="race in raceStore.races" :key="race.id" :value="race.id">
          {{ race.name }}
        </option>
      </select>
    </div>

    <div class="space-y-1">
      <label for="class">Class</label>
      <select class="rounded-lg mx-2 text-neutral-800" id="class" v-model.number="classId">
        <option :value="0" disabled>Select a class</option>
        <option v-for="dndClass in dndClassStore.classes" :key="dndClass.id" :value="dndClass.id">
          {{ dndClass.name }}
        </option>
      </select>
    </div>
  </div>
  <div class="flex justify-end pt-4">
    <BaseButton variant="primary" :disabled="!canProceed()" @click="$emit('next')">
      Next
    </BaseButton>
  </div>
</template>
