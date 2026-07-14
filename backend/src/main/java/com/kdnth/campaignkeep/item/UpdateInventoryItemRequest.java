package com.kdnth.campaignkeep.item;

import jakarta.validation.constraints.Min;

public record UpdateInventoryItemRequest(
        EquipmentSlot equipToSlot,
        Boolean unequip,
        @Min(1) Short quantity
) {
}
