package com.kdnth.campaignkeep.item;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

final class EquipmentMapper {

    private EquipmentMapper() {}

    static String joinDesc(List<String> desc) {
        if (desc == null || desc.isEmpty()) {
            return null;
        }
        String joined = String.join("\n", desc);
        return joined.length() > 500 ? joined.substring(0, 497) + "..." : joined;
    }

    static Integer costInGp(Dnd5eEquipmentCost cost) {
        if (cost == null || cost.quantity() == null) {
            return 0;
        }
        String unit = cost.unit() != null ? cost.unit().toLowerCase(Locale.ROOT) : "gp";
        return switch (unit) {
            case "sp" -> Math.max(0, cost.quantity() / 10);
            case "cp" -> Math.max(0, cost.quantity() / 100);
            case "pp" -> cost.quantity() * 10;
            case "ep" -> Math.max(0, (cost.quantity() * 5) / 10);
            default -> cost.quantity();
        };
    }

    static Short weightToShort(Double weight) {
        if (weight == null) {
            return 0;
        }
        return (short) Math.round(weight);
    }

    static Optional<DamageType> parseDamageType(String index) {
        if (index == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(DamageType.valueOf(index.toLowerCase(Locale.ROOT)));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    static WeaponRange parseWeaponRange(String weaponRange) {
        if (weaponRange == null) {
            return WeaponRange.melee;
        }
        return weaponRange.toLowerCase(Locale.ROOT).contains("ranged")
                ? WeaponRange.ranged
                : WeaponRange.melee;
    }
}
