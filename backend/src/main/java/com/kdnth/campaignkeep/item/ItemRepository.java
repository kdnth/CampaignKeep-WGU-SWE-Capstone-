package com.kdnth.campaignkeep.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByCampaignIsNull();

    List<Item> findByCampaignIsNullOrCampaign_Id(Long campaignId);

    Optional<Item> findByApiIndex(String apiIndex);
}
