<script setup lang="ts">
import { useCharacterAvatar } from '@/composables/useCharacterAvatar'
import { computed } from 'vue'

const props = withDefaults(
  defineProps<{
    raceName: string
    className: string
    size?: 'sm' | 'md' | 'lg'
  }>(),
  { size: 'md' },
)

const { avatarUrl } = useCharacterAvatar(
  () => props.raceName,
  () => props.className,
)

const sizeClass = computed(() => {
  switch (props.size) {
    case 'sm':
      return 'w-16'
    case 'lg':
      return 'w-48'
    default:
      return 'w-32'
  }
})
</script>

<template>
  <img
    :src="avatarUrl"
    alt="Character Avatar"
    :class="[sizeClass, 'h-auto object-cover rounded-lg']"
  />
</template>
