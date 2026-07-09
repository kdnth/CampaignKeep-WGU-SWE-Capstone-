package com.kdnth.campaignkeep.note;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.kdnth.campaignkeep.base.AuthUtil.getUserId;

@RestController
@RequestMapping("/api/campaigns/{campaignId}")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/player-note")
    public NoteDetailResponse getPlayerNote(
            @PathVariable Long campaignId,
            @RequestParam Long characterId,
            Authentication authentication
    ) {
        return noteService.getPlayerNote(campaignId, characterId, getUserId(authentication));
    }

    @PutMapping("/player-note")
    public NoteDetailResponse upsertPlayerNote(
            @PathVariable Long campaignId,
            @RequestParam Long characterId,
            @Valid @RequestBody UpsertNoteRequest request,
            Authentication authentication
    ) {
        return noteService.upsertPlayerNote(campaignId, characterId, getUserId(authentication), request);
    }

    @GetMapping("/master-notes")
    public List<NoteSummaryResponse> listMasterNotes(
            @PathVariable Long campaignId,
            Authentication authentication
    ) {
        return noteService.listMasterNotes(campaignId, getUserId(authentication));
    }

    @PostMapping("/master-notes")
    public ResponseEntity<NoteDetailResponse> createMasterNote(
            @PathVariable Long campaignId,
            @Valid @RequestBody UpsertNoteRequest request,
            Authentication authentication
    ) {
        NoteDetailResponse response = noteService.createMasterNote(
                campaignId,
                getUserId(authentication),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/master-notes/{noteId}")
    public NoteDetailResponse getMasterNote(
            @PathVariable Long campaignId,
            @PathVariable Long noteId,
            Authentication authentication
    ) {
        return noteService.getMasterNote(campaignId, noteId, getUserId(authentication));
    }

    @PutMapping("/master-notes/{noteId}")
    public NoteDetailResponse updateMasterNote(
            @PathVariable Long campaignId,
            @PathVariable Long noteId,
            @Valid @RequestBody UpsertNoteRequest request,
            Authentication authentication
    ) {
        return noteService.updateMasterNote(campaignId, noteId, getUserId(authentication), request);
    }

    @DeleteMapping("/master-notes/{noteId}")
    public ResponseEntity<Void> deleteMasterNote(
            @PathVariable Long campaignId,
            @PathVariable Long noteId,
            Authentication authentication
    ) {
        noteService.deleteMasterNote(campaignId, noteId, getUserId(authentication));
        return ResponseEntity.noContent().build();
    }
}
