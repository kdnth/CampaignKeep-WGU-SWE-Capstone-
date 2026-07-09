<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import {
  formatCastingTimeTag,
  formatLevelAndComponents,
  formatSpellSchool,
  useSpellSchoolColor,
} from '@/composables/useSpellSchoolColor'
import type { SpellDetailResponse, SpellSummaryResponse } from '@/stores/spell'
import { computed, ref } from 'vue'

const props = withDefaults(
  defineProps<{
    spell: SpellSummaryResponse | SpellDetailResponse
    mode?: 'browse' | 'spellbook' | 'select'
    isInSpellbook?: boolean
    isSelected?: boolean
    isDisabled?: boolean
    isLoadingAction?: boolean
  }>(),
  {
    mode: 'browse',
    isInSpellbook: false,
    isSelected: false,
    isDisabled: false,
    isLoadingAction: false,
  },
)

const emit = defineEmits<{
  add: []
  remove: []
  toggleSelect: []
}>()

const isFlipped = ref(false)
const colors = computed(() => useSpellSchoolColor(props.spell.school))

const detailSpell = computed(() => props.spell as SpellDetailResponse)

const hasDetailFields = computed(
  () => 'description' in props.spell && props.spell.description !== undefined,
)

const levelAndComponents = computed(() =>
  formatLevelAndComponents(
    props.spell.level,
    props.spell.hasVerbal,
    props.spell.hasSomatic,
    props.spell.hasMaterial,
  ),
)

const castingTimeTag = computed(() => formatCastingTimeTag(props.spell.castingTime))

function handleCardClick() {
  isFlipped.value = !isFlipped.value
}

function handleAction(event: Event) {
  event.stopPropagation()
  if (props.mode === 'select') {
    emit('toggleSelect')
    return
  }
  if (props.isInSpellbook) {
    emit('remove')
  } else {
    emit('add')
  }
}
</script>

<template>
  <div class="spell-flip-scene h-60" @click="handleCardClick">
    <div class="spell-flip-inner h-full" :class="{ 'is-flipped': isFlipped }">
      <!-- Front -->
      <div
        class="spell-flip-face flex h-full cursor-pointer flex-col justify-between rounded-2xl border-2 bg-neutral-900 p-4"
        :class="colors.border"
      >
        <div class="space-y-2">
          <div class="flex items-start justify-between gap-2">
            <h3 class="text-lg font-semibold leading-tight text-white">{{ spell.name }}</h3>
            <span
              class="shrink-0 rounded-full px-2 py-0.5 text-xs font-semibold text-white"
              :class="colors.badge"
            >
              {{ formatSpellSchool(spell.school) }}
            </span>
          </div>

          <p class="text-sm text-neutral-300">{{ levelAndComponents }}</p>

          <span
            v-if="castingTimeTag"
            class="inline-block rounded-md bg-green-800 px-2 py-0.5 text-xs font-medium text-green-100"
          >
            {{ castingTimeTag }}
          </span>

          <div class="space-y-0.5 text-xs text-neutral-400">
            <p v-if="spell.concentration">Requires Concentration</p>
            <p v-if="spell.ritual">Ritual Spell</p>
          </div>

          <span
            v-if="mode === 'select' && isSelected"
            class="inline-block rounded bg-white/20 px-2 py-0.5 text-xs text-white"
          >
            Selected
          </span>
        </div>

        <p class="text-center text-xs text-neutral-500">Click to flip</p>
      </div>

      <!-- Back -->
      <div
        class="spell-flip-face spell-flip-back flex h-full cursor-pointer flex-col rounded-2xl border-2 bg-neutral-900 p-4"
        :class="colors.border"
      >
        <div class="min-h-0 flex-1 space-y-2 overflow-y-auto text-sm text-neutral-100">
          <h3 class="font-semibold text-white">{{ spell.name }}</h3>

          <template v-if="hasDetailFields">
            <p v-if="detailSpell.rangeDisplay">
              <span class="text-neutral-400">Range:</span> {{ detailSpell.rangeDisplay }}
            </p>
            <p v-if="detailSpell.duration">
              <span class="text-neutral-400">Duration:</span> {{ detailSpell.duration }}
            </p>
            <p v-if="detailSpell.materialDesc">
              <span class="text-neutral-400">Material:</span> {{ detailSpell.materialDesc }}
            </p>
            <p v-if="detailSpell.description" class="whitespace-pre-line">
              {{ detailSpell.description }}
            </p>
            <p v-if="detailSpell.higherLevel" class="whitespace-pre-line text-neutral-300">
              <span class="text-neutral-400">At Higher Levels:</span>
              {{ detailSpell.higherLevel }}
            </p>
          </template>
          <p v-else class="text-neutral-400">Loading details...</p>
        </div>

        <div v-if="mode !== 'browse'" class="mt-3 shrink-0" @click.stop>
          <BaseButton
            v-if="mode === 'select'"
            :variant="isSelected ? 'danger' : 'primary'"
            :disabled="isDisabled"
            class="w-full"
            @click="handleAction"
          >
            {{ isSelected ? 'Deselect' : 'Select' }}
          </BaseButton>
          <BaseButton
            v-else
            :variant="isInSpellbook ? 'danger' : 'primary'"
            :loading="isLoadingAction"
            :disabled="isLoadingAction"
            class="w-full"
            @click="handleAction"
          >
            {{ isInSpellbook ? 'Remove from Spellbook' : 'Add to Spellbook' }}
          </BaseButton>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.spell-flip-scene {
  perspective: 1000px;
}

.spell-flip-inner {
  position: relative;
  transition: transform 0.5s;
  transform-style: preserve-3d;
}

.spell-flip-inner.is-flipped {
  transform: rotateY(180deg);
}

.spell-flip-face {
  position: absolute;
  inset: 0;
  backface-visibility: hidden;
}

.spell-flip-back {
  transform: rotateY(180deg);
}
</style>
