package com.kdnth.campaignkeep.campaign;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddMemberRequest(
        @NotBlank String identifier,
        @NotNull CampaignRole role
) {
}
