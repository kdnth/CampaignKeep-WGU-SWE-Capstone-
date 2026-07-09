package com.kdnth.campaignkeep.spell;

public record SpellDetailResponse(
        Long id,
        String apiIndex,
        String name,
        Short level,
        MagicSchool school,
        String castingTime,
        String duration,
        SpellRangeType rangeType,
        Short rangeFeet,
        String rangeDisplay,
        boolean concentration,
        boolean ritual,
        boolean hasVerbal,
        boolean hasSomatic,
        boolean hasMaterial,
        String materialDesc,
        String description,
        String higherLevel
) {
}
