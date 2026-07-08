package com.kdnth.campaignkeep.campaign;

import com.kdnth.campaignkeep.character.Character;
import com.kdnth.campaignkeep.character.NonplayableCharacter;
import com.kdnth.campaignkeep.character.PlayableCharacter;

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

    CampaignMember requireMaster(Long campaignId, Long userId);
    CampaignMember requireMembership(Long campaignId, Long userId);
    void detachCharacterFromAllCampaigns(Long characterId);

    Character addPlayableCharacter(Long campaignId, Long characterId, Long callerId);
    void removePlayableCharacter(Long campaignId, Long characterId, Long callerId);
    Character addNonplayableCharacter(Long campaignId, Long characterId, Long callerId);
    void removeNonplayableCharacter(Long campaignId, Long characterId, Long callerId);

    List<PlayableCharacter> getPlayableCharacters(Long campaignId, Long callerId);
    List<NonplayableCharacter> getNonplayableCharacters(Long campaignId, Long callerId);
}
