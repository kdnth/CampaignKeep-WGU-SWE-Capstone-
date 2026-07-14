package com.kdnth.campaignkeep.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeaponCategoryRepository extends JpaRepository<WeaponCategory, Long> {
    Optional<WeaponCategory> findByNameIgnoreCase(String name);
}
