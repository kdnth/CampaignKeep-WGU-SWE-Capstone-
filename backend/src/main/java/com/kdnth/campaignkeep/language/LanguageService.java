package com.kdnth.campaignkeep.language;

import java.util.List;

public interface LanguageService {
    List<LanguageResponse> getLanguages();
    LanguageResponse getLanguageById(Long languageId);
}
