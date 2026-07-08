package com.kdnth.campaignkeep.character;

import java.util.List;
import java.util.stream.Collectors;

public final class CharacterMapper {
    private CharacterMapper() {}

    public static CharacterResponse toResponse(
            Character character,
            List<CharacterClass> classes,
            List<CharacterLanguage> languages
    ) {
        Long ownerId = character instanceof PlayableCharacter pc ? pc.getPlayer().getId() : ((NonplayableCharacter) character).getCreatedBy().getId();

        String characterType = character instanceof PlayableCharacter ? "PC" : "NPC";

        return new CharacterResponse(
                character.getId(),
                characterType,
                ownerId,
                character.getName(),
                character.getRace().getId(),
                character.getRace().getName(),
                character.getSubrace() != null ? character.getSubrace().getId() : null,
                character.getSubrace() != null ? character.getSubrace().getName() : null,
                character.getBackground() != null ? character.getBackground().getId() : null,
                character.getBackground() != null ? character.getBackground().getName() : null,
                character.getStatus(),
                character.getStrength(),
                character.getDexterity(),
                character.getConstitution(),
                character.getIntelligence(),
                character.getWisdom(),
                character.getCharisma(),
                character.getHitPoints(),
                character.getArmorClass(),
                character.getInitiativeBonus(),
                character.getSpeed(),
                classes.stream().map(cc -> cc.getDndClass().getName()).collect(Collectors.toList()),
                languages.stream().map(cl -> cl.getLanguage().getName()).collect(Collectors.toList()),
                character.getCreatedOn(),
                character.getUpdatedOn()


        );
    }
}
