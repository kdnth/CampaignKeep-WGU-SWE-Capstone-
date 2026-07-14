package com.kdnth.campaignkeep.character;

import java.time.LocalDateTime;
import java.util.List;

public record CharacterResponse(
        Long id,
        String characterType,
        Long ownerId,
        String name,
        Long raceId,
        String raceName,
        Long subraceId,
        String subraceName,
        Long backgroundId,
        String backgroundName,
        CharacterStatus status,
        Short strength,
        Short dexterity,
        Short constitution,
        Short intelligence,
        Short wisdom,
        Short charisma,
        Short hitPoints,
        Short armorClass,
        Short initiativeBonus,
        Short speed,
        Integer gold,
        Boolean startingEquipmentChosen,
        List<String> classNames,
        List<String> languageNames,
        LocalDateTime createdOn,
        LocalDateTime updatedOn
) {
}
