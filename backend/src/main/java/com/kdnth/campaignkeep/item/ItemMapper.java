package com.kdnth.campaignkeep.item;

import org.hibernate.Hibernate;

public final class ItemMapper {

    private ItemMapper() {}

    public static ItemDetailResponse toDetail(Item item) {
        Item resolved = (Item) Hibernate.unproxy(item);

        if (resolved instanceof Weapon weapon) {
            return baseDetail(
                    weapon,
                    ItemType.weapon,
                    weapon.getWeaponCategory() != null ? weapon.getWeaponCategory().getName() : null,
                    weapon.getDamage(),
                    weapon.getDamageType(),
                    weapon.getRange(),
                    weapon.getWeaponRange(),
                    null, null, null, null
            );
        }

        if (resolved instanceof Armor armor) {
            return baseDetail(
                    armor,
                    ItemType.armor,
                    null, null, null, null, null,
                    armor.getArmorCategoryName(),
                    armor.getBaseAc(),
                    armor.getMaxDexBonus(),
                    armor.getStrMinimum()
            );
        }

        ItemType type = resolved.getItemType() != null ? resolved.getItemType() : ItemType.gear;
        return baseDetail(resolved, type, null, null, null, null, null, null, null, null, null);
    }

    public static ItemSummaryResponse toSummary(Item item) {
        Item resolved = (Item) Hibernate.unproxy(item);
        WeaponRange weaponRange = null;
        if (resolved instanceof Weapon weapon) {
            weaponRange = weapon.getWeaponRange();
        }
        return new ItemSummaryResponse(
                resolved.getId(),
                resolved.getItemType(),
                resolved.getName(),
                resolved.getDescription(),
                resolved.getValue(),
                resolved.getEffectiveValue(),
                resolved.getWeight(),
                resolved.getRarity() != null ? resolved.getRarity() : ItemRarity.common,
                Boolean.TRUE.equals(resolved.getIsMagical()),
                weaponRange,
                resolved.getCampaign() != null ? resolved.getCampaign().getId() : null
        );
    }

    private static ItemDetailResponse baseDetail(
            Item item,
            ItemType type,
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
        return new ItemDetailResponse(
                item.getId(),
                type,
                item.getName(),
                item.getDescription(),
                item.getValue(),
                item.getEffectiveValue(),
                item.getWeight(),
                item.getRarity() != null ? item.getRarity() : ItemRarity.common,
                Boolean.TRUE.equals(item.getIsMagical()),
                item.getApiIndex(),
                item.getCampaign() != null ? item.getCampaign().getId() : null,
                weaponCategoryName,
                damage,
                damageType,
                range,
                weaponRange,
                armorCategoryName,
                baseAc,
                maxDexBonus,
                strMinimum
        );
    }
}
