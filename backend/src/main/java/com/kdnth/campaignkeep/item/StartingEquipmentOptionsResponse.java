package com.kdnth.campaignkeep.item;

import java.util.List;
import java.util.Map;

public record StartingEquipmentOptionsResponse(
        Map<String, List<ItemDetailResponse>> optionsByGroup
) {
}
