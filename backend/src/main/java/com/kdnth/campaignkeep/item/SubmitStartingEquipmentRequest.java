package com.kdnth.campaignkeep.item;
import jakarta.validation.constraints.NotNull;


public record SubmitStartingEquipmentRequest(
        @NotNull Long weaponItemId,
        Long armorItemId,
        Long shieldItemId
) {
}
