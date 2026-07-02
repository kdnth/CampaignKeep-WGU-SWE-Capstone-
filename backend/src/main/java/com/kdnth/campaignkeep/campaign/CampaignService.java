package com.kdnth.campaignkeep.campaign;

import java.util.List;

public interface CampaignService {
    CampaignResponse createCampaign(Long userId, CreateCampaignRequest request);
    List<CampaignResponse> getCampaignsForUser(Long userId);
    CampaignResponse getCampaign(Long campaignId, Long userId);
    CampaignResponse updateCampaign(Long campaignId, Long userId, UpdateCampaignRequest request);
    void finishCampaign(Long campaignId, Long userId);
    void deleteCampaign(Long campaignId, Long userId);

    List<CampaignMemberResponse> getMembers(Long campaignId, Long userId);
    CampaignMemberResponse addMember(Long campaignId, Long actingUserId, AddMemberRequest request);
    void removeMember(Long campaignId, Long actingUserId, Long targetUserId);
    CampaignMemberResponse changeRole(Long campaignId, Long actingUserId, Long targetUserId, ChangeRoleRequest request);
}
