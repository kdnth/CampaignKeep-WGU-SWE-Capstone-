package com.kdnth.campaignkeep.character;

import jakarta.validation.constraints.NotNull;

public record UpdateCharacterStatusRequest(
        @NotNull CharacterStatus status
) {
}
