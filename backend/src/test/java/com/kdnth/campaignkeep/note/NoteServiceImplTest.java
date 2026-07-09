package com.kdnth.campaignkeep.note;

import com.kdnth.campaignkeep.base.AccessDeniedException;
import com.kdnth.campaignkeep.campaign.Campaign;
import com.kdnth.campaignkeep.campaign.CampaignMember;
import com.kdnth.campaignkeep.campaign.CampaignPlayableCharacterRepository;
import com.kdnth.campaignkeep.campaign.CampaignService;
import com.kdnth.campaignkeep.character.PlayableCharacter;
import com.kdnth.campaignkeep.character.PlayableCharacterRepository;
import com.kdnth.campaignkeep.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {

    private static final Long CAMPAIGN_ID = 1L;
    private static final Long USER_ID = 10L;
    private static final Long CHARACTER_ID = 100L;
    private static final Long NOTE_ID = 200L;

    @Mock
    private CampaignService campaignService;
    @Mock
    private CampaignPlayableCharacterRepository campaignPlayableCharacterRepository;
    @Mock
    private PlayableCharacterRepository playableCharacterRepository;
    @Mock
    private NoteRepository noteRepository;
    @Mock
    private CharacterNoteRepository characterNoteRepository;
    @Mock
    private CampaignMasterNoteRepository campaignMasterNoteRepository;

    @InjectMocks
    private NoteServiceImpl noteService;

    private PlayableCharacter playableCharacter;
    private Campaign campaign;
    private CampaignMember masterMembership;

    @BeforeEach
    void setUp() {
        User player = new User();
        player.setId(USER_ID);

        playableCharacter = new PlayableCharacter();
        playableCharacter.setId(CHARACTER_ID);
        playableCharacter.setPlayer(player);

        campaign = new Campaign();
        campaign.setId(CAMPAIGN_ID);

        masterMembership = new CampaignMember();
        masterMembership.setCampaign(campaign);
    }

    @Test
    void getPlayerNote_returnsEmptyShellWhenNoNoteExists() {
        stubOwnedCharacterInCampaign();

        NoteDetailResponse response = noteService.getPlayerNote(CAMPAIGN_ID, CHARACTER_ID, USER_ID);

        assertNull(response.id());
        assertEquals("", response.body());
    }

    @Test
    void upsertPlayerNote_createsNoteOnFirstSave() {
        stubOwnedCharacterInCampaign();
        when(characterNoteRepository.findByCharacter_Id(CHARACTER_ID)).thenReturn(Optional.empty());
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> {
            Note note = invocation.getArgument(0);
            note.setId(NOTE_ID);
            return note;
        });
        when(characterNoteRepository.save(any(CharacterNote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NoteDetailResponse response = noteService.upsertPlayerNote(
                CAMPAIGN_ID,
                CHARACTER_ID,
                USER_ID,
                new UpsertNoteRequest("My Note", "Remember the secret door.")
        );

        assertEquals(NOTE_ID, response.id());
        assertNull(response.title());
        assertEquals("Remember the secret door.", response.body());
        verify(characterNoteRepository).save(any(CharacterNote.class));
    }

    @Test
    void upsertPlayerNote_updatesExistingNote() {
        stubOwnedCharacterInCampaign();

        Note existingNote = new Note();
        existingNote.setId(NOTE_ID);
        existingNote.setTitle("Old");
        existingNote.setBody("Old body");

        CharacterNote characterNote = new CharacterNote();
        characterNote.setCharacter(playableCharacter);
        characterNote.setNote(existingNote);

        when(characterNoteRepository.findByCharacter_Id(CHARACTER_ID)).thenReturn(Optional.of(characterNote));
        when(noteRepository.save(existingNote)).thenReturn(existingNote);

        NoteDetailResponse response = noteService.upsertPlayerNote(
                CAMPAIGN_ID,
                CHARACTER_ID,
                USER_ID,
                new UpsertNoteRequest("Updated", "New body")
        );

        assertNull(response.title());
        assertEquals("New body", response.body());
        verify(characterNoteRepository, never()).save(any(CharacterNote.class));
    }

    @Test
    void getPlayerNote_rejectsWhenCharacterNotAttachedToCampaign() {
        when(campaignService.requireMembership(CAMPAIGN_ID, USER_ID)).thenReturn(masterMembership);
        when(playableCharacterRepository.findById(CHARACTER_ID)).thenReturn(Optional.of(playableCharacter));
        when(campaignPlayableCharacterRepository.existsByCharacterIdAndCampaignId(CHARACTER_ID, CAMPAIGN_ID))
                .thenReturn(false);

        assertThrows(
                NoSuchElementException.class,
                () -> noteService.getPlayerNote(CAMPAIGN_ID, CHARACTER_ID, USER_ID)
        );
    }

    @Test
    void getPlayerNote_rejectsNonOwner() {
        when(campaignService.requireMembership(CAMPAIGN_ID, USER_ID)).thenReturn(masterMembership);

        User otherPlayer = new User();
        otherPlayer.setId(999L);
        PlayableCharacter otherCharacter = new PlayableCharacter();
        otherCharacter.setId(CHARACTER_ID);
        otherCharacter.setPlayer(otherPlayer);

        when(playableCharacterRepository.findById(CHARACTER_ID)).thenReturn(Optional.of(otherCharacter));

        assertThrows(
                AccessDeniedException.class,
                () -> noteService.getPlayerNote(CAMPAIGN_ID, CHARACTER_ID, USER_ID)
        );
    }

    @Test
    void listMasterNotes_requiresMaster() {
        when(campaignService.requireMaster(CAMPAIGN_ID, USER_ID)).thenThrow(new AccessDeniedException("denied"));

        assertThrows(AccessDeniedException.class, () -> noteService.listMasterNotes(CAMPAIGN_ID, USER_ID));
    }

    @Test
    void createMasterNote_persistsNoteAndLink() {
        when(campaignService.requireMaster(CAMPAIGN_ID, USER_ID)).thenReturn(masterMembership);
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> {
            Note note = invocation.getArgument(0);
            note.setId(NOTE_ID);
            return note;
        });

        NoteDetailResponse response = noteService.createMasterNote(
                CAMPAIGN_ID,
                USER_ID,
                new UpsertNoteRequest("Session prep", "Villain monologue")
        );

        assertEquals(NOTE_ID, response.id());
        assertEquals("Session prep", response.title());

        ArgumentCaptor<CampaignMasterNote> linkCaptor = ArgumentCaptor.forClass(CampaignMasterNote.class);
        verify(campaignMasterNoteRepository).save(linkCaptor.capture());
        assertEquals(campaign, linkCaptor.getValue().getCampaign());
    }

    @Test
    void deleteMasterNote_removesJunctionAndNote() {
        when(campaignService.requireMaster(CAMPAIGN_ID, USER_ID)).thenReturn(masterMembership);

        Note note = new Note();
        note.setId(NOTE_ID);

        CampaignMasterNote link = new CampaignMasterNote();
        link.setCampaign(campaign);
        link.setNote(note);

        when(campaignMasterNoteRepository.findByCampaign_IdAndNote_Id(CAMPAIGN_ID, NOTE_ID))
                .thenReturn(Optional.of(link));

        noteService.deleteMasterNote(CAMPAIGN_ID, NOTE_ID, USER_ID);

        verify(campaignMasterNoteRepository).delete(link);
        verify(noteRepository).delete(note);
    }

    @Test
    void listMasterNotes_returnsSummariesWithoutBody() {
        when(campaignService.requireMaster(CAMPAIGN_ID, USER_ID)).thenReturn(masterMembership);

        Note note = new Note();
        note.setId(NOTE_ID);
        note.setTitle("Plot hook");
        note.setBody("Secret body");

        CampaignMasterNote link = new CampaignMasterNote();
        link.setNote(note);

        when(campaignMasterNoteRepository.findByCampaign_IdOrderByNote_UpdatedOnDesc(CAMPAIGN_ID))
                .thenReturn(List.of(link));

        List<NoteSummaryResponse> summaries = noteService.listMasterNotes(CAMPAIGN_ID, USER_ID);

        assertEquals(1, summaries.size());
        assertEquals(NOTE_ID, summaries.get(0).id());
        assertEquals("Plot hook", summaries.get(0).title());
    }

    private void stubOwnedCharacterInCampaign() {
        when(campaignService.requireMembership(CAMPAIGN_ID, USER_ID)).thenReturn(masterMembership);
        when(playableCharacterRepository.findById(CHARACTER_ID)).thenReturn(Optional.of(playableCharacter));
        when(campaignPlayableCharacterRepository.existsByCharacterIdAndCampaignId(CHARACTER_ID, CAMPAIGN_ID))
                .thenReturn(true);
        when(characterNoteRepository.findByCharacter_Id(CHARACTER_ID)).thenReturn(Optional.empty());
    }
}
