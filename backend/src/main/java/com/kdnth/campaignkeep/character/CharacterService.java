package com.kdnth.campaignkeep.character;

import java.util.List;

public interface CharacterService {

    PlayableCharacter createPlayableCharacter(CreatePlayableCharacterRequest request, Long callerId);
    NonplayableCharacter createNonplayableCharacter(CreateNonplayableCharacterRequest request, Long callerId);
    Character getCharacter(Long characterId, Long callerId);
    void deleteCharacter(Long characterId, Long callerId);
    Character getCharacterForCampaignView(Long characterId, Long campaignId, Long callerId);
    CharacterResponse toResponse(Character character);
    List<CharacterResponse> toResponseList(List<? extends Character> characters);
}