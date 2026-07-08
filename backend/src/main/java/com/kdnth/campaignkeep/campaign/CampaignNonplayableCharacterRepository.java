package com.kdnth.campaignkeep.campaign;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignNonplayableCharacterRepository extends JpaRepository<CampaignNonplayableCharacter, CampaignNonplayableCharacter.CampaignNonplayableCharacterId> {
    List<CampaignNonplayableCharacter> findByCampaignId(Long campaignId);
    List<CampaignNonplayableCharacter> findByCharacterId(Long characterId);
    boolean existsByCharacterIdAndCampaignId(Long characterId, Long campaignId);
}