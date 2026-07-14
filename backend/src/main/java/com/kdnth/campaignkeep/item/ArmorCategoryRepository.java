package com.kdnth.campaignkeep.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArmorCategoryRepository extends JpaRepository<ArmorCategory, Long> {
    Optional<ArmorCategory> findByNameIgnoreCase(String name);
}
