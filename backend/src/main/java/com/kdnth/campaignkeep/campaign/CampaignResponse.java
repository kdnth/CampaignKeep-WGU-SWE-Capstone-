package com.kdnth.campaignkeep.campaign;

import java.time.LocalDateTime;

public record CampaignResponse(
        Long id,
        String title,
        String description,
        LocalDateTime createdOn,
        LocalDateTime updatedOn,
        LocalDateTime finishedOn,
        CampaignRole callerRole
) {
}
