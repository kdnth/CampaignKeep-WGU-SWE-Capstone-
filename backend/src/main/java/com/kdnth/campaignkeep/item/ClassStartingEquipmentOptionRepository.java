package com.kdnth.campaignkeep.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassStartingEquipmentOptionRepository extends JpaRepository<ClassStartingEquipmentOption, Long> {

    List<ClassStartingEquipmentOption> findByDndClass_IdOrderBySortOrderAsc(Long classId);
}
