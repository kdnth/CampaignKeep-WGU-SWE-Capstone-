package com.kdnth.campaignkeep.background;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/backgrounds")
public class BackgroundController {

    private final BackgroundService backgroundService;

    public BackgroundController(BackgroundService backgroundService) {
        this.backgroundService = backgroundService;
    }

    @GetMapping
    public List<BackgroundResponse> getBackgrounds() {
        return backgroundService.getBackgrounds();
    }

    @GetMapping("/{backgroundId}")
    public BackgroundResponse getBackground(@PathVariable Long backgroundId) {
        return backgroundService.getBackground(backgroundId);
    }

    @PostMapping
    public ResponseEntity<BackgroundResponse> createBackground(
            @Valid @RequestBody CreateBackgroundRequest request
    ) {
        BackgroundResponse response = backgroundService.createBackground(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
