package com.kdnth.campaignkeep.character;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NonplayableCharacterRepository extends JpaRepository<NonplayableCharacter, Long> {
    List<NonplayableCharacter> findByCreatedBy_Id(Long createdByUserId);
}
