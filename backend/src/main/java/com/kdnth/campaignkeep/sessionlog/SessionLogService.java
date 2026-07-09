package com.kdnth.campaignkeep.sessionlog;

import java.util.List;

public interface SessionLogService {

    List<SessionLogSummaryResponse> listSessionLogs(Long campaignId, Long userId);

    SessionLogDetailResponse getSessionLog(Long campaignId, Long sessionLogId, Long userId);

    SessionLogDetailResponse createSessionLog(Long campaignId, Long userId, CreateSessionLogRequest request);

    void deleteSessionLog(Long campaignId, Long sessionLogId, Long userId);
}
