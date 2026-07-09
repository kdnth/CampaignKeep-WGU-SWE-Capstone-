package com.kdnth.campaignkeep.spell;

import com.kdnth.campaignkeep.character.Character;

import java.util.List;
import java.util.Set;

public interface SpellService {

    List<SpellSummaryResponse> listSpells(
            String search,
            Short level,
            MagicSchool school,
            String classIndex,
            Short maxLevel
    );

    SpellDetailResponse getSpellById(Long id);

    SpellDetailResponse getSpellByApiIndex(String apiIndex);

    List<SpellDetailResponse> getCharacterSpells(Long characterId, Long callerId);

    SpellDetailResponse addSpellToCharacter(Long characterId, Long spellId, Long callerId);

    void removeSpellFromCharacter(Long characterId, Long spellId, Long callerId);

    void assignSpellsToCharacter(Character character, List<Long> spellIds);

    void validateCreationSpells(
            Long classId,
            Long subraceId,
            List<Long> spellIds
    );

    Set<String> getWizardSpellIndices();
}
