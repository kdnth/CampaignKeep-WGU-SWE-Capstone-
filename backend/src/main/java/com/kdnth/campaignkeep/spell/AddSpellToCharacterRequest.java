package com.kdnth.campaignkeep.spell;

import jakarta.validation.constraints.NotNull;

public record AddSpellToCharacterRequest(
        @NotNull Long spellId
) {
}
