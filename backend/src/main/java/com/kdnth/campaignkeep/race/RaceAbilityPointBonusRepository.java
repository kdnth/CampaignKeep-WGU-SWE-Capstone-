package com.kdnth.campaignkeep.race;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RaceAbilityPointBonusRepository extends JpaRepository<RaceAbilityPointBonus, RaceAbilityPointBonus.RaceAbilityPointBonusId> {
    List<RaceAbilityPointBonus> findByRaceId(Long raceId);
}