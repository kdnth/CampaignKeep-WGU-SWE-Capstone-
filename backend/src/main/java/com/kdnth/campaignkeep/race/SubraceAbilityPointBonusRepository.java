package com.kdnth.campaignkeep.race;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubraceAbilityPointBonusRepository extends JpaRepository<SubraceAbilityPointBonus, SubraceAbilityPointBonus.SubraceAbilityPointBonusId> {
    List<SubraceAbilityPointBonus> findBySubraceId(Long subraceId);
}
