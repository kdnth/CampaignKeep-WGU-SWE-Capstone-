package com.kdnth.campaignkeep.race;

public record RaceSummaryResponse(
        Long id,
        String name,
        String size,
        Short speed,
        String index
) {
}
