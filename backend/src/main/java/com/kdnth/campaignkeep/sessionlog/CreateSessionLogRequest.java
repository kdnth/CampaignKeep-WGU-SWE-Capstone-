package com.kdnth.campaignkeep.sessionlog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSessionLogRequest(
        @NotBlank @Size(max = 100) String title,
        @Size(max = 10000) String body
) {
}
