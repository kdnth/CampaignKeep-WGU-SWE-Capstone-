package com.kdnth.campaignkeep.item;

public record CharacterAttackResponse(
        Long inventoryItemId,
        String weaponName,
        int attackBonus,
        String damageDice,
        int damageBonus,
        DamageType damageType,
        WeaponRange weaponRange,
        Short range,
        EquipmentSlot equippedSlot
) {
}
