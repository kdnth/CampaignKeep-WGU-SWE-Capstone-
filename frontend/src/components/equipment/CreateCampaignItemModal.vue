<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import {
  formatRarityLabel,
  useInventoryStore,
  type CreateCampaignItemRequest,
  type ItemRarity,
  type ItemType,
} from '@/stores/inventory'
import { isAxiosError } from 'axios'
import { computed, ref } from 'vue'

const props = defineProps<{
  campaignId: number
}>()

const emit = defineEmits<{
  created: []
}>()

const inventoryStore = useInventoryStore()
const isOpen = ref(false)
const isSubmitting = ref(false)
const errorMessage = ref<string | null>(null)

const form = ref<CreateCampaignItemRequest>({
  itemType: 'gear',
  name: '',
  description: '',
  value: 0,
  weight: 1,
  rarity: 'common',
  isMagical: false,
  damage: '1d6',
  damageType: 'slashing',
  range: 5,
  weaponRange: 'melee',
  armorCategoryId: 1,
  baseAc: 11,
})

const itemTypes: ItemType[] = ['weapon', 'armor', 'tool', 'gear', 'jewelry', 'clothing']
const rarities: ItemRarity[] = ['common', 'uncommon', 'rare', 'very_rare', 'legendary', 'artifact']

const rarityMultipliers: Record<ItemRarity, number> = {
  common: 1,
  uncommon: 2,
  rare: 5,
  very_rare: 10,
  legendary: 25,
  artifact: 50,
}

const previewCost = computed(() => {
  const base = form.value.value ?? 0
  const rarity = form.value.rarity ?? 'common'
  return base * rarityMultipliers[rarity]
})

function open() {
  isOpen.value = true
  errorMessage.value = null
}

function close() {
  isOpen.value = false
}

async function handleSubmit() {
  isSubmitting.value = true
  errorMessage.value = null
  try {
    await inventoryStore.createCampaignItem(props.campaignId, form.value)
    emit('created')
    close()
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to create item')
      : 'Failed to create item'
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <div>
    <BaseButton variant="secondary" @click="open">Create Campaign Item</BaseButton>

    <Teleport to="body">
      <div
        v-if="isOpen"
        class="fixed inset-0 z-50 flex items-center justify-center bg-overlay p-4"
        @click.self="close"
      >
        <div
          class="max-h-[90vh] w-full max-w-lg overflow-y-auto rounded-2xl bg-surface p-6 text-fg"
        >
          <h2 class="mb-4 text-xl font-semibold">Create Campaign Item</h2>

          <form class="space-y-4" @submit.prevent="handleSubmit">
            <div>
              <label class="text-sm text-fg-subtle">Type</label>
              <select v-model="form.itemType" class="bg-input mt-1 w-full rounded-lg px-3 py-2 text-fg">
                <option v-for="t in itemTypes" :key="t" :value="t">{{ t }}</option>
              </select>
            </div>

            <div>
              <label class="text-sm text-fg-subtle">Name</label>
              <input
                v-model="form.name"
                class="bg-input mt-1 w-full rounded-lg px-3 py-2 text-fg"
                required
              />
            </div>
            <div>
              <label class="text-sm text-fg-subtle">Description</label>
              <input
                v-model="form.description"
                class="bg-input mt-1 w-full rounded-lg px-3 py-2 text-fg"
              />
            </div>
            <div class="grid grid-cols-2 gap-3">
              <div>
                <label class="text-sm text-fg-subtle">Base Value (gp)</label>
                <input
                  v-model.number="form.value"
                  type="number"
                  min="0"
                  class="bg-input mt-1 w-full rounded-lg px-3 py-2 text-fg"
                />
              </div>
              <div>
                <label class="text-sm text-fg-subtle">Weight (lb.)</label>
                <input
                  v-model.number="form.weight"
                  type="number"
                  min="0"
                  class="bg-input mt-1 w-full rounded-lg px-3 py-2 text-fg"
                  required
                />
              </div>
            </div>

            <div>
              <label class="text-sm text-fg-subtle">Rarity</label>
              <select v-model="form.rarity" class="bg-input mt-1 w-full rounded-lg px-3 py-2 text-fg">
                <option v-for="r in rarities" :key="r" :value="r">
                  {{ formatRarityLabel(r) }} (×{{ rarityMultipliers[r] }})
                </option>
              </select>
              <p class="mt-1 text-xs text-fg-subtle">Effective cost: {{ previewCost }} gp</p>
            </div>

            <label class="flex items-center gap-2 text-sm">
              <input v-model="form.isMagical" type="checkbox" />
              Magical item
            </label>

            <template v-if="form.itemType === 'weapon'">
              <div>
                <label class="text-sm text-fg-subtle">Damage (e.g. 1d8)</label>
                <input v-model="form.damage" class="bg-input mt-1 w-full rounded-lg px-3 py-2 text-fg" />
              </div>
              <div>
                <label class="text-sm text-fg-subtle">Damage Type</label>
                <select
                  v-model="form.damageType"
                  class="bg-input mt-1 w-full rounded-lg px-3 py-2 text-fg"
                >
                  <option value="slashing">slashing</option>
                  <option value="piercing">piercing</option>
                  <option value="bludgeoning">bludgeoning</option>
                </select>
              </div>
              <div>
                <label class="text-sm text-fg-subtle">Range Type</label>
                <select
                  v-model="form.weaponRange"
                  class="bg-input mt-1 w-full rounded-lg px-3 py-2 text-fg"
                >
                  <option value="melee">melee</option>
                  <option value="ranged">ranged</option>
                </select>
              </div>
              <div>
                <label class="text-sm text-fg-subtle">Range (ft.)</label>
                <input
                  v-model.number="form.range"
                  type="number"
                  class="bg-input mt-1 w-full rounded-lg px-3 py-2 text-fg"
                />
              </div>
            </template>

            <template v-if="form.itemType === 'armor'">
              <div>
                <label class="text-sm text-fg-subtle">Armor Category</label>
                <select
                  v-model.number="form.armorCategoryId"
                  class="bg-input mt-1 w-full rounded-lg px-3 py-2 text-fg"
                >
                  <option :value="1">light</option>
                  <option :value="2">medium</option>
                  <option :value="3">heavy</option>
                  <option :value="4">shield</option>
                </select>
              </div>
              <div>
                <label class="text-sm text-fg-subtle">Base AC</label>
                <input
                  v-model.number="form.baseAc"
                  type="number"
                  class="bg-input mt-1 w-full rounded-lg px-3 py-2 text-fg"
                />
              </div>
              <div>
                <label class="text-sm text-fg-subtle">Max DEX Bonus</label>
                <input
                  v-model.number="form.maxDexBonus"
                  type="number"
                  class="bg-input mt-1 w-full rounded-lg px-3 py-2 text-fg"
                />
              </div>
              <div>
                <label class="text-sm text-fg-subtle">STR Minimum</label>
                <input
                  v-model.number="form.strMinimum"
                  type="number"
                  class="bg-input mt-1 w-full rounded-lg px-3 py-2 text-fg"
                />
              </div>
            </template>

            <p v-if="errorMessage" class="text-danger">{{ errorMessage }}</p>

            <div class="flex gap-3">
              <BaseButton type="button" variant="cancel" @click="close">Cancel</BaseButton>
              <BaseButton type="submit" variant="primary" :loading="isSubmitting">
                Create
              </BaseButton>
            </div>
          </form>
        </div>
      </div>
    </Teleport>
  </div>
</template>
