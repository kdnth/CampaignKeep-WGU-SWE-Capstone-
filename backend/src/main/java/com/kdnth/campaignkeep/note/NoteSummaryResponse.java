package com.kdnth.campaignkeep.note;

import java.time.LocalDateTime;

public record NoteSummaryResponse(
        Long id,
        String title,
        LocalDateTime updatedOn
) {
}
