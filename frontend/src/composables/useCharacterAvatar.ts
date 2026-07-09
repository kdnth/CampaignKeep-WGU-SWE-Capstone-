import { computed, type MaybeRefOrGetter, toValue } from 'vue'
import humanFighter from '@/assets/default-avatars/human-fighter.png'
import elfFighter from '@/assets/default-avatars/elf-fighter.png'
import dwarfFighter from '@/assets/default-avatars/dwarf-fighter.png'
import humanWizard from '@/assets/default-avatars/human-wizard.png'
import elfWizard from '@/assets/default-avatars/elf-wizard.png'
import dwarfWizard from '@/assets/default-avatars/dwarf-wizard.png'
import humanRogue from '@/assets/default-avatars/human-rogue.png'
import elfRogue from '@/assets/default-avatars/elf-rogue.png'
import dwarfRogue from '@/assets/default-avatars/dwarf-rogue.png'

const avatarMap: Record<string, Record<string, string>> = {
  human: { fighter: humanFighter, wizard: humanWizard, rogue: humanRogue },
  elf: { fighter: elfFighter, wizard: elfWizard, rogue: elfRogue },
  dwarf: { fighter: dwarfFighter, wizard: dwarfWizard, rogue: dwarfRogue },
}

export function resolveCharacterAvatar(raceName: string, className: string): string {
  const race = raceName.toLowerCase()
  const cls = className.toLowerCase()
  if (cls === 'npc') {
    return avatarMap[race]?.fighter ?? humanFighter
  }
  return avatarMap[race]?.[cls] ?? avatarMap[race]?.fighter ?? humanFighter
}

export function useCharacterAvatar(
  raceName: MaybeRefOrGetter<string>,
  className: MaybeRefOrGetter<string>,
) {
  const avatarUrl = computed(() =>
    resolveCharacterAvatar(toValue(raceName), toValue(className)),
  )
  return { avatarUrl }
}
