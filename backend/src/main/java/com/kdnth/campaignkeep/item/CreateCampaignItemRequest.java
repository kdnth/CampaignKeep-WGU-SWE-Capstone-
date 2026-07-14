package com.kdnth.campaignkeep.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCampaignItemRequest(
        @NotNull ItemType itemType,
        @NotBlank @Size(max = 75) String name,
        @Size(max = 500) String description,
        Integer value,
        @NotNull Short weight,
        ItemRarity rarity,
        Boolean isMagical,
        Long weaponCategoryId,
        @Size(max = 10) String damage,
        DamageType damageType,
        Short range,
        WeaponRange weaponRange,
        Long armorCategoryId,
        Short baseAc,
        Short maxDexBonus,
        Short strMinimum
) {
}
