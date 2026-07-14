package com.kdnth.campaignkeep.item;

public record ItemDetailResponse(
        Long id,
        ItemType itemType,
        String name,
        String description,
        Integer value,
        Integer effectiveValue,
        Short weight,
        ItemRarity rarity,
        Boolean isMagical,
        String apiIndex,
        Long campaignId,
        String weaponCategoryName,
        String damage,
        DamageType damageType,
        Short range,
        WeaponRange weaponRange,
        String armorCategoryName,
        Short baseAc,
        Short maxDexBonus,
        Short strMinimum
) {
}
