import type { MagicSchool } from '@/stores/spell'

export interface SpellSchoolColors {
  bg: string
  border: string
  text: string
  badge: string
}

const SCHOOL_COLORS: Record<MagicSchool, SpellSchoolColors> = {
  abjuration: {
    bg: 'bg-blue-950',
    border: 'border-blue-600',
    text: 'text-blue-200',
    badge: 'bg-blue-600',
  },
  conjuration: {
    bg: 'bg-amber-950',
    border: 'border-amber-500',
    text: 'text-amber-200',
    badge: 'bg-amber-500',
  },
  divination: {
    bg: 'bg-indigo-950',
    border: 'border-indigo-500',
    text: 'text-indigo-200',
    badge: 'bg-indigo-500',
  },
  enchantment: {
    bg: 'bg-rose-950',
    border: 'border-rose-500',
    text: 'text-rose-200',
    badge: 'bg-rose-500',
  },
  evocation: {
    bg: 'bg-orange-950',
    border: 'border-orange-500',
    text: 'text-orange-200',
    badge: 'bg-orange-500',
  },
  illusion: {
    bg: 'bg-fuchsia-950',
    border: 'border-fuchsia-500',
    text: 'text-fuchsia-200',
    badge: 'bg-fuchsia-500',
  },
  necromancy: {
    bg: 'bg-emerald-950',
    border: 'border-emerald-800',
    text: 'text-emerald-200',
    badge: 'bg-emerald-800',
  },
  transmutation: {
    bg: 'bg-stone-900',
    border: 'border-stone-500',
    text: 'text-stone-200',
    badge: 'bg-stone-500',
  },
}

export function useSpellSchoolColor(school: MagicSchool): SpellSchoolColors {
  return SCHOOL_COLORS[school]
}

export function formatSpellLevel(level: number): string {
  return level === 0 ? 'Cantrip' : `Level ${level}`
}

export function formatSpellSchool(school: MagicSchool): string {
  return school.charAt(0).toUpperCase() + school.slice(1)
}

export function formatComponents(
  hasVerbal: boolean,
  hasSomatic: boolean,
  hasMaterial: boolean,
): string {
  const parts: string[] = []
  if (hasVerbal) parts.push('V')
  if (hasSomatic) parts.push('S')
  if (hasMaterial) parts.push('M')
  return parts.length > 0 ? parts.join(', ') : '—'
}

export function formatLevelAndComponents(
  level: number,
  hasVerbal: boolean,
  hasSomatic: boolean,
  hasMaterial: boolean,
): string {
  return `${formatSpellLevel(level)} • ${formatComponents(hasVerbal, hasSomatic, hasMaterial)}`
}

/** Formats casting time for the green cost tag on spell cards. */
export function formatCastingTimeTag(castingTime: string | null | undefined): string | null {
  if (!castingTime?.trim()) return null

  const normalized = castingTime.trim().toLowerCase()
  const hasAction = /\b1 action\b/.test(normalized)
  const hasBonusAction = /\b1 bonus action\b/.test(normalized)

  if (hasAction && hasBonusAction) return 'Action + Bonus action'
  if (hasBonusAction) return 'Bonus action'
  if (hasAction) return 'Action'
  if (normalized === '1 reaction') return 'Reaction'

  return castingTime.trim()
}
