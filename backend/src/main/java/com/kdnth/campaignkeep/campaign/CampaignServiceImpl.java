package com.kdnth.campaignkeep.campaign;

import com.kdnth.campaignkeep.base.AccessDeniedException;
import com.kdnth.campaignkeep.base.ConflictException;
import com.kdnth.campaignkeep.user.User;
import com.kdnth.campaignkeep.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;
    private final CampaignMemberRepository campaignMemberRepository;
    private final UserRepository userRepository;

    public CampaignServiceImpl(
            CampaignRepository campaignRepository,
            CampaignMemberRepository campaignMemberRepository,
            UserRepository userRepository) {
        this.campaignRepository = campaignRepository;
        this.campaignMemberRepository = campaignMemberRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CampaignResponse createCampaign(Long userId, CreateCampaignRequest request) {
        Campaign campaign = new Campaign();
        campaign.setTitle(request.title());
        campaign.setDescription(request.description());
        campaign = campaignRepository.save(campaign);

        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IllegalStateException("User not found"));
        CampaignMember membership = new CampaignMember();
        membership.setCampaign(campaign);
        membership.setUser(user);
        membership.setRole(CampaignRole.master);
        campaignMemberRepository.save(membership);

        return toCampaignResponse(campaign, CampaignRole.master);
    }

    @Override
    public List<CampaignResponse> getCampaignsForUser(Long userId) {
        return campaignMemberRepository.findByUser_Id(userId).stream()
                .map(member -> toCampaignResponse(member.getCampaign(), member.getRole()))
                .toList();
    }

    @Override
    public CampaignResponse getCampaign(Long campaignId, Long userId) {
        CampaignMember membership = requireMembership(campaignId, userId);
        return toCampaignResponse(membership.getCampaign(), membership.getRole());
    }

    @Override
    public CampaignResponse updateCampaign(Long campaignId, Long userId, UpdateCampaignRequest request) {
        Campaign campaign = requireMaster(campaignId, userId).getCampaign();
        campaign.setTitle(request.title());
        campaign.setDescription(request.description());
        campaign = campaignRepository.save(campaign);

        return toCampaignResponse(campaign, CampaignRole.master);
    }

    @Override
    public void finishCampaign(Long campaignId, Long userId) {
        Campaign campaign = requireMaster(campaignId, userId).getCampaign();
        campaign.setFinishedOn(java.time.LocalDateTime.now());
        campaignRepository.save(campaign);
    }

    @Override
    public void deleteCampaign(Long campaignId, Long userId) {
        requireMaster(campaignId, userId);
        campaignMemberRepository.deleteAll(campaignMemberRepository.findByCampaign_Id(campaignId));
        campaignRepository.deleteById(campaignId);
    }

    @Override
    public List<CampaignMemberResponse> getMembers(Long campaignId, Long userId) {
        requireMembership(campaignId, userId);
        return campaignMemberRepository.findByCampaign_Id(campaignId).stream()
                .map(this::toMemberResponse)
                .toList();
    }

    @Override
    public CampaignMemberResponse addMember(Long campaignId, Long actingUserId, AddMemberRequest request) {
        Campaign campaign = requireMaster(campaignId, actingUserId).getCampaign();
        CampaignMember newMember = new CampaignMember();
        User targetUser = findUserByUsernameOrEmail(request.identifier());

        if (campaignMemberRepository.existsByCampaign_IdAndUser_Id(campaignId, targetUser.getId())) {
            throw new ConflictException("User is already a member of this campaign");
        }
        newMember.setCampaign(campaign);
        newMember.setUser(targetUser);
        newMember.setRole(request.role());
        campaignMemberRepository.save(newMember);

        return toMemberResponse(newMember);
    }

    @Override
    public void removeMember(Long campaignId, Long actingUserId, Long targetUserId) {
        if (!targetUserId.equals(actingUserId)) {
            requireMaster(campaignId, actingUserId);
        }
        CampaignMember targetMember = requireMembership(campaignId, targetUserId);

        guardAgainstLastMasterLoss(targetMember);

        campaignMemberRepository.delete(targetMember);
    }

    @Override
    public CampaignMemberResponse changeRole(Long campaignId, Long actingUserId, Long targetUserId, ChangeRoleRequest request) {
        requireMaster(campaignId, actingUserId);

        CampaignMember targetMember = campaignMemberRepository
                .findByCampaign_IdAndUser_Id(campaignId, targetUserId)
                .orElseThrow(() -> new NoSuchElementException("User is not member of this campaign. Please add them and try again.")
                );

        guardAgainstLastMasterLoss(targetMember);

        targetMember.setRole(request.role());
        campaignMemberRepository.save(targetMember);

        return toMemberResponse(targetMember);
    }

    private CampaignMember requireMembership(Long campaignId, Long userId) {
        campaignExists(campaignId);

        return campaignMemberRepository.findByCampaign_IdAndUser_Id(campaignId, userId)
                .orElseThrow(() -> new AccessDeniedException("Not a member of this campaign"));
    }

    private CampaignMember requireMaster(Long campaignId, Long userId) {
        CampaignMember membership = requireMembership(campaignId, userId);
        if (!membership.getRole().equals(CampaignRole.master)) {
            throw new AccessDeniedException("Can only be performed by game master");
        }
        return membership;
    }

    private CampaignResponse toCampaignResponse(Campaign campaign, CampaignRole callerRole) {
        return new CampaignResponse(
                campaign.getId(),
                campaign.getTitle(),
                campaign.getDescription(),
                campaign.getCreatedOn(),
                campaign.getUpdatedOn(),
                campaign.getFinishedOn(),
                callerRole
        );
    }

    private CampaignMemberResponse toMemberResponse(CampaignMember member) {
        return new CampaignMemberResponse(
                member.getUser().getId(),
                member.getUser().getUsername(),
                member.getRole());
    }

    private void campaignExists(Long campaignId) {
        if (!campaignRepository.existsById(campaignId)) {
            throw new NoSuchElementException("Campaign not found");
        }
    }

    private User findUserByUsernameOrEmail(String identifier) {
        return userRepository.findByUsernameOrEmail(identifier).orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    private void guardAgainstLastMasterLoss(CampaignMember targetMember) {
        if (targetMember.getRole().equals(CampaignRole.master)
            && campaignMemberRepository.countByCampaign_IdAndRole(targetMember.getCampaign().getId(), CampaignRole.master) == 1) {
            throw new ConflictException("Campaign must have at least one game master.");
        }
    }
}
