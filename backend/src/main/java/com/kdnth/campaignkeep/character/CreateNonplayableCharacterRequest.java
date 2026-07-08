package com.kdnth.campaignkeep.character;

import jakarta.validation.constraints.*;

import java.util.List;

public record CreateNonplayableCharacterRequest(
        @NotBlank @Size(max = 50) String name,
        @NotNull Long raceId,
        Long subraceId,
        Long backgroundId,
        List<Long> languageIds,

        @NotNull @Min(1) @Max(30) Short strength,
        @NotNull @Min(1) @Max(30) Short dexterity,
        @NotNull @Min(1) @Max(30) Short constitution,
        @NotNull @Min(1) @Max(30) Short intelligence,
        @NotNull @Min(1) @Max(30) Short wisdom,
        @NotNull @Min(1) @Max(30) Short charisma,

        @NotNull @Min(1) Short hitPoints,
        @NotNull @Min(0) Short armorClass
) {
}
