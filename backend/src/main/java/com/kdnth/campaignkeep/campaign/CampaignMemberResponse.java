package com.kdnth.campaignkeep.campaign;

public record CampaignMemberResponse(
        Long userId,
        String username,
        CampaignRole role
) {
}
