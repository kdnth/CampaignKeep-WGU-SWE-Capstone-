package com.kdnth.campaignkeep.language;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LanguageServiceImpl implements LanguageService {
    private final LanguageRepository languageRepository;

    public LanguageServiceImpl(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    @Override
    public List<LanguageResponse> getLanguages() {
        return languageRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public LanguageResponse getLanguageById(Long languageId) {
        return toResponse(languageRepository.findById(languageId)
                                  .orElseThrow(() -> new NoSuchElementException("Language not found")));
    }

    private LanguageResponse toResponse(Language language) {
        return new LanguageResponse(
                language.getId(),
                language.getName()
        );
    }
}
