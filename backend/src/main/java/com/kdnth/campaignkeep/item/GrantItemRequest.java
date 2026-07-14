package com.kdnth.campaignkeep.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GrantItemRequest(
        @NotNull Long itemId,
        @NotNull @Min(1) Short quantity
) {
}
