package com.kdnth.campaignkeep.race;

import java.util.List;

public record SubraceDetailResponse(
        Long id,
        Long raceId,
        String raceName,
        String name,
        String description,
        List<SubraceAbilityPointBonusResponse> abilityPointBonuses
) {
}
