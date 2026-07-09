package com.kdnth.campaignkeep.note;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CharacterNoteRepository extends JpaRepository<CharacterNote, Long> {
    Optional<CharacterNote> findByCharacter_Id(Long characterId);
    boolean existsByCharacter_Id(Long characterId);
}
