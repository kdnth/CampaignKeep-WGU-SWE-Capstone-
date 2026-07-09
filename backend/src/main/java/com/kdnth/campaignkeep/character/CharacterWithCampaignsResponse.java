package com.kdnth.campaignkeep.character;

import java.util.List;

public record CharacterWithCampaignsResponse(
        CharacterResponse character,
        List<String> campaignNames
) {
}
