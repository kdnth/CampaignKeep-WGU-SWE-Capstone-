package com.kdnth.campaignkeep.item;

public record InventoryItemResponse(
        Long inventoryItemId,
        ItemDetailResponse item,
        Short quantity,
        EquipmentSlot equippedSlot
) {
}
