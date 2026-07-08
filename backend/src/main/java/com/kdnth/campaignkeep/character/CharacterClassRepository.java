package com.kdnth.campaignkeep.character;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharacterClassRepository extends JpaRepository<CharacterClass, CharacterClass.CharacterClassId> {
    List<CharacterClass> findByCharacterId(Long characterId);
}