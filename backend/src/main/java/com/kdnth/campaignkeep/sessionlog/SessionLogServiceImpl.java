package com.kdnth.campaignkeep.sessionlog;

import com.kdnth.campaignkeep.campaign.Campaign;
import com.kdnth.campaignkeep.campaign.CampaignService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SessionLogServiceImpl implements SessionLogService {

    private final CampaignService campaignService;
    private final SessionLogRepository sessionLogRepository;

    public SessionLogServiceImpl(CampaignService campaignService, SessionLogRepository sessionLogRepository) {
        this.campaignService = campaignService;
        this.sessionLogRepository = sessionLogRepository;
    }

    @Override
    public List<SessionLogSummaryResponse> listSessionLogs(Long campaignId, Long userId) {
        campaignService.requireMembership(campaignId, userId);
        return sessionLogRepository.findByCampaign_IdOrderByCreatedOnDesc(campaignId).stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Override
    public SessionLogDetailResponse getSessionLog(Long campaignId, Long sessionLogId, Long userId) {
        campaignService.requireMembership(campaignId, userId);
        return toDetailResponse(findSessionLog(campaignId, sessionLogId));
    }

    @Override
    @Transactional
    public SessionLogDetailResponse createSessionLog(
            Long campaignId,
            Long userId,
            CreateSessionLogRequest request
    ) {
        Campaign campaign = campaignService.requireMaster(campaignId, userId).getCampaign();

        SessionLog sessionLog = new SessionLog();
        sessionLog.setCampaign(campaign);
        sessionLog.setTitle(normalizeTitle(request.title()));
        sessionLog.setBody(normalizeBody(request.body()));
        sessionLog = sessionLogRepository.save(sessionLog);

        return toDetailResponse(sessionLog);
    }

    @Override
    @Transactional
    public void deleteSessionLog(Long campaignId, Long sessionLogId, Long userId) {
        campaignService.requireMaster(campaignId, userId);
        SessionLog sessionLog = findSessionLog(campaignId, sessionLogId);
        sessionLogRepository.delete(sessionLog);
    }

    private SessionLog findSessionLog(Long campaignId, Long sessionLogId) {
        return sessionLogRepository.findByIdAndCampaign_Id(sessionLogId, campaignId)
                .orElseThrow(() -> new NoSuchElementException("Session log not found."));
    }

    private SessionLogSummaryResponse toSummaryResponse(SessionLog sessionLog) {
        return new SessionLogSummaryResponse(
                sessionLog.getId(),
                sessionLog.getTitle(),
                sessionLog.getCreatedOn()
        );
    }

    private SessionLogDetailResponse toDetailResponse(SessionLog sessionLog) {
        return new SessionLogDetailResponse(
                sessionLog.getId(),
                sessionLog.getTitle(),
                sessionLog.getBody(),
                sessionLog.getCreatedOn()
        );
    }

    private String normalizeTitle(String title) {
        return title == null ? "" : title.trim();
    }

    private String normalizeBody(String body) {
        return body == null ? "" : body;
    }
}
