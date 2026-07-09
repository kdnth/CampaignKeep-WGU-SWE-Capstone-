package com.kdnth.campaignkeep.sessionlog;

import java.time.LocalDateTime;

public record SessionLogDetailResponse(
        Long id,
        String title,
        String body,
        LocalDateTime createdOn
) {
}
