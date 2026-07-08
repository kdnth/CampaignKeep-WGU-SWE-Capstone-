package com.kdnth.campaignkeep.character;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;


public record CreatePlayableCharacterRequest(
        @NotBlank @Size(max=50) String name,
        @NotNull Long raceId,
        Long subraceId,
        Long backgroundId,
        @NotNull Long classId,
        List<Long> languagesIds,

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
