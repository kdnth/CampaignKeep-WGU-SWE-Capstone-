package com.kdnth.campaignkeep.background;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BackgroundServiceImpl implements BackgroundService {

    private final BackgroundRepository backgroundRepository;

    public BackgroundServiceImpl(BackgroundRepository backgroundRepository) {
        this.backgroundRepository = backgroundRepository;
    }

    @Override
    public List<BackgroundResponse> getBackgrounds() {
        return backgroundRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public BackgroundResponse getBackground(Long backgroundId) {
        Background background = backgroundRepository.findById(backgroundId)
                .orElseThrow(() -> new NoSuchElementException("Background not found"));
        return toResponse(background);
    }

    @Override
    public BackgroundResponse createBackground(CreateBackgroundRequest request) {
        Background background = new Background();
        background.setName(request.name());
        background.setDescription(request.description());
        background = backgroundRepository.save(background);
        return toResponse(background);
    }

    private BackgroundResponse toResponse(Background background) {
        return new BackgroundResponse(
                background.getId(),
                background.getName(),
                background.getDescription()
        );
    }
}
