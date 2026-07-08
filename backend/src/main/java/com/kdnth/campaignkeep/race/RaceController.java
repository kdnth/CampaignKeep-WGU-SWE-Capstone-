package com.kdnth.campaignkeep.race;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/races")
public class RaceController {

    private final RaceService raceService;

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    @GetMapping
    public List<RaceSummaryResponse> getRaces() {
        return raceService.getRaces();
    }

    @GetMapping("/{raceId}")
    public RaceDetailResponse getRace(@PathVariable Long raceId) {
        return raceService.getRace(raceId);
    }
}
