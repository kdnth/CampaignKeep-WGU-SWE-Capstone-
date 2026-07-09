package com.kdnth.campaignkeep.campaign;

import com.kdnth.campaignkeep.base.AccessDeniedException;
import com.kdnth.campaignkeep.base.ConflictException;
import com.kdnth.campaignkeep.character.CharacterRepository;
import com.kdnth.campaignkeep.character.NonplayableCharacter;
import com.kdnth.campaignkeep.character.NonplayableCharacterRepository;
import com.kdnth.campaignkeep.character.PlayableCharacter;
import com.kdnth.campaignkeep.character.PlayableCharacterRepository;
import com.kdnth.campaignkeep.user.User;
import com.kdnth.campaignkeep.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.kdnth.campaignkeep.character.Character;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;
    private final CampaignMemberRepository campaignMemberRepository;
    private final UserRepository userRepository;
    private final CharacterRepository characterRepository;
    private final CampaignPlayableCharacterRepository
            campaignPlayableCharacterRepository;
    private final CampaignNonplayableCharacterRepository
            campaignNonplayableCharacterRepository;
    private final PlayableCharacterRepository playableCharacterRepository;
    private final NonplayableCharacterRepository nonplayableCharacterRepository;


    public CampaignServiceImpl(
            CampaignRepository campaignRepository,
            CampaignMemberRepository campaignMemberRepository,
            UserRepository userRepository, CharacterRepository characterRepository,
            CampaignPlayableCharacterRepository campaignPlayableCharacterRepository,
            CampaignNonplayableCharacterRepository campaignNonplayableCharacterRepository,
            PlayableCharacterRepository playableCharacterRepository,
            NonplayableCharacterRepository nonplayableCharacterRepository) {
        this.campaignRepository = campaignRepository;
        this.campaignMemberRepository = campaignMemberRepository;
        this.userRepository = userRepository;
        this.characterRepository = characterRepository;
        this.campaignPlayableCharacterRepository = campaignPlayableCharacterRepository;
        this.campaignNonplayableCharacterRepository = campaignNonplayableCharacterRepository;
        this.playableCharacterRepository = playableCharacterRepository;
        this.nonplayableCharacterRepository = nonplayableCharacterRepository;
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

    @Override
    public CampaignMember requireMembership(Long campaignId, Long userId) {
        campaignExists(campaignId);

        return campaignMemberRepository.findByCampaign_IdAndUser_Id(campaignId, userId)
                .orElseThrow(() -> new AccessDeniedException("Not a member of this campaign"));
    }

    @Override
    public CampaignMember requireMaster(Long campaignId, Long userId) {
        CampaignMember membership = requireMembership(campaignId, userId);
        if (membership.getRole() != CampaignRole.master) {
            throw new AccessDeniedException("Can only be performed by game master");
        }
        return membership;
    }

    @Override
    @Transactional
    public Character addPlayableCharacter(Long campaignId, Long characterId, Long callerId) {
        Campaign campaign = requireMembership(campaignId, callerId).getCampaign();

        Character character = characterRepository.findById(characterId)
                                                                                  .orElseThrow(() -> new NoSuchElementException("Character not found."));
        if (!character.canBeEditedBy(callerId)) {
            throw new AccessDeniedException("You do not own this character.");
        }

        if (campaignPlayableCharacterRepository.existsByCharacterIdAndCampaignId(characterId, campaignId)) {
            throw new ConflictException("Character is already attached to this campaign.");
        }

        CampaignPlayableCharacter link = new CampaignPlayableCharacter();
        link.setCampaign(campaign);
        link.setCharacter(character);
        campaignPlayableCharacterRepository.save(link);
        return character;
    }

    @Override
    @Transactional
    public void removePlayableCharacter(Long campaignId, Long characterId, Long callerId) {
        Character character = characterRepository.findById(characterId)
                                                 .orElseThrow(() -> new NoSuchElementException("Character not found."));

        boolean isOwner = character.canBeEditedBy(callerId);
        boolean isMaster = isCallerMaster(campaignId, callerId);
        if (!isOwner && !isMaster) {
            throw new AccessDeniedException("You do not have permission to remove this character.");
        }

        CampaignPlayableCharacter.CampaignPlayableCharacterId id =
                new CampaignPlayableCharacter.CampaignPlayableCharacterId(campaignId, characterId);
        campaignPlayableCharacterRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Character addNonplayableCharacter(Long campaignId, Long characterId, Long callerId) {
        Campaign campaign = findCampaign(campaignId);
        requireMaster(campaignId, callerId);

        Character character = characterRepository.findById(characterId)
                                                 .orElseThrow(() -> new NoSuchElementException("Character not found."));
        if (!(character instanceof NonplayableCharacter) || !character.canBeEditedBy(callerId)) {
            throw new AccessDeniedException("You do not own this NPC.");
        }

        if (campaignNonplayableCharacterRepository.existsByCharacterIdAndCampaignId(characterId, campaignId)) {
            throw new ConflictException("NPC is already attached to this campaign.");
        }

        CampaignNonplayableCharacter link = new CampaignNonplayableCharacter();
        link.setCampaign(campaign);
        link.setCharacter(character);
        campaignNonplayableCharacterRepository.save(link);
        return character;
    }

    @Override
    @Transactional
    public void removeNonplayableCharacter(Long campaignId, Long characterId, Long callerId) {
        requireMaster(campaignId, callerId);

        CampaignNonplayableCharacter.CampaignNonplayableCharacterId id =
                new CampaignNonplayableCharacter.CampaignNonplayableCharacterId(campaignId, characterId);
        campaignNonplayableCharacterRepository.deleteById(id);
    }

    @Override
    public List<PlayableCharacter> getPlayableCharacters(Long campaignId, Long callerId) {
        requireMembership(campaignId, callerId);
        List<Long> characterIds = campaignPlayableCharacterRepository.findByCampaignId(campaignId).stream()
                .map(link -> link.getCharacter().getId())
                .toList();
        if (characterIds.isEmpty()) {
            return List.of();
        }
        return playableCharacterRepository.findAllById(characterIds);
    }

    @Override
    public List<NonplayableCharacter> getNonplayableCharacters(Long campaignId, Long callerId) {
        requireMaster(campaignId, callerId);
        List<Long> characterIds = campaignNonplayableCharacterRepository.findByCampaignId(campaignId).stream()
                .map(link -> link.getCharacter().getId())
                .toList();
        if (characterIds.isEmpty()) {
            return List.of();
        }
        return nonplayableCharacterRepository.findAllById(characterIds);
    }

    @Override
    public void detachCharacterFromAllCampaigns(Long characterId) {
        campaignPlayableCharacterRepository.deleteAll(campaignPlayableCharacterRepository.findByCharacterId(characterId));
        campaignNonplayableCharacterRepository.deleteAll(campaignNonplayableCharacterRepository.findByCharacterId(characterId));
    }

    private static CampaignResponse toCampaignResponse(Campaign campaign, CampaignRole callerRole) {
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
        if (targetMember.getRole() == CampaignRole.master
            && campaignMemberRepository.countByCampaign_IdAndRole(targetMember.getCampaign().getId(), CampaignRole.master) == 1) {
            throw new ConflictException("Campaign must have at least one game master.");
        }
    }

    private boolean isCallerMaster(Long campaignId, Long callerId) {
        return campaignMemberRepository.findByCampaign_IdAndUser_Id(campaignId, callerId)
                                       .map(m -> m.getRole() == CampaignRole.master)
                                       .orElse(false);
    }

    private Campaign findCampaign(Long campaignId) {
        return campaignRepository.findById(campaignId)
                                  .orElseThrow(() -> new NoSuchElementException("Campaign not found"));
    }

}
