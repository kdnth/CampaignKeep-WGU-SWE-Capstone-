package com.kdnth.campaignkeep.sessionlog;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.kdnth.campaignkeep.base.AuthUtil.getUserId;

@RestController
@RequestMapping("/api/campaigns/{campaignId}/session-logs")
public class SessionLogController {

    private final SessionLogService sessionLogService;

    public SessionLogController(SessionLogService sessionLogService) {
        this.sessionLogService = sessionLogService;
    }

    @GetMapping
    public List<SessionLogSummaryResponse> listSessionLogs(
            @PathVariable Long campaignId,
            Authentication authentication
    ) {
        return sessionLogService.listSessionLogs(campaignId, getUserId(authentication));
    }

    @GetMapping("/{sessionLogId}")
    public SessionLogDetailResponse getSessionLog(
            @PathVariable Long campaignId,
            @PathVariable Long sessionLogId,
            Authentication authentication
    ) {
        return sessionLogService.getSessionLog(campaignId, sessionLogId, getUserId(authentication));
    }

    @PostMapping
    public ResponseEntity<SessionLogDetailResponse> createSessionLog(
            @PathVariable Long campaignId,
            @Valid @RequestBody CreateSessionLogRequest request,
            Authentication authentication
    ) {
        SessionLogDetailResponse response = sessionLogService.createSessionLog(
                campaignId,
                getUserId(authentication),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{sessionLogId}")
    public ResponseEntity<Void> deleteSessionLog(
            @PathVariable Long campaignId,
            @PathVariable Long sessionLogId,
            Authentication authentication
    ) {
        sessionLogService.deleteSessionLog(campaignId, sessionLogId, getUserId(authentication));
        return ResponseEntity.noContent().build();
    }
}
