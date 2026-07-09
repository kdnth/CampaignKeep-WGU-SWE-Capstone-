package com.kdnth.campaignkeep.sessionlog;

import com.kdnth.campaignkeep.base.AccessDeniedException;
import com.kdnth.campaignkeep.campaign.Campaign;
import com.kdnth.campaignkeep.campaign.CampaignMember;
import com.kdnth.campaignkeep.campaign.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionLogServiceImplTest {

    private static final Long CAMPAIGN_ID = 1L;
    private static final Long USER_ID = 10L;
    private static final Long SESSION_LOG_ID = 200L;

    @Mock
    private CampaignService campaignService;
    @Mock
    private SessionLogRepository sessionLogRepository;

    @InjectMocks
    private SessionLogServiceImpl sessionLogService;

    private Campaign campaign;
    private CampaignMember masterMembership;
    private CampaignMember playerMembership;

    @BeforeEach
    void setUp() {
        campaign = new Campaign();
        campaign.setId(CAMPAIGN_ID);

        masterMembership = new CampaignMember();
        masterMembership.setCampaign(campaign);

        playerMembership = new CampaignMember();
        playerMembership.setCampaign(campaign);
    }

    @Test
    void listSessionLogs_allowsMember() {
        when(campaignService.requireMembership(CAMPAIGN_ID, USER_ID)).thenReturn(playerMembership);

        SessionLog sessionLog = buildSessionLog();
        when(sessionLogRepository.findByCampaign_IdOrderByCreatedOnDesc(CAMPAIGN_ID))
                .thenReturn(List.of(sessionLog));

        List<SessionLogSummaryResponse> summaries = sessionLogService.listSessionLogs(CAMPAIGN_ID, USER_ID);

        assertEquals(1, summaries.size());
        assertEquals(SESSION_LOG_ID, summaries.get(0).id());
        assertEquals("Session 1", summaries.get(0).title());
    }

    @Test
    void listSessionLogs_rejectsNonMember() {
        when(campaignService.requireMembership(CAMPAIGN_ID, USER_ID))
                .thenThrow(new AccessDeniedException("denied"));

        assertThrows(
                AccessDeniedException.class,
                () -> sessionLogService.listSessionLogs(CAMPAIGN_ID, USER_ID)
        );
        verify(sessionLogRepository, never()).findByCampaign_IdOrderByCreatedOnDesc(CAMPAIGN_ID);
    }

    @Test
    void getSessionLog_allowsMember() {
        when(campaignService.requireMembership(CAMPAIGN_ID, USER_ID)).thenReturn(playerMembership);

        SessionLog sessionLog = buildSessionLog();
        when(sessionLogRepository.findByIdAndCampaign_Id(SESSION_LOG_ID, CAMPAIGN_ID))
                .thenReturn(Optional.of(sessionLog));

        SessionLogDetailResponse response = sessionLogService.getSessionLog(
                CAMPAIGN_ID,
                SESSION_LOG_ID,
                USER_ID
        );

        assertEquals(SESSION_LOG_ID, response.id());
        assertEquals("The party entered the cave.", response.body());
    }

    @Test
    void getSessionLog_returns404WhenWrongCampaign() {
        when(campaignService.requireMembership(CAMPAIGN_ID, USER_ID)).thenReturn(playerMembership);
        when(sessionLogRepository.findByIdAndCampaign_Id(SESSION_LOG_ID, CAMPAIGN_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> sessionLogService.getSessionLog(CAMPAIGN_ID, SESSION_LOG_ID, USER_ID)
        );
    }

    @Test
    void createSessionLog_requiresMaster() {
        when(campaignService.requireMaster(CAMPAIGN_ID, USER_ID))
                .thenThrow(new AccessDeniedException("denied"));

        assertThrows(
                AccessDeniedException.class,
                () -> sessionLogService.createSessionLog(
                        CAMPAIGN_ID,
                        USER_ID,
                        new CreateSessionLogRequest("Session 1", "Notes")
                )
        );
        verify(sessionLogRepository, never()).save(any(SessionLog.class));
    }

    @Test
    void createSessionLog_persistsLog() {
        when(campaignService.requireMaster(CAMPAIGN_ID, USER_ID)).thenReturn(masterMembership);
        when(sessionLogRepository.save(any(SessionLog.class))).thenAnswer(invocation -> {
            SessionLog sessionLog = invocation.getArgument(0);
            sessionLog.setId(SESSION_LOG_ID);
            sessionLog.setCreatedOn(LocalDateTime.of(2026, 1, 15, 20, 0));
            return sessionLog;
        });

        SessionLogDetailResponse response = sessionLogService.createSessionLog(
                CAMPAIGN_ID,
                USER_ID,
                new CreateSessionLogRequest("Session 1", "The party entered the cave.")
        );

        assertEquals(SESSION_LOG_ID, response.id());
        assertEquals("Session 1", response.title());
        assertEquals("The party entered the cave.", response.body());

        ArgumentCaptor<SessionLog> captor = ArgumentCaptor.forClass(SessionLog.class);
        verify(sessionLogRepository).save(captor.capture());
        assertEquals(campaign, captor.getValue().getCampaign());
        assertEquals("Session 1", captor.getValue().getTitle());
    }

    @Test
    void deleteSessionLog_requiresMaster() {
        when(campaignService.requireMaster(CAMPAIGN_ID, USER_ID))
                .thenThrow(new AccessDeniedException("denied"));

        assertThrows(
                AccessDeniedException.class,
                () -> sessionLogService.deleteSessionLog(CAMPAIGN_ID, SESSION_LOG_ID, USER_ID)
        );
        verify(sessionLogRepository, never()).delete(any(SessionLog.class));
    }

    @Test
    void deleteSessionLog_removesLog() {
        when(campaignService.requireMaster(CAMPAIGN_ID, USER_ID)).thenReturn(masterMembership);

        SessionLog sessionLog = buildSessionLog();
        when(sessionLogRepository.findByIdAndCampaign_Id(SESSION_LOG_ID, CAMPAIGN_ID))
                .thenReturn(Optional.of(sessionLog));

        sessionLogService.deleteSessionLog(CAMPAIGN_ID, SESSION_LOG_ID, USER_ID);

        verify(sessionLogRepository).delete(sessionLog);
    }

    @Test
    void deleteSessionLog_returns404WhenWrongCampaign() {
        when(campaignService.requireMaster(CAMPAIGN_ID, USER_ID)).thenReturn(masterMembership);
        when(sessionLogRepository.findByIdAndCampaign_Id(SESSION_LOG_ID, CAMPAIGN_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> sessionLogService.deleteSessionLog(CAMPAIGN_ID, SESSION_LOG_ID, USER_ID)
        );
        verify(sessionLogRepository, never()).delete(any(SessionLog.class));
    }

    private SessionLog buildSessionLog() {
        SessionLog sessionLog = new SessionLog();
        sessionLog.setId(SESSION_LOG_ID);
        sessionLog.setCampaign(campaign);
        sessionLog.setTitle("Session 1");
        sessionLog.setBody("The party entered the cave.");
        sessionLog.setCreatedOn(LocalDateTime.of(2026, 1, 15, 20, 0));
        return sessionLog;
    }
}
