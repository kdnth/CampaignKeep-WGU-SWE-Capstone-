package com.kdnth.campaignkeep.character;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharacterLanguageRepository extends JpaRepository<CharacterLanguage, CharacterLanguage.CharacterLanguageId> {
    List<CharacterLanguage> findByCharacterId(Long characterId);
}