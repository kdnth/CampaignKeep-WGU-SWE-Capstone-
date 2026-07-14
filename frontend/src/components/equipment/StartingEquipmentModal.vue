<script setup lang="ts">
import StepStartingEquipment from '@/components/equipment/StepStartingEquipment.vue'
import BaseButton from '@/components/BaseButton.vue'
import { useInventoryStore, type SubmitStartingEquipmentRequest } from '@/stores/inventory'
import { isAxiosError } from 'axios'
import { ref } from 'vue'

const props = defineProps<{
  characterId: number
  classId: number
  strength: number
}>()

const emit = defineEmits<{
  completed: []
}>()

const inventoryStore = useInventoryStore()
const picks = ref({
  weaponItemId: null as number | null,
  armorItemId: null as number | null,
  shieldItemId: null as number | null,
})
const isSubmitting = ref(false)
const errorMessage = ref<string | null>(null)

async function handleSubmit() {
  if (!picks.value.weaponItemId) return
  isSubmitting.value = true
  errorMessage.value = null
  try {
    const request: SubmitStartingEquipmentRequest = {
      weaponItemId: picks.value.weaponItemId,
      armorItemId: picks.value.armorItemId,
      shieldItemId: picks.value.shieldItemId,
    }
    await inventoryStore.submitStartingEquipment(props.characterId, request)
    emit('completed')
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to save starting equipment')
      : 'Failed to save starting equipment'
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <Teleport to="body">
    <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-4">
      <div class="max-h-[90vh] w-full max-w-2xl overflow-y-auto rounded-2xl bg-neutral-900 p-6">
        <h2 class="mb-2 text-xl font-semibold text-white">Choose Starting Equipment</h2>
        <p class="mb-4 text-sm text-neutral-400">
          Your character needs starting equipment before continuing.
        </p>

        <StepStartingEquipment
          :class-id="classId"
          :strength="strength"
          v-model="picks"
          hide-navigation
        />

        <p v-if="errorMessage" class="mt-4 text-red-400">{{ errorMessage }}</p>

        <div class="mt-4">
          <BaseButton
            variant="primary"
            :loading="isSubmitting"
            :disabled="!picks.weaponItemId"
            @click="handleSubmit"
          >
            Confirm Equipment
          </BaseButton>
        </div>
      </div>
    </div>
  </Teleport>
</template>
