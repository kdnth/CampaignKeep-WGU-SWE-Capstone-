package com.kdnth.campaignkeep.campaign;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {
    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping
    public ResponseEntity<CampaignResponse> createCampaign(
            @Valid @RequestBody CreateCampaignRequest request,
            Authentication authentication
    ){
        Long userId = getUserId(authentication);
        CampaignResponse response = campaignService.createCampaign(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<CampaignResponse> getCampaigns(Authentication authentication){
        Long userId = getUserId(authentication);
        return campaignService.getCampaignsForUser(userId);
    }

    @GetMapping("/{campaignId}")
    public CampaignResponse getCampaign(
            @PathVariable Long campaignId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        return campaignService.getCampaign(campaignId, userId);
    }

    @PutMapping("/{campaignId}")
    public CampaignResponse updateCampaign(
            @PathVariable Long campaignId,
            @Valid @RequestBody UpdateCampaignRequest request,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        return campaignService.updateCampaign(campaignId, userId, request);
    }

    @PostMapping("/{campaignId}/finish")
    public ResponseEntity<Void> finishCampaign(
            @PathVariable Long campaignId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        campaignService.finishCampaign(campaignId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{campaignId}")
    public ResponseEntity<Void> deleteCampaign(
            @PathVariable Long campaignId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        campaignService.deleteCampaign(campaignId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{campaignId}/members")
    public List<CampaignMemberResponse> getMembers(
            @PathVariable Long campaignId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        return campaignService.getMembers(campaignId, userId);
    }

    @PostMapping("/{campaignId}/members")
    public ResponseEntity<CampaignMemberResponse> addMember(
            @PathVariable Long campaignId,
            @Valid @RequestBody AddMemberRequest request,
            Authentication authentication
    ) {
        Long actingUserId = getUserId(authentication);
        CampaignMemberResponse response = campaignService.addMember(campaignId, actingUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{campaignId}/members/{targetUserId}/role")
    public CampaignMemberResponse changeRole(
            @PathVariable Long campaignId,
            @PathVariable Long targetUserId,
            @Valid @RequestBody ChangeRoleRequest request,
            Authentication authentication
    ) {
        Long actingUserId = getUserId(authentication);
        return campaignService.changeRole(campaignId, actingUserId, targetUserId, request);
    }

    @DeleteMapping("/{campaignId}/members/{targetUserId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long campaignId,
            Authentication authentication,
            @PathVariable Long targetUserId
    ) {
        Long actingUserId = getUserId(authentication);
        campaignService.removeMember(campaignId, actingUserId, targetUserId);
        return ResponseEntity.noContent().build();
    }

    private Long getUserId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}
