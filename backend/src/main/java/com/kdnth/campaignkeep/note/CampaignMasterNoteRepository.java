package com.kdnth.campaignkeep.note;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CampaignMasterNoteRepository
        extends JpaRepository<CampaignMasterNote, CampaignMasterNote.CampaignMasterNoteId> {

    List<CampaignMasterNote> findByCampaign_IdOrderByNote_UpdatedOnDesc(Long campaignId);

    Optional<CampaignMasterNote> findByCampaign_IdAndNote_Id(Long campaignId, Long noteId);
}
