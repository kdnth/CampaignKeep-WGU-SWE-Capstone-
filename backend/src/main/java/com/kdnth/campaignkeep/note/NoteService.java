package com.kdnth.campaignkeep.note;

import java.util.List;

public interface NoteService {
    NoteDetailResponse getPlayerNote(Long campaignId, Long characterId, Long userId);

    NoteDetailResponse upsertPlayerNote(Long campaignId, Long characterId, Long userId, UpsertNoteRequest request);

    List<NoteSummaryResponse> listMasterNotes(Long campaignId, Long userId);

    NoteDetailResponse getMasterNote(Long campaignId, Long noteId, Long userId);

    NoteDetailResponse createMasterNote(Long campaignId, Long userId, UpsertNoteRequest request);

    NoteDetailResponse updateMasterNote(Long campaignId, Long noteId, Long userId, UpsertNoteRequest request);

    void deleteMasterNote(Long campaignId, Long noteId, Long userId);
}
