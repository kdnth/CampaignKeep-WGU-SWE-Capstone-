package com.kdnth.campaignkeep.character;

import com.kdnth.campaignkeep.campaign.CampaignResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.kdnth.campaignkeep.base.AuthUtil.getUserId;

@RestController
@RequestMapping("/api/characters")
public class CharacterController {
    private final CharacterService characterService;
    private final PlayableCharacterRepository playableCharacterRepository;
    private final NonplayableCharacterRepository nonplayableCharacterRepository;


    public CharacterController(CharacterService characterService,
                               PlayableCharacterRepository playableCharacterRepository,
                               NonplayableCharacterRepository nonplayableCharacterRepository) {
        this.characterService = characterService;
        this.playableCharacterRepository = playableCharacterRepository;
        this.nonplayableCharacterRepository = nonplayableCharacterRepository;
    }

    @PostMapping("/playable-characters")
    public ResponseEntity<CharacterResponse> createPlayableCharacter(
            @Valid @RequestBody CreatePlayableCharacterRequest request,
            Authentication authentication
    ) {
        Long callerId = getUserId(authentication);
        PlayableCharacter character = characterService.createPlayableCharacter(request, callerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(characterService.toResponse(character));
    }

    @PostMapping("/nonplayable-characters")
    public ResponseEntity<CharacterResponse> createNonplayableCharacter(
            @Valid @RequestBody CreateNonplayableCharacterRequest request,
            Authentication authentication
    ) {
        Long callerId = getUserId(authentication);
        NonplayableCharacter character = characterService.createNonplayableCharacter(request, callerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(characterService.toResponse(character));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CharacterResponse> getCharacter(
            @PathVariable Long id,
            Authentication authentication
    )  {
        Long callerId = getUserId(authentication);
        Character character = characterService.getCharacter(id, callerId);
        return ResponseEntity.ok(characterService.toResponse(character));
    }

    @GetMapping("/{id}/campaign-view")
    public ResponseEntity<CharacterResponse> getCharacterCampaignView(
            @PathVariable Long id,
            @RequestParam Long campaignId,
            Authentication authentication
    ) {
        Long callerId = getUserId(authentication);
        Character character = characterService.getCharacterForCampaignView(id, campaignId, callerId);
        return ResponseEntity.ok(characterService.toResponse(character));
    }

    @GetMapping("/{id}/campaigns")
    public ResponseEntity<List<CampaignResponse>> getCampaignsForCharacter(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long callerId = getUserId(authentication);
        return ResponseEntity.ok(characterService.getCampaignsForCharacter(id, callerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacter(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long callerId = getUserId(authentication);
        characterService.deleteCharacter(id, callerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CharacterResponse> updateCharacterStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCharacterStatusRequest request,
            Authentication authentication
    ) {
        Long callerId = getUserId(authentication);
        Character character = characterService.updateCharacterStatus(id, request, callerId);
        return ResponseEntity.ok(characterService.toResponse(character));
    }

    @GetMapping("/mine/playable")
    public ResponseEntity<List<CharacterWithCampaignsResponse>> getMyPlayableCharacters(
            Authentication authentication
    ) {
        Long callerId = getUserId(authentication);
        List<PlayableCharacter> characters = playableCharacterRepository.findByPlayerId(callerId);

        List<CharacterWithCampaignsResponse> result = characters.stream()
                .map(c -> new CharacterWithCampaignsResponse(
                        characterService.toResponse(c),
                        characterService.getCampaignNames(c.getId())
                ))
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/mine/nonplayable")
    public ResponseEntity<List<CharacterWithCampaignsResponse>> getMyNonplayableCharacters(
            Authentication authentication
    ) {
        Long callerId = getUserId(authentication);
        List<NonplayableCharacter> characters = nonplayableCharacterRepository.findByCreatedBy_Id(callerId);

        List<CharacterWithCampaignsResponse> result = characters.stream()
                .map(c -> new CharacterWithCampaignsResponse(
                        characterService.toResponse(c),
                        characterService.getCampaignNames(c.getId())
                ))
                .toList();

        return ResponseEntity.ok(result);
    }
}
