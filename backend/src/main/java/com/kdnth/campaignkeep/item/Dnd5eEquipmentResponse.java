package com.kdnth.campaignkeep.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Dnd5eEquipmentResponse(
        String index,
        String name,
        List<String> desc,
        Dnd5eEquipmentNamedResource equipment_category,
        String weapon_category,
        String weapon_range,
        Dnd5eEquipmentCost cost,
        Dnd5eEquipmentDamage damage,
        Dnd5eEquipmentRange range,
        Double weight,
        String armor_category,
        Dnd5eEquipmentArmorClass armor_class,
        Integer str_minimum,
        Dnd5eEquipmentNamedResource gear_category,
        String tool_category,
        List<Dnd5eEquipmentContent> contents
) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record Dnd5eEquipmentNamedResource(
        String index,
        String name
) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record Dnd5eEquipmentCost(
        Integer quantity,
        String unit
) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record Dnd5eEquipmentDamage(
        String damage_dice,
        Dnd5eEquipmentNamedResource damage_type
) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record Dnd5eEquipmentRange(
        Integer normal,
        Integer long_range
) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record Dnd5eEquipmentArmorClass(
        Integer base,
        Boolean dex_bonus,
        Integer max_bonus
) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record Dnd5eEquipmentContent(
        Dnd5eEquipmentNamedResource item,
        Integer quantity
) {
}
