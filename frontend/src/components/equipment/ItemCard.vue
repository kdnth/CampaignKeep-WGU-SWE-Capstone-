<script setup lang="ts">
import { formatRarityLabel, type ItemDetailResponse, type ItemRarity } from '@/stores/inventory'
import {
  BiTools,
  GiAbdominalArmor,
  GiAxeSword,
  GiBigDiamondRing,
  GiClothes,
  GiCrossbow,
  GiRopeCoil,
} from 'oh-vue-icons/icons'
import { OhVueIcon, addIcons } from 'oh-vue-icons'
import { computed, ref } from 'vue'

addIcons(GiAxeSword, GiCrossbow, GiAbdominalArmor, BiTools, GiClothes, GiBigDiamondRing, GiRopeCoil)

const props = defineProps<{
  item: ItemDetailResponse
  quantity?: number
}>()

const isFlipped = ref(false)

const iconName = computed(() => {
  switch (props.item.itemType) {
    case 'weapon':
      return props.item.weaponRange === 'ranged' ? 'gi-crossbow' : 'gi-axe-sword'
    case 'armor':
      return 'gi-abdominal-armor'
    case 'tool':
      return 'bi-tools'
    case 'clothing':
      return 'gi-clothes'
    case 'jewelry':
      return 'gi-big-diamond-ring'
    case 'gear':
    default:
      return 'gi-rope-coil'
  }
})

const rarityClass = computed(() => {
  const map: Record<ItemRarity, string> = {
    common: 'border-neutral-500',
    uncommon: 'border-green-500',
    rare: 'border-blue-500',
    very_rare: 'border-purple-500',
    legendary: 'border-orange-400',
    artifact: 'border-yellow-300',
  }
  return map[props.item.rarity] ?? map.common
})

const itemType = computed(() => {
  const item = props.item

  switch (item.itemType) {
    case 'armor':
      return item.armorCategoryName + ' Armor'
    case 'weapon':
      return item?.weaponCategoryName || '' + ' Weapon'
    case 'clothing':
      return 'Clothing'
    case 'gear':
      return 'Gear'
    case 'jewelry':
      return 'Jewelry'
    case 'tool':
      return 'Tool'
    default:
      return 'Item'
  }
})

function toggleFlip() {
  isFlipped.value = !isFlipped.value
}
</script>

<template>
  <div class="item-flip-scene h-52" @click="toggleFlip">
    <div class="item-flip-inner h-full" :class="{ 'is-flipped': isFlipped }">
      <!-- Front -->
      <div
        class="item-flip-face flex h-full cursor-pointer flex-col items-center justify-center gap-3 rounded-2xl border-2 bg-neutral-900 p-4 text-center"
        :class="rarityClass"
      >
        <OhVueIcon :name="iconName" scale="2.2" class="text-white" />
        <div>
          <h3 class="text-base font-semibold leading-tight text-white">{{ item.name }}</h3>
          <p v-if="quantity && quantity > 1" class="text-xs text-neutral-400">x{{ quantity }}</p>
        </div>
        <p class="text-sm text-neutral-300">{{ item.weight ?? 0 }} lb.</p>
        <p class="text-xs text-neutral-500">Click to flip</p>
      </div>

      <!-- Back -->
      <div
        class="item-flip-face item-flip-back flex h-full cursor-pointer flex-col rounded-2xl border-2 bg-neutral-900 p-4"
        :class="rarityClass"
      >
        <div class="min-h-0 flex-1 space-y-1.5 overflow-y-auto text-sm text-neutral-100">
          <h3 class="font-semibold text-white">{{ item.name }}</h3>
          <p class="text-xs text-neutral-400 italic">
            {{ formatRarityLabel(item.rarity) }} ∙ {{ item.effectiveValue }} gp
            <span
              v-if="item.value != null && item.value !== item.effectiveValue"
              class="text-neutral-500"
            >
              (base {{ item.value }})
            </span>
            ∙ {{ item.weight ?? 0 }} lb.
          </p>
          <p class="capitalize text-neutral-400">
            {{ itemType }}
            <span v-if="item.isMagical"> ∙ Magical</span>
          </p>
          <template v-if="item.itemType === 'weapon'">
            <p v-if="item.damage">
              <span class="text-neutral-400">Damage:</span>
              {{ item.damage }}
              <span v-if="item.damageType"> {{ item.damageType }}</span>
            </p>
            <p v-if="item.weaponRange" class="capitalize">
              <span class="text-neutral-400">Range type:</span>
              {{ item.weaponRange }}
              <span v-if="item.range"> ({{ item.range }} ft.)</span>
            </p>
            <p v-if="item.weaponCategoryName">
              <span class="text-neutral-400">Category:</span>
              {{ item.weaponCategoryName }}
            </p>
          </template>

          <template v-if="item.itemType === 'armor'">
            <p v-if="item.armorCategoryName" class="capitalize">
              <span class="text-neutral-400">Armor:</span>
              {{ item.armorCategoryName }}
            </p>
            <p v-if="item.baseAc != null">
              <span class="text-neutral-400">Base AC:</span>
              {{ item.baseAc }}
            </p>
            <p v-if="item.maxDexBonus != null">
              <span class="text-neutral-400">Max DEX:</span>
              {{ item.maxDexBonus }}
            </p>
            <p v-if="item.strMinimum != null">
              <span class="text-neutral-400">STR Min:</span>
              {{ item.strMinimum }}
            </p>
          </template>

          <p v-if="item.description" class="whitespace-pre-line text-neutral-300">
            {{ item.description }}
          </p>
          <p v-else class="text-neutral-500">No description.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.item-flip-scene {
  perspective: 1000px;
}

.item-flip-inner {
  position: relative;
  transition: transform 0.5s;
  transform-style: preserve-3d;
}

.item-flip-inner.is-flipped {
  transform: rotateY(180deg);
}

.item-flip-face {
  position: absolute;
  inset: 0;
  backface-visibility: hidden;
}

.item-flip-back {
  transform: rotateY(180deg);
}
</style>
