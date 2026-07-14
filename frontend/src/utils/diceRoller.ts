export function rollDie(sides: number): number {
  return Math.floor(Math.random() * sides) + 1
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

export function formatModifier(mod: number): string {
  return mod >= 0 ? `+${mod}` : `${mod}`
}
