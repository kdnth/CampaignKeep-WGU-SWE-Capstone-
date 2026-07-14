package com.kdnth.campaignkeep.item;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.kdnth.campaignkeep.base.AuthUtil.getUserId;

@RestController
@RequestMapping("/api")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/items/{id}")
    public ItemDetailResponse getItemById(@PathVariable Long id, Authentication authentication) {
        return itemService.getItemById(id, getUserId(authentication));
    }

    @GetMapping("/campaigns/{campaignId}/items")
    public List<ItemDetailResponse> listCampaignItems(
            @PathVariable Long campaignId,
            Authentication authentication
    ) {
        return itemService.listCampaignItems(campaignId, getUserId(authentication));
    }

    @PostMapping("/campaigns/{campaignId}/items")
    public ResponseEntity<ItemDetailResponse> createCampaignItem(
            @PathVariable Long campaignId,
            @Valid @RequestBody CreateCampaignItemRequest request,
            Authentication authentication
    ) {
        ItemDetailResponse response = itemService.createCampaignItem(
                campaignId, request, getUserId(authentication));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/classes/{classId}/starting-equipment-options")
    public StartingEquipmentOptionsResponse getStartingEquipmentOptions(@PathVariable Long classId) {
        return itemService.getStartingEquipmentOptions(classId);
    }

    @GetMapping("/equipment-packs")
    public List<EquipmentPackResponse> listStartingPacks() {
        return itemService.listStartingPacks();
    }

    @GetMapping("/equipment-packs/{packIndex}")
    public EquipmentPackResponse getStartingPack(@PathVariable String packIndex) {
        return itemService.getStartingPack(packIndex);
    }
}
