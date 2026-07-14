package com.kdnth.campaignkeep.item;

import java.util.List;

public record EquipmentPackResponse(
        String index,
        String name,
        Integer costGp,
        List<PackContentResponse> contents
) {
}
