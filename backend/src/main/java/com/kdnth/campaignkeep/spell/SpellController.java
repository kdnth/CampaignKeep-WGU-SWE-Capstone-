package com.kdnth.campaignkeep.spell;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spells")
public class SpellController {

    private final SpellService spellService;

    public SpellController(SpellService spellService) {
        this.spellService = spellService;
    }

    @GetMapping
    public List<SpellSummaryResponse> listSpells(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Short level,
            @RequestParam(required = false) MagicSchool school,
            @RequestParam(required = false) String classIndex,
            @RequestParam(required = false) Short maxLevel
    ) {
        return spellService.listSpells(search, level, school, classIndex, maxLevel);
    }

    @GetMapping("/{id}")
    public SpellDetailResponse getSpellById(@PathVariable Long id) {
        return spellService.getSpellById(id);
    }

    @GetMapping("/by-index/{apiIndex}")
    public SpellDetailResponse getSpellByApiIndex(@PathVariable String apiIndex) {
        return spellService.getSpellByApiIndex(apiIndex);
    }
}
