package com.kdnth.campaignkeep.race;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subraces")
public class SubraceController {

    private final SubraceService subraceService;

    public SubraceController(SubraceService subraceService) {
        this.subraceService = subraceService;
    }

    @GetMapping
    public List<SubraceSummaryResponse> getSubraces() {
        return subraceService.getSubraces();
    }

    @GetMapping("/{subraceId}")
    public SubraceDetailResponse getSubrace(@PathVariable Long subraceId) {
        return subraceService.getSubrace(subraceId);
    }
}
