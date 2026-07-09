package com.kdnth.campaignkeep.sessionlog;

import java.time.LocalDateTime;

public record SessionLogSummaryResponse(
        Long id,
        String title,
        LocalDateTime createdOn
) {
}
