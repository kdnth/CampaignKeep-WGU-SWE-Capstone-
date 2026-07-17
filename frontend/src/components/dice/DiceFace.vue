<script setup lang="ts">
import { faceValueForDisplay, type DiceType } from '@/utils/diceRoller'
import { computed } from 'vue'

const diceUrlModules = import.meta.glob('@/assets/dice/**/*.svg', {
  eager: true,
  query: '?url',
  import: 'default',
}) as Record<string, string>

const props = withDefaults(
  defineProps<{
    diceType: DiceType
    value: number
    dimmed?: boolean
    size?: 'sm' | 'md' | 'lg'
  }>(),
  { dimmed: false, size: 'md' },
)

const face = computed(() => faceValueForDisplay(props.diceType, props.value))

const src = computed(() => {
  const needle = `/${props.diceType}/Face=${face.value}.svg`
  const entry = Object.entries(diceUrlModules).find(([path]) => path.includes(needle))
  return entry?.[1] ?? ''
})

const sizeClass = computed(() => {
  switch (props.size) {
    case 'sm':
      return 'h-16 w-16'
    case 'lg':
      return 'h-28 w-28'
    default:
      return 'h-20 w-20'
  }
})
</script>

<template>
  <img
    v-if="src"
    :src="src"
    :alt="`${diceType} showing ${face}`"
    class="object-contain transition-opacity duration-150"
    :class="[sizeClass, dimmed ? 'opacity-35' : 'opacity-100']"
  />
  <div
    v-else
    class="flex items-center justify-center rounded-lg bg-surface-muted text-sm font-semibold text-fg"
    :class="sizeClass"
  >
    {{ face }}
  </div>
</template>
