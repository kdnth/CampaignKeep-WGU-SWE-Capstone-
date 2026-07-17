<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRaceStore } from '@/stores/race'
import type { CreateNonplayableCharacterRequest } from '@/stores/character'
import BaseButton from '@/components/BaseButton.vue'

const props = defineProps<{ form: CreateNonplayableCharacterRequest }>()
const emit = defineEmits(['update:form', 'next'])

const raceStore = useRaceStore()

onMounted(async () => {
  if (raceStore.races.length === 0) await raceStore.fetchRaces()
})

const name = computed({
  get: () => props.form.name,
  set: (value) => emit('update:form', { ...props.form, name: value }),
})

const raceId = computed({
  get: () => props.form.raceId,
  set: (value) => emit('update:form', { ...props.form, raceId: value }),
})

const canProceed = () => name.value.trim().length > 0 && raceId.value > 0
</script>

<template>
  <h2 class="text-4xl text-fg my-4">Basic NPC Information</h2>
  <div class="space-y-4 text-fg border-2 border-border-strong rounded-2xl p-8">
    <div class="space-y-1">
      <label for="npc-name">Name</label>
      <input
        class="bg-input text-fg rounded-lg mx-2 cursor-auto"
        id="npc-name"
        v-model="name"
        placeholder="NPC Name"
      />
    </div>

    <div class="space-y-1">
      <label for="npc-race">Race</label>
      <select class="bg-input rounded-lg mx-2 text-fg" id="npc-race" v-model.number="raceId">
        <option :value="0" disabled>Select a race</option>
        <option v-for="race in raceStore.races" :key="race.id" :value="race.id">
          {{ race.name }}
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
