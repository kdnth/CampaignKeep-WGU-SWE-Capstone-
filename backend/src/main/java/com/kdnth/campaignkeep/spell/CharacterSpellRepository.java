package com.kdnth.campaignkeep.spell;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharacterSpellRepository extends JpaRepository<CharacterSpell, CharacterSpell.CharacterSpellId> {

    List<CharacterSpell> findByCharacterId(Long characterId);

    boolean existsByCharacter_IdAndSpell_Id(Long characterId, Long spellId);

    void deleteByCharacter_IdAndSpell_Id(Long characterId, Long spellId);
}
