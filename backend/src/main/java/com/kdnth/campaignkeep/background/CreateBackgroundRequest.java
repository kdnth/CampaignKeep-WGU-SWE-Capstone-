package com.kdnth.campaignkeep.background;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBackgroundRequest(
        @NotBlank @Size(max = 50) String name,
        @Size(max = 1000) String description
) {
}
