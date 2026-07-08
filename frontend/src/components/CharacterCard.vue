<script setup lang="ts">
import { computed } from 'vue'
import humanFighter from '@/assets/default-avatars/human-fighter.png'
import elfFighter from '@/assets/default-avatars/elf-fighter.png'
import dwarfFighter from '@/assets/default-avatars/dwarf-fighter.png'
import humanWizard from '@/assets/default-avatars/human-wizard.png'
import elfWizard from '@/assets/default-avatars/elf-wizard.png'
import dwarfWizard from '@/assets/default-avatars/dwarf-wizard.png'
import humanRogue from '@/assets/default-avatars/human-rogue.png'
import elfRogue from '@/assets/default-avatars/elf-rogue.png'
import dwarfRogue from '@/assets/default-avatars/dwarf-rogue.png'

const props = defineProps<{
  name: string
  raceName: string
  className: string
  campaigns: string[]
}>()

const avatarMap: Record<string, Record<string, string>> = {
  human: { fighter: humanFighter, wizard: humanWizard, rogue: humanRogue },
  elf: { fighter: elfFighter, wizard: elfWizard, rogue: elfRogue },
  dwarf: { fighter: dwarfFighter, wizard: dwarfWizard, rogue: dwarfRogue },
}

const avatarUrl = computed(() => {
  const race = props.raceName.toLowerCase()
  const className = props.className.toLowerCase()
  return avatarMap[race]?.[className] ?? avatarMap[race]?.fighter ?? humanFighter
})
</script>

<template>
  <div
    class="w-full h-64 grid grid-rows-5 bg-white cursor-pointer rounded-xl overflow-hidden transition-colors hover:border-4 hover:border-purple-600"
  >
    <div class="row-span-3 bg-purple-400 flex justify-center items-center">
      <img :src="avatarUrl" alt="Character Avatar" class="w-32 h-auto object-cover rounded-lg" />
    </div>
    <div class="justify-start row-span-2 p-2">
      <div class="min-w-0 flex flex-col">
        <h2 class="text-xl truncate">{{ name }}</h2>
        <h4 class="text-sm capitalize">{{ raceName }} - {{ className }} - Lvl. 1</h4>
        <!-- tag list of campaigns -->
        <div class="flex flex-wrap gap-1 mt-1">
          <span
            v-for="(campaign, index) in campaigns"
            :key="index"
            class="bg-purple-200 text-purple-800 text-xs font-semibold mr-2 px-2.5 py-0.5 rounded-lg"
          >
            {{ campaign }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
