<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import {
  useInventoryStore,
  type EquipmentSlot,
  type InventoryItemResponse,
} from '@/stores/inventory'
import { isAxiosError } from 'axios'
import { computed, onMounted, ref } from 'vue'

const props = defineProps<{
  characterId: number
  strength: number
  readOnly?: boolean
}>()

const emit = defineEmits<{
  equipmentUpdated: []
}>()

const inventoryStore = useInventoryStore()
const isLoading = ref(true)
const errorMessage = ref<string | null>(null)
const actionError = ref<string | null>(null)
const isActing = ref(false)

const inventory = computed(() => inventoryStore.inventory)

const slots: { id: EquipmentSlot; label: string }[] = [
  { id: 'armor', label: 'Armor' },
  { id: 'shield', label: 'Shield' },
  { id: 'main_hand', label: 'Main Hand' },
  { id: 'off_hand', label: 'Off Hand' },
]

function itemInSlot(slot: EquipmentSlot): InventoryItemResponse | undefined {
  return inventory.value?.items.find((i) => i.equippedSlot === slot)
}

function unequippedItems(): InventoryItemResponse[] {
  return inventory.value?.items.filter((i) => !i.equippedSlot) ?? []
}

function canEquipInSlot(item: InventoryItemResponse, slot: EquipmentSlot): boolean {
  if (slot === 'armor' || slot === 'shield') {
    if (item.item.itemType !== 'armor') return false
    const isShield = item.item.armorCategoryName === 'shield'
    if (slot === 'shield' && !isShield) return false
    if (slot === 'armor' && isShield) return false
    if (item.item.strMinimum != null && item.item.strMinimum > props.strength) return false
    return true
  }
  return item.item.itemType === 'weapon'
}

function equipSlotsForItem(item: InventoryItemResponse): EquipmentSlot[] {
  return slots.filter((s) => canEquipInSlot(item, s.id)).map((s) => s.id)
}

function isStackable(item: InventoryItemResponse) {
  return item.item.itemType === 'gear' || item.item.itemType === 'tool'
}

async function runAction(action: () => Promise<void>) {
  actionError.value = null
  isActing.value = true
  try {
    await action()
    emit('equipmentUpdated')
  } catch (err: unknown) {
    actionError.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Action failed')
      : 'Action failed'
  } finally {
    isActing.value = false
  }
}

async function handleEquip(inventoryItemId: number, slot: EquipmentSlot) {
  await runAction(async () => {
    await inventoryStore.equipItem(props.characterId, inventoryItemId, slot)
  })
}

async function handleUnequip(inventoryItemId: number) {
  await runAction(async () => {
    await inventoryStore.unequipItem(props.characterId, inventoryItemId)
  })
}

async function handleRemove(inventoryItemId: number) {
  await runAction(async () => {
    await inventoryStore.removeItem(props.characterId, inventoryItemId)
  })
}

onMounted(async () => {
  try {
    await inventoryStore.fetchInventory(props.characterId)
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to load inventory')
      : 'Failed to load inventory'
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <div class="space-y-6 rounded-2xl border-2 border-border-strong p-6 text-fg">
    <p v-if="isLoading" class="text-fg-subtle">Loading equipment...</p>
    <p v-else-if="errorMessage" class="text-danger">{{ errorMessage }}</p>

    <template v-else-if="inventory">
      <div class="flex flex-wrap gap-6 text-sm">
        <p>
          <span class="text-fg-subtle">Weight:</span>
          {{ inventory.totalWeight }} / {{ inventory.carryingCapacity }} lb.
        </p>
        <p>
          <span class="text-fg-subtle">AC:</span>
          {{ inventory.armorClass }}
        </p>
      </div>

      <div>
        <h3 class="mb-3 text-lg font-medium">Equipped</h3>
        <div class="grid gap-3 sm:grid-cols-2">
          <div
            v-for="slot in slots"
            :key="slot.id"
            class="rounded-lg border border-border p-3"
          >
            <p class="text-xs uppercase text-fg-subtle">{{ slot.label }}</p>
            <template v-if="itemInSlot(slot.id)">
              <p class="font-medium">{{ itemInSlot(slot.id)!.item.name }}</p>
              <BaseButton
                v-if="!readOnly"
                variant="secondary"
                class="mt-2"
                :disabled="isActing"
                @click="handleUnequip(itemInSlot(slot.id)!.inventoryItemId)"
              >
                Unequip
              </BaseButton>
            </template>
            <p v-else class="text-fg-subtle">Empty</p>
          </div>
        </div>
      </div>

      <div>
        <h3 class="mb-3 text-lg font-medium">Inventory</h3>
        <p v-if="unequippedItems().length === 0" class="text-fg-subtle">No items in inventory.</p>
        <div v-else class="space-y-3">
          <div
            v-for="entry in unequippedItems()"
            :key="entry.inventoryItemId"
            class="flex flex-wrap items-center justify-between gap-3 rounded-lg border border-border p-3"
          >
            <div>
              <p class="font-medium">
                {{ entry.item.name }}
                <span v-if="isStackable(entry) && entry.quantity > 1" class="text-fg-subtle">
                  x{{ entry.quantity }}
                </span>
              </p>
              <p class="text-xs text-fg-subtle">
                {{ entry.item.itemType }} · {{ (entry.item.weight ?? 0) * entry.quantity }} lb.
              </p>
            </div>
            <div v-if="!readOnly" class="flex flex-wrap gap-2">
              <BaseButton
                v-for="slot in equipSlotsForItem(entry)"
                :key="slot"
                variant="primary"
                :disabled="isActing"
                @click="handleEquip(entry.inventoryItemId, slot)"
              >
                Equip {{ slot.replace('_', ' ') }}
              </BaseButton>
              <BaseButton
                variant="danger"
                :disabled="isActing"
                @click="handleRemove(entry.inventoryItemId)"
              >
                Remove
              </BaseButton>
            </div>
          </div>
        </div>
      </div>

      <p v-if="actionError" class="text-danger">{{ actionError }}</p>
    </template>
  </div>
</template>
