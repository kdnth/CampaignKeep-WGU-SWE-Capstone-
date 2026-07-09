export type CharacterStatus = 'alive' | 'down' | 'dead'

export function readStoredHp(campaignId: number, characterId: number, maxHp: number): number {
  const raw = localStorage.getItem(`campaignkeep:currentHp:${campaignId}:${characterId}`)
  if (raw === null) return maxHp
  const parsed = Number(raw)
  if (!Number.isFinite(parsed)) return maxHp
  return Math.min(Math.max(0, parsed), maxHp)
}

export function resolveDisplayStatus(
  apiStatus: CharacterStatus,
  currentHp?: number,
): CharacterStatus {
  if (apiStatus === 'dead') return 'dead'
  if (currentHp !== undefined && currentHp === 0) return 'down'
  return apiStatus
}
