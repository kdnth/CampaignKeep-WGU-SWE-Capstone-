package com.kdnth.campaignkeep.race;

import java.util.List;

public interface RaceService {

    List<RaceSummaryResponse> getRaces();

    RaceDetailResponse getRace(Long raceId);
}
