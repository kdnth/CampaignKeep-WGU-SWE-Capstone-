package com.kdnth.campaignkeep.character;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayableCharacterRepository extends JpaRepository<PlayableCharacter, Long> {
    List<PlayableCharacter> findByPlayerId(Long playerId);
}
