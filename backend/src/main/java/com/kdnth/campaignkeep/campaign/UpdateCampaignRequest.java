package com.kdnth.campaignkeep.campaign;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCampaignRequest(
        @NotBlank @Size(max = 75) String title,
        @Size(max=3000) String description
) {
}
