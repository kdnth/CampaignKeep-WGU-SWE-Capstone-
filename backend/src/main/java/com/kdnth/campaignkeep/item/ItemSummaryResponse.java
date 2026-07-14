package com.kdnth.campaignkeep.item;

public record ItemSummaryResponse(
        Long id,
        ItemType itemType,
        String name,
        String description,
        Integer value,
        Integer effectiveValue,
        Short weight,
        ItemRarity rarity,
        Boolean isMagical,
        WeaponRange weaponRange,
        Long campaignId
) {
}
