package com.kdnth.campaignkeep.note;

import com.kdnth.campaignkeep.base.AccessDeniedException;
import com.kdnth.campaignkeep.campaign.Campaign;
import com.kdnth.campaignkeep.campaign.CampaignPlayableCharacterRepository;
import com.kdnth.campaignkeep.campaign.CampaignService;
import com.kdnth.campaignkeep.character.PlayableCharacter;
import com.kdnth.campaignkeep.character.PlayableCharacterRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NoteServiceImpl implements NoteService {

    private final CampaignService campaignService;
    private final CampaignPlayableCharacterRepository campaignPlayableCharacterRepository;
    private final PlayableCharacterRepository playableCharacterRepository;
    private final NoteRepository noteRepository;
    private final CharacterNoteRepository characterNoteRepository;
    private final CampaignMasterNoteRepository campaignMasterNoteRepository;

    public NoteServiceImpl(
            CampaignService campaignService,
            CampaignPlayableCharacterRepository campaignPlayableCharacterRepository,
            PlayableCharacterRepository playableCharacterRepository,
            NoteRepository noteRepository,
            CharacterNoteRepository characterNoteRepository,
            CampaignMasterNoteRepository campaignMasterNoteRepository
    ) {
        this.campaignService = campaignService;
        this.campaignPlayableCharacterRepository = campaignPlayableCharacterRepository;
        this.playableCharacterRepository = playableCharacterRepository;
        this.noteRepository = noteRepository;
        this.characterNoteRepository = characterNoteRepository;
        this.campaignMasterNoteRepository = campaignMasterNoteRepository;
    }

    @Override
    public NoteDetailResponse getPlayerNote(Long campaignId, Long characterId, Long userId) {
        PlayableCharacter character = resolveOwnedCharacterInCampaign(campaignId, characterId, userId);
        return characterNoteRepository.findByCharacter_Id(character.getId())
                .map(link -> toDetailResponse(link.getNote()))
                .orElse(emptyDetailResponse());
    }

    @Override
    @Transactional
    public NoteDetailResponse upsertPlayerNote(
            Long campaignId,
            Long characterId,
            Long userId,
            UpsertNoteRequest request
    ) {
        PlayableCharacter character = resolveOwnedCharacterInCampaign(campaignId, characterId, userId);

        CharacterNote characterNote = characterNoteRepository.findByCharacter_Id(character.getId())
                .orElseGet(() -> createCharacterNote(character));

        Note note = characterNote.getNote();
        note.setTitle(null);
        note.setBody(normalizeBody(request.body()));
        note = noteRepository.save(note);

        return toDetailResponse(note);
    }

    @Override
    public List<NoteSummaryResponse> listMasterNotes(Long campaignId, Long userId) {
        campaignService.requireMaster(campaignId, userId);
        return campaignMasterNoteRepository.findByCampaign_IdOrderByNote_UpdatedOnDesc(campaignId).stream()
                .map(link -> toSummaryResponse(link.getNote()))
                .toList();
    }

    @Override
    public NoteDetailResponse getMasterNote(Long campaignId, Long noteId, Long userId) {
        campaignService.requireMaster(campaignId, userId);
        Note note = findMasterNote(campaignId, noteId);
        return toDetailResponse(note);
    }

    @Override
    @Transactional
    public NoteDetailResponse createMasterNote(Long campaignId, Long userId, UpsertNoteRequest request) {
        Campaign campaign = campaignService.requireMaster(campaignId, userId).getCampaign();

        Note note = new Note();
        note.setTitle(normalizeTitle(request.title()));
        note.setBody(normalizeBody(request.body()));
        note = noteRepository.save(note);

        CampaignMasterNote link = new CampaignMasterNote();
        link.setCampaign(campaign);
        link.setNote(note);
        campaignMasterNoteRepository.save(link);

        return toDetailResponse(note);
    }

    @Override
    @Transactional
    public NoteDetailResponse updateMasterNote(
            Long campaignId,
            Long noteId,
            Long userId,
            UpsertNoteRequest request
    ) {
        campaignService.requireMaster(campaignId, userId);
        Note note = findMasterNote(campaignId, noteId);
        note.setTitle(normalizeTitle(request.title()));
        note.setBody(normalizeBody(request.body()));
        note = noteRepository.save(note);
        return toDetailResponse(note);
    }

    @Override
    @Transactional
    public void deleteMasterNote(Long campaignId, Long noteId, Long userId) {
        campaignService.requireMaster(campaignId, userId);
        CampaignMasterNote link = campaignMasterNoteRepository
                .findByCampaign_IdAndNote_Id(campaignId, noteId)
                .orElseThrow(() -> new NoSuchElementException("Note not found."));
        Note note = link.getNote();
        campaignMasterNoteRepository.delete(link);
        noteRepository.delete(note);
    }

    private PlayableCharacter resolveOwnedCharacterInCampaign(Long campaignId, Long characterId, Long userId) {
        campaignService.requireMembership(campaignId, userId);

        PlayableCharacter character = playableCharacterRepository.findById(characterId)
                .orElseThrow(() -> new NoSuchElementException("Character not found."));

        if (!character.canBeEditedBy(userId)) {
            throw new AccessDeniedException("You do not own this character.");
        }

        if (!campaignPlayableCharacterRepository.existsByCharacterIdAndCampaignId(characterId, campaignId)) {
            throw new NoSuchElementException("No character attached to this campaign.");
        }

        return character;
    }

    private CharacterNote createCharacterNote(PlayableCharacter character) {
        Note note = new Note();
        note.setBody("");
        note = noteRepository.save(note);

        CharacterNote characterNote = new CharacterNote();
        characterNote.setCharacter(character);
        characterNote.setNote(note);
        return characterNoteRepository.save(characterNote);
    }

    private Note findMasterNote(Long campaignId, Long noteId) {
        return campaignMasterNoteRepository.findByCampaign_IdAndNote_Id(campaignId, noteId)
                .map(CampaignMasterNote::getNote)
                .orElseThrow(() -> new NoSuchElementException("Note not found."));
    }

    private static String normalizeTitle(String title) {
        if (title == null) {
            return null;
        }
        String trimmed = title.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String normalizeBody(String body) {
        return body == null ? "" : body;
    }

    private static NoteDetailResponse emptyDetailResponse() {
        return new NoteDetailResponse(null, null, "", null, null);
    }

    private static NoteDetailResponse toDetailResponse(Note note) {
        return new NoteDetailResponse(
                note.getId(),
                note.getTitle(),
                note.getBody(),
                note.getCreatedOn(),
                note.getUpdatedOn()
        );
    }

    private static NoteSummaryResponse toSummaryResponse(Note note) {
        return new NoteSummaryResponse(note.getId(), note.getTitle(), note.getUpdatedOn());
    }
}
