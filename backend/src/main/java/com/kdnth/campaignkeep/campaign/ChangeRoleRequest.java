package com.kdnth.campaignkeep.campaign;

import jakarta.validation.constraints.NotNull;

public record ChangeRoleRequest(
        @NotNull CampaignRole role
) {
}
