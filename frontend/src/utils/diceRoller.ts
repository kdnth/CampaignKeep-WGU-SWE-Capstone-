export type DiceType = 'd4' | 'd6' | 'd8' | 'd10' | 'd12' | 'd20' | 'd100'

export const DICE_TYPES: DiceType[] = ['d4', 'd6', 'd8', 'd10', 'd12', 'd20', 'd100']

export type AbilityKey =
  | 'strength'
  | 'dexterity'
  | 'constitution'
  | 'intelligence'
  | 'wisdom'
  | 'charisma'

export interface SkillOption {
  name: string
  ability: AbilityKey
}

export const SKILL_OPTIONS: SkillOption[] = [
  { name: 'Athletics', ability: 'strength' },
  { name: 'Acrobatics', ability: 'dexterity' },
  { name: 'Sleight of Hand', ability: 'dexterity' },
  { name: 'Stealth', ability: 'dexterity' },
  { name: 'Arcana', ability: 'intelligence' },
  { name: 'History', ability: 'intelligence' },
  { name: 'Investigation', ability: 'intelligence' },
  { name: 'Nature', ability: 'intelligence' },
  { name: 'Religion', ability: 'intelligence' },
  { name: 'Animal Handling', ability: 'wisdom' },
  { name: 'Insight', ability: 'wisdom' },
  { name: 'Medicine', ability: 'wisdom' },
  { name: 'Perception', ability: 'wisdom' },
  { name: 'Survival', ability: 'wisdom' },
  { name: 'Deception', ability: 'charisma' },
  { name: 'Intimidation', ability: 'charisma' },
  { name: 'Performance', ability: 'charisma' },
  { name: 'Persuasion', ability: 'charisma' },
]

export function abilityModifier(score: number): number {
  return Math.floor((score - 10) / 2)
}

export function formatModifier(mod: number): string {
  return mod >= 0 ? `+${mod}` : `${mod}`
}

export function rollDie(sides: number): number {
  return Math.floor(Math.random() * sides) + 1
}

export function diceSides(type: DiceType): number {
  return Number(type.slice(1))
}

/** d100 uses tens faces only (10–100), not a flat 1–100 roll. */
export function rollDiceOfType(type: DiceType): number {
  if (type === 'd100') {
    return rollDie(10) * 10
  }
  return rollDie(diceSides(type))
}

export function faceValueForDisplay(_type: DiceType, roll: number): number {
  return roll
}

export function randomFaceForAnimation(type: DiceType): number {
  return rollDiceOfType(type)
}

export type RollMode = 'normal' | 'advantage' | 'disadvantage'

export interface SingleDieOutcome {
  value: number
  face: number
  kept: boolean
}

export interface DiceRollOutcome {
  diceType: DiceType
  quantity: number
  mode: RollMode
  dice: SingleDieOutcome[]
  diceTotal: number
  modifier: number
  total: number
  skillName: string | null
  abilityKey: AbilityKey | null
}

export function resolveKeep(
  values: number[],
  mode: RollMode,
): boolean[] {
  if (mode === 'normal' || values.length < 2) {
    return values.map(() => true)
  }
  if (mode === 'advantage') {
    const best = Math.max(...values)
    return values.map((v, i) => v === best && values.indexOf(best) === i)
  }
  const worst = Math.min(...values)
  return values.map((v, i) => v === worst && values.indexOf(worst) === i)
}

export function performRoll(options: {
  diceType: DiceType
  quantity: number
  mode: RollMode
  modifier: number
  skillName?: string | null
  abilityKey?: AbilityKey | null
}): DiceRollOutcome {
  const { diceType, quantity, mode, modifier } = options
  const effectiveMode = diceType === 'd20' ? mode : 'normal'

  const rollCount =
    effectiveMode !== 'normal' && diceType === 'd20' ? 2 : Math.max(1, quantity)

  const values = Array.from({ length: rollCount }, () => rollDiceOfType(diceType))
  const keptFlags = resolveKeep(values, effectiveMode)

  const dice: SingleDieOutcome[] = values.map((value, i) => ({
    value,
    face: faceValueForDisplay(diceType, value),
    kept: keptFlags[i] ?? true,
  }))

  const diceTotal = dice.filter((d) => d.kept).reduce((sum, d) => sum + d.value, 0)

  return {
    diceType,
    quantity: rollCount,
    mode: effectiveMode,
    dice,
    diceTotal,
    modifier,
    total: diceTotal + modifier,
    skillName: options.skillName ?? null,
    abilityKey: options.abilityKey ?? null,
  }
}

export function parseDiceNotation(notation: string): { count: number; sides: number } | null {
  const match = notation.trim().match(/^(\d+)d(\d+)$/i)
  if (!match) return null
  return { count: Number(match[1]), sides: Number(match[2]) }
}

export function rollDice(notation: string): number {
  const parsed = parseDiceNotation(notation)
  if (!parsed) return 0
  let total = 0
  for (let i = 0; i < parsed.count; i++) {
    total += rollDie(parsed.sides)
  }
  return total
}

export interface DamageRollResult {
  rolls: number[]
  diceTotal: number
  damageBonus: number
  total: number
}

export function rollDamage(
  damageDice: string,
  numberOfAttacks: number,
  damageBonus: number,
): DamageRollResult {
  const rolls: number[] = []
  for (let i = 0; i < numberOfAttacks; i++) {
    rolls.push(rollDice(damageDice))
  }
  const diceTotal = rolls.reduce((sum, r) => sum + r, 0)
  const total = diceTotal + damageBonus * numberOfAttacks
  return { rolls, diceTotal, damageBonus, total }
}
