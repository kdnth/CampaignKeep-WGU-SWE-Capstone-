package com.kdnth.campaignkeep.sessionlog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionLogRepository extends JpaRepository<SessionLog, Long> {

    List<SessionLog> findByCampaign_IdOrderByCreatedOnDesc(Long campaignId);

    Optional<SessionLog> findByIdAndCampaign_Id(Long id, Long campaignId);
}
