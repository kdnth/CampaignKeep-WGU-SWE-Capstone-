package com.kdnth.campaignkeep.race;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SubraceServiceImpl implements SubraceService {

    private final SubraceRepository subraceRepository;
    private final SubraceAbilityPointBonusRepository subraceAbilityPointBonusRepository;

    public SubraceServiceImpl(
            SubraceRepository subraceRepository,
            SubraceAbilityPointBonusRepository subraceAbilityPointBonusRepository
    ) {
        this.subraceRepository = subraceRepository;
        this.subraceAbilityPointBonusRepository = subraceAbilityPointBonusRepository;
    }

    @Override
    @Transactional
    public List<SubraceSummaryResponse> getSubraces() {
        return subraceRepository.findAll().stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Override
    @Transactional
    public SubraceDetailResponse getSubrace(Long subraceId) {
        Subrace subrace = subraceRepository.findById(subraceId)
                .orElseThrow(() -> new NoSuchElementException("Subrace not found"));
        List<SubraceAbilityPointBonusResponse> bonuses = subraceAbilityPointBonusRepository
                .findBySubraceId(subraceId).stream()
                .map(this::toBonusResponse)
                .toList();
        return toDetailResponse(subrace, bonuses);
    }

    private SubraceSummaryResponse toSummaryResponse(Subrace subrace) {
        return new SubraceSummaryResponse(
                subrace.getId(),
                subrace.getRace().getId(),
                subrace.getRace().getName(),
                subrace.getName()
        );
    }

    private SubraceDetailResponse toDetailResponse(
            Subrace subrace,
            List<SubraceAbilityPointBonusResponse> bonuses
    ) {
        return new SubraceDetailResponse(
                subrace.getId(),
                subrace.getRace().getId(),
                subrace.getRace().getName(),
                subrace.getName(),
                subrace.getDescription(),
                bonuses
        );
    }

    private SubraceAbilityPointBonusResponse toBonusResponse(SubraceAbilityPointBonus bonus) {
        return new SubraceAbilityPointBonusResponse(
                bonus.getAbility().getId(),
                bonus.getAbility().getName(),
                bonus.getValue()
        );
    }
}
