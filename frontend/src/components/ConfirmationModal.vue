<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'

withDefaults(
  defineProps<{
    title: string
    message: string
    confirmLabel?: string
    cancelLabel?: string
    variant?: 'primary' | 'danger'
    isLoading?: boolean
  }>(),
  {
    confirmLabel: 'Confirm',
    cancelLabel: 'Cancel',
    variant: 'primary',
    isLoading: false,
  },
)

const emit = defineEmits<{
  confirm: []
  cancel: []
}>()
</script>

<template>
  <Teleport to="body">
    <div
      class="fixed inset-0 bg-black/70 flex items-center justify-center z-50"
      @click.self="emit('cancel')"
    >
      <div class="bg-white rounded-xl p-6 w-full max-w-sm flex flex-col gap-4">
        <h3 class="text-xl">{{ title }}</h3>
        <p class="text-neutral-700">{{ message }}</p>

        <div class="flex justify-end gap-2">
          <BaseButton variant="cancel" type="button" @click="emit('cancel')">
            {{ cancelLabel }}
          </BaseButton>
          <BaseButton
            :variant="variant"
            type="button"
            :loading="isLoading"
            @click="emit('confirm')"
          >
            {{ isLoading ? 'Working...' : confirmLabel }}
          </BaseButton>
        </div>
      </div>
    </div>
  </Teleport>
</template>
