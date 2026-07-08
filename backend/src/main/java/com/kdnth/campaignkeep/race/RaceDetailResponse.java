package com.kdnth.campaignkeep.race;

import java.util.List;

public record RaceDetailResponse(
        Long id,
        String name,
        String ageDesc,
        String alignmentDesc,
        String languageDesc,
        String size,
        String sizeDesc,
        Short speed,
        String index,
        List<RaceAbilityPointBonusResponse> abilityPointBonuses
) {
}
