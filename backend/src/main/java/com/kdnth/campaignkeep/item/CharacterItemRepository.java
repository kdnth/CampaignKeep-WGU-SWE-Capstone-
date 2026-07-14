package com.kdnth.campaignkeep.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CharacterItemRepository extends JpaRepository<CharacterItem, Long> {

    List<CharacterItem> findByCharacter_Id(Long characterId);

    Optional<CharacterItem> findByCharacter_IdAndItem_Id(Long characterId, Long itemId);

    Optional<CharacterItem> findByCharacter_IdAndEquippedSlot(Long characterId, EquipmentSlot slot);

    void deleteByCharacter_Id(Long characterId);
}
