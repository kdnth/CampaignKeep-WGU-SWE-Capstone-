package com.kdnth.campaignkeep.campaign;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignPlayableCharacterRepository extends JpaRepository<CampaignPlayableCharacter, CampaignPlayableCharacter.CampaignPlayableCharacterId> {
    List<CampaignPlayableCharacter> findByCampaignId(Long campaignId);
    List<CampaignPlayableCharacter> findByCharacterId(Long characterId);
    boolean existsByCharacterIdAndCampaignId(Long characterId, Long campaignId);
    boolean existsByCharacterId(Long characterId);
}