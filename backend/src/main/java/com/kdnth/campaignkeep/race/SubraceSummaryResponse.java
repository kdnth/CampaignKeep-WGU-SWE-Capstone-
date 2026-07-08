package com.kdnth.campaignkeep.race;

public record SubraceSummaryResponse(
        Long id,
        Long raceId,
        String raceName,
        String name
) {
}
