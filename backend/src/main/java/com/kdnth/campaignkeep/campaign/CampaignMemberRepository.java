package com.kdnth.campaignkeep.campaign;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CampaignMemberRepository extends JpaRepository<CampaignMember, CampaignMemberId> {
    List<CampaignMember> findByCampaign_Id(Long campaignId);
    List<CampaignMember> findByUser_Id(Long userId);
    Optional<CampaignMember> findByCampaign_IdAndUser_Id(Long campaignId, Long userId);
    long countByCampaign_IdAndRole(Long campaignId, CampaignRole role);
    boolean existsByCampaign_IdAndUser_Id(Long campaignId, Long userId);
}
