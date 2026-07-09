package com.kdnth.campaignkeep.spell;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SpellRepository extends JpaRepository<Spell, Long> {

    Optional<Spell> findByApiIndex(String apiIndex);

    List<Spell> findByApiIndexIn(Collection<String> apiIndices);

    long countByApiIndexIsNotNull();
}
