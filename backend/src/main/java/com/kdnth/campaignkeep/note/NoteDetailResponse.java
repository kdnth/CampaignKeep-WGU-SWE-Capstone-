package com.kdnth.campaignkeep.note;

import java.time.LocalDateTime;

public record NoteDetailResponse(
        Long id,
        String title,
        String body,
        LocalDateTime createdOn,
        LocalDateTime updatedOn
) {
}
