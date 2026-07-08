package com.kdnth.campaignkeep.race;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RaceServiceImpl implements RaceService {

    private final RaceRepository raceRepository;
    private final RaceAbilityPointBonusRepository raceAbilityPointBonusRepository;

    public RaceServiceImpl(
            RaceRepository raceRepository,
            RaceAbilityPointBonusRepository raceAbilityPointBonusRepository
    ) {
        this.raceRepository = raceRepository;
        this.raceAbilityPointBonusRepository = raceAbilityPointBonusRepository;
    }

    @Override
    public List<RaceSummaryResponse> getRaces() {
        return raceRepository.findAll().stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Override
    @Transactional
    public RaceDetailResponse getRace(Long raceId) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new NoSuchElementException("Race not found"));
        List<RaceAbilityPointBonusResponse> bonuses = raceAbilityPointBonusRepository
                .findByRaceId(raceId).stream()
                .map(this::toBonusResponse)
                .toList();
        return toDetailResponse(race, bonuses);
    }

    private RaceSummaryResponse toSummaryResponse(Race race) {
        return new RaceSummaryResponse(
                race.getId(),
                race.getName(),
                race.getSize(),
                race.getSpeed(),
                race.getIndex()
        );
    }

    private RaceDetailResponse toDetailResponse(
            Race race,
            List<RaceAbilityPointBonusResponse> bonuses
    ) {
        return new RaceDetailResponse(
                race.getId(),
                race.getName(),
                race.getAgeDesc(),
                race.getAlignmentDesc(),
                race.getLanguageDesc(),
                race.getSize(),
                race.getSizeDesc(),
                race.getSpeed(),
                race.getIndex(),
                bonuses
        );
    }

    private RaceAbilityPointBonusResponse toBonusResponse(RaceAbilityPointBonus bonus) {
        return new RaceAbilityPointBonusResponse(
                bonus.getAbility().getId(),
                bonus.getAbility().getName(),
                bonus.getValue()
        );
    }
}
