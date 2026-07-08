package com.kdnth.campaignkeep.language;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/languages")
public class LanguageController {
    private final LanguageService languageService;
    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @GetMapping
    public List<LanguageResponse> getLanguages() {
        return languageService.getLanguages();
    }

    @GetMapping("/languages/{id}")
    public LanguageResponse getLanguageById(@PathVariable Long id) {
        return languageService.getLanguageById(id);
    }
}

