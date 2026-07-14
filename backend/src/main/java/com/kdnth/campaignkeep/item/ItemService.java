package com.kdnth.campaignkeep.item;

import java.util.List;

public interface ItemService {

    ItemDetailResponse getItemById(Long id, Long callerId);

    List<ItemDetailResponse> listCampaignItems(Long campaignId, Long callerId);

    ItemDetailResponse createCampaignItem(Long campaignId, CreateCampaignItemRequest request, Long callerId);

    StartingEquipmentOptionsResponse getStartingEquipmentOptions(Long classId);

    List<EquipmentPackResponse> listStartingPacks();

    EquipmentPackResponse getStartingPack(String packIndex);

    Item findOrCreateFromApi(String apiIndex);
}
