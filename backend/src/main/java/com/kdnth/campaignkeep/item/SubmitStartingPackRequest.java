package com.kdnth.campaignkeep.item;

import jakarta.validation.constraints.NotBlank;

public record SubmitStartingPackRequest(
        @NotBlank String packIndex
) {
}
