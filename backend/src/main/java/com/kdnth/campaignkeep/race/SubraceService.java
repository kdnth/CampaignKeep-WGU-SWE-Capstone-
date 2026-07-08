package com.kdnth.campaignkeep.race;

import java.util.List;

public interface SubraceService {

    List<SubraceSummaryResponse> getSubraces();

    SubraceDetailResponse getSubrace(Long subraceId);
}
