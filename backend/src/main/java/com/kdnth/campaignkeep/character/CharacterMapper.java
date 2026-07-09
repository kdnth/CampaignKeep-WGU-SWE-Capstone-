package com.kdnth.campaignkeep.character;

import org.hibernate.Hibernate;

import java.util.List;
import java.util.stream.Collectors;

public final class CharacterMapper {
    private CharacterMapper() {}

    public static CharacterResponse toResponse(
            Character character,
            List<CharacterClass> classes,
            List<CharacterLanguage> languages
    ) {
        Character resolved = (Character) Hibernate.unproxy(character);
        Long ownerId = resolved instanceof PlayableCharacter pc
                ? pc.getPlayer().getId()
                : ((NonplayableCharacter) resolved).getCreatedBy().getId();

        String characterType = resolved instanceof PlayableCharacter ? "PC" : "NPC";

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
                resolved.getStatus(),
                resolved.getStrength(),
                resolved.getDexterity(),
                resolved.getConstitution(),
                resolved.getIntelligence(),
                resolved.getWisdom(),
                resolved.getCharisma(),
                resolved.getHitPoints(),
                resolved.getArmorClass(),
                resolved.getInitiativeBonus(),
                resolved.getSpeed(),
                classes.stream().map(cc -> cc.getDndClass().getName()).collect(Collectors.toList()),
                languages.stream().map(cl -> cl.getLanguage().getName()).collect(Collectors.toList()),
                character.getCreatedOn(),
                character.getUpdatedOn()


        );
    }
}
