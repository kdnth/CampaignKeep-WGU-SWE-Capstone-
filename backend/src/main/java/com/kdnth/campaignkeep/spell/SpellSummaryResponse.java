package com.kdnth.campaignkeep.spell;

public record SpellSummaryResponse(
        Long id,
        String apiIndex,
        String name,
        Short level,
        MagicSchool school,
        String castingTime,
        boolean concentration,
        boolean ritual,
        boolean hasVerbal,
        boolean hasSomatic,
        boolean hasMaterial
) {
}
