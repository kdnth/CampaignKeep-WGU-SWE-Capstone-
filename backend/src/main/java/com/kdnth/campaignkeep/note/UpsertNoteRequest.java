package com.kdnth.campaignkeep.note;

import jakarta.validation.constraints.Size;

public record UpsertNoteRequest(
        @Size(max = 100) String title,
        @Size(max = 10000) String body
) {
}
