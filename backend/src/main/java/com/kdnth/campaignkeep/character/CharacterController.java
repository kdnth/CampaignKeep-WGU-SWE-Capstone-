package com.kdnth.campaignkeep.character;

import com.kdnth.campaignkeep.campaign.CampaignResponse;
import com.kdnth.campaignkeep.spell.AddSpellToCharacterRequest;
import com.kdnth.campaignkeep.spell.SpellDetailResponse;
import com.kdnth.campaignkeep.spell.SpellService;
import com.kdnth.campaignkeep.item.*;
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
    private final SpellService spellService;
    private final InventoryService inventoryService;
    private final PlayableCharacterRepository playableCharacterRepository;
    private final NonplayableCharacterRepository nonplayableCharacterRepository;


    public CharacterController(CharacterService characterService,
                               SpellService spellService,
                               InventoryService inventoryService,
                               PlayableCharacterRepository playableCharacterRepository,
                               NonplayableCharacterRepository nonplayableCharacterRepository) {
        this.characterService = characterService;
        this.spellService = spellService;
        this.inventoryService = inventoryService;
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

    @GetMapping("/{id}/spells")
    public ResponseEntity<List<SpellDetailResponse>> getCharacterSpells(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long callerId = getUserId(authentication);
        return ResponseEntity.ok(spellService.getCharacterSpells(id, callerId));
    }

    @PostMapping("/{id}/spells")
    public ResponseEntity<SpellDetailResponse> addSpellToCharacter(
            @PathVariable Long id,
            @Valid @RequestBody AddSpellToCharacterRequest request,
            Authentication authentication
    ) {
        Long callerId = getUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(spellService.addSpellToCharacter(id, request.spellId(), callerId));
    }

    @DeleteMapping("/{id}/spells/{spellId}")
    public ResponseEntity<Void> removeSpellFromCharacter(
            @PathVariable Long id,
            @PathVariable Long spellId,
            Authentication authentication
    ) {
        Long callerId = getUserId(authentication);
        spellService.removeSpellFromCharacter(id, spellId, callerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/inventory")
    public ResponseEntity<CharacterInventoryResponse> getCharacterInventory(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long callerId = getUserId(authentication);
        return ResponseEntity.ok(inventoryService.getInventory(id, callerId));
    }

    @PostMapping("/{id}/starting-equipment")
    public ResponseEntity<CharacterInventoryResponse> submitStartingEquipment(
            @PathVariable Long id,
            @Valid @RequestBody SubmitStartingEquipmentRequest request,
            Authentication authentication
    ) {
        Long callerId = getUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.submitStartingEquipment(id, request, callerId));
    }

    @PostMapping("/{id}/starting-pack")
    public ResponseEntity<CharacterInventoryResponse> submitStartingPack(
            @PathVariable Long id,
            @Valid @RequestBody SubmitStartingPackRequest request,
            Authentication authentication
    ) {
        Long callerId = getUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.submitStartingPack(id, request, callerId));
    }

    @PatchMapping("/{id}/inventory/{inventoryItemId}")
    public ResponseEntity<CharacterInventoryResponse> updateInventoryItem(
            @PathVariable Long id,
            @PathVariable Long inventoryItemId,
            @Valid @RequestBody UpdateInventoryItemRequest request,
            Authentication authentication
    ) {
        Long callerId = getUserId(authentication);
        return ResponseEntity.ok(inventoryService.updateInventoryItem(id, inventoryItemId, request, callerId));
    }

    @DeleteMapping("/{id}/inventory/{inventoryItemId}")
    public ResponseEntity<Void> removeInventoryItem(
            @PathVariable Long id,
            @PathVariable Long inventoryItemId,
            Authentication authentication
    ) {
        Long callerId = getUserId(authentication);
        inventoryService.removeInventoryItem(id, inventoryItemId, callerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/gold")
    public ResponseEntity<CharacterInventoryResponse> updateGold(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGoldRequest request,
            Authentication authentication
    ) {
        Long callerId = getUserId(authentication);
        return ResponseEntity.ok(inventoryService.updateGold(id, request, callerId));
    }

    @GetMapping("/{id}/attacks")
    public ResponseEntity<List<CharacterAttackResponse>> getCharacterAttacks(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long callerId = getUserId(authentication);
        return ResponseEntity.ok(inventoryService.getAttacks(id, callerId));
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
