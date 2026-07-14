package com.kdnth.campaignkeep.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateGoldRequest(
        @NotNull @Min(0) Integer gold
) {
}
