package com.kdnth.campaignkeep.item;

import java.util.List;

public interface InventoryService {

    CharacterInventoryResponse getInventory(Long characterId, Long callerId);

    CharacterInventoryResponse submitStartingEquipment(Long characterId, SubmitStartingEquipmentRequest request, Long callerId);

    CharacterInventoryResponse updateInventoryItem(Long characterId, Long inventoryItemId, UpdateInventoryItemRequest request, Long callerId);

    void removeInventoryItem(Long characterId, Long inventoryItemId, Long callerId);

    CharacterInventoryResponse updateGold(Long characterId, UpdateGoldRequest request, Long callerId);

    List<CharacterAttackResponse> getAttacks(Long characterId, Long callerId);

    CharacterInventoryResponse grantItem(Long campaignId, Long characterId, GrantItemRequest request, Long callerId);

    CharacterInventoryResponse submitStartingPack(Long characterId, SubmitStartingPackRequest request, Long callerId);
}
