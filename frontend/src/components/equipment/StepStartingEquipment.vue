<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import type { ItemDetailResponse } from '@/stores/inventory'
import { useInventoryStore } from '@/stores/inventory'
import { isAxiosError } from 'axios'
import { computed, onMounted, ref, watch } from 'vue'

const props = defineProps<{
  classId: number
  strength: number
  hideNavigation?: boolean
  modelValue?: {
    weaponItemId: number | null
    armorItemId: number | null
    shieldItemId: number | null
  }
}>()

const emit = defineEmits<{
  next: []
  back: []
  'update:modelValue': [
    value: {
      weaponItemId: number | null
      armorItemId: number | null
      shieldItemId: number | null
    },
  ]
}>()

const inventoryStore = useInventoryStore()
const isLoading = ref(true)
const errorMessage = ref<string | null>(null)

const weaponItemId = ref<number | null>(props.modelValue?.weaponItemId ?? null)
const armorItemId = ref<number | null>(props.modelValue?.armorItemId ?? null)
const shieldItemId = ref<number | null>(props.modelValue?.shieldItemId ?? null)

const options = computed(() => inventoryStore.startingOptions?.optionsByGroup ?? {})

const weaponOptions = computed(() => options.value['weapon'] ?? [])
const armorOptions = computed(() => options.value['armor'] ?? [])
const shieldOptions = computed(() => options.value['shield'] ?? [])

const hasArmorOptions = computed(() => armorOptions.value.length > 0)

function isArmorDisabled(item: ItemDetailResponse) {
  return item.strMinimum != null && item.strMinimum > props.strength
}

function syncModel() {
  emit('update:modelValue', {
    weaponItemId: weaponItemId.value,
    armorItemId: armorItemId.value,
    shieldItemId: shieldItemId.value,
  })
}

watch([weaponItemId, armorItemId, shieldItemId], syncModel)

const canContinue = computed(() => {
  if (!weaponItemId.value) return false
  if (hasArmorOptions.value && !armorItemId.value) return false
  return true
})

function handleNext() {
  syncModel()
  emit('next')
}

onMounted(async () => {
  try {
    await inventoryStore.fetchStartingOptions(props.classId)
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to load starting equipment options')
      : 'Failed to load starting equipment options'
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <div class="space-y-6 text-white">
    <h2 class="text-xl font-semibold">Starting Equipment</h2>
    <p class="text-sm text-neutral-400">
      Choose your class starting gear. Armor with a strength requirement is disabled if you don't
      meet it.
    </p>

    <p v-if="isLoading" class="text-neutral-400">Loading options...</p>
    <p v-else-if="errorMessage" class="text-red-400">{{ errorMessage }}</p>

    <template v-else>
      <div>
        <label class="mb-2 block text-sm font-medium">Weapon (required)</label>
        <div class="space-y-2">
          <label
            v-for="item in weaponOptions"
            :key="item.id"
            class="flex cursor-pointer items-center gap-3 rounded-lg border border-neutral-600 px-3 py-2 hover:bg-neutral-800"
          >
            <input v-model="weaponItemId" type="radio" :value="item.id" />
            <span>{{ item.name }}</span>
          </label>
        </div>
      </div>

      <div v-if="hasArmorOptions">
        <label class="mb-2 block text-sm font-medium">Armor (required)</label>
        <div class="space-y-2">
          <label
            v-for="item in armorOptions"
            :key="item.id"
            class="flex items-center gap-3 rounded-lg border border-neutral-600 px-3 py-2"
            :class="isArmorDisabled(item) ? 'cursor-not-allowed opacity-50' : 'cursor-pointer hover:bg-neutral-800'"
          >
            <input
              v-model="armorItemId"
              type="radio"
              :value="item.id"
              :disabled="isArmorDisabled(item)"
            />
            <span>
              {{ item.name }}
              <span v-if="item.strMinimum" class="text-xs text-neutral-400">
                (STR {{ item.strMinimum }})
              </span>
            </span>
          </label>
        </div>
      </div>

      <div v-if="shieldOptions.length">
        <label class="mb-2 block text-sm font-medium">Shield (optional)</label>
        <div class="space-y-2">
          <label
            class="flex cursor-pointer items-center gap-3 rounded-lg border border-neutral-600 px-3 py-2 hover:bg-neutral-800"
          >
            <input v-model="shieldItemId" type="radio" :value="null" />
            <span>None</span>
          </label>
          <label
            v-for="item in shieldOptions"
            :key="item.id"
            class="flex cursor-pointer items-center gap-3 rounded-lg border border-neutral-600 px-3 py-2 hover:bg-neutral-800"
          >
            <input v-model="shieldItemId" type="radio" :value="item.id" />
            <span>{{ item.name }}</span>
          </label>
        </div>
      </div>

      <div v-if="!hideNavigation" class="flex gap-3">
        <BaseButton variant="secondary" @click="emit('back')">Back</BaseButton>
        <BaseButton variant="primary" :disabled="!canContinue" @click="handleNext">
          Continue
        </BaseButton>
      </div>
    </template>
  </div>
</template>
