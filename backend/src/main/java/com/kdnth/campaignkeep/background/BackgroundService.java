package com.kdnth.campaignkeep.background;

import java.util.List;

public interface BackgroundService {

    List<BackgroundResponse> getBackgrounds();

    BackgroundResponse getBackground(Long backgroundId);

    BackgroundResponse createBackground(CreateBackgroundRequest request);
}
