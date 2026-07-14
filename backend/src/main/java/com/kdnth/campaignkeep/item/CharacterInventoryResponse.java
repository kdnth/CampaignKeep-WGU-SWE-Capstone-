package com.kdnth.campaignkeep.item;

import java.util.List;

public record CharacterInventoryResponse(
        List<InventoryItemResponse> items,
        int totalWeight,
        int carryingCapacity,
        int gold,
        boolean startingEquipmentChosen,
        short armorClass
) {
}
