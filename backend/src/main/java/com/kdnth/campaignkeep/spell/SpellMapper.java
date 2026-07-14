package com.kdnth.campaignkeep.spell;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final class SpellMapper {

    private static final Pattern FEET_PATTERN = Pattern.compile("(\\d+)\\s*feet", Pattern.CASE_INSENSITIVE);

    private SpellMapper() {
    }

    static Spell toEntity(Dnd5eSpellResponse response) {
        Spell spell = new Spell();
        spell.setApiIndex(response.index());
        spell.setName(response.name());
        spell.setLevel((short) response.level());
        spell.setCastingTime(response.casting_time());
        spell.setDuration(response.duration());
        spell.setConcentration(response.concentration());
        spell.setRitual(response.ritual());
        spell.setDescription(joinLines(response.desc()));
        spell.setHigherLevel(joinLines(response.higher_level()));

        if (response.school() != null) {
            spell.setSchool(MagicSchool.valueOf(response.school().index()));
        }

        List<String> components = response.components() != null ? response.components() : List.of();
        spell.setHasVerbal(components.contains("V"));
        spell.setHasSomatic(components.contains("S"));
        spell.setHasMaterial(components.contains("M"));
        spell.setComponents(String.join("", components));
        spell.setMaterialDesc(response.material());

        applyRange(spell, response.range());
        return spell;
    }

    static SpellSummaryResponse toSummary(Spell spell) {
        return new SpellSummaryResponse(
                spell.getId(),
                spell.getApiIndex(),
                spell.getName(),
                spell.getLevel(),
                spell.getSchool(),
                spell.getCastingTime(),
                Boolean.TRUE.equals(spell.getConcentration()),
                Boolean.TRUE.equals(spell.getRitual()),
                Boolean.TRUE.equals(spell.getHasVerbal()),
                Boolean.TRUE.equals(spell.getHasSomatic()),
                Boolean.TRUE.equals(spell.getHasMaterial())
        );
    }

    static SpellDetailResponse toDetail(Spell spell) {
        return new SpellDetailResponse(
                spell.getId(),
                spell.getApiIndex(),
                spell.getName(),
                spell.getLevel(),
                spell.getSchool(),
                spell.getCastingTime(),
                spell.getDuration(),
                spell.getRangeType(),
                spell.getRangeFeet(),
                formatRangeDisplay(spell),
                Boolean.TRUE.equals(spell.getConcentration()),
                Boolean.TRUE.equals(spell.getRitual()),
                Boolean.TRUE.equals(spell.getHasVerbal()),
                Boolean.TRUE.equals(spell.getHasSomatic()),
                Boolean.TRUE.equals(spell.getHasMaterial()),
                spell.getMaterialDesc(),
                spell.getDescription(),
                spell.getHigherLevel()
        );
    }

    private static void applyRange(Spell spell, String range) {
        if (range == null || range.isBlank()) {
            spell.setRangeType(SpellRangeType.special);
            return;
        }

        String normalized = range.trim().toLowerCase(Locale.ROOT);
        if (normalized.equals("self")) {
            spell.setRangeType(SpellRangeType.self);
            return;
        }
        if (normalized.equals("touch")) {
            spell.setRangeType(SpellRangeType.touch);
            return;
        }
        if (normalized.equals("sight")) {
            spell.setRangeType(SpellRangeType.sight);
            return;
        }
        if (normalized.equals("unlimited")) {
            spell.setRangeType(SpellRangeType.unlimited);
            return;
        }

        Matcher matcher = FEET_PATTERN.matcher(normalized);
        if (matcher.find()) {
            spell.setRangeType(SpellRangeType.ranged);
            spell.setRangeFeet(Short.parseShort(matcher.group(1)));
            return;
        }

        spell.setRangeType(SpellRangeType.special);
    }

    private static String formatRangeDisplay(Spell spell) {
        SpellRangeType rangeType = spell.getRangeType();
        if (rangeType == null) {
            return SpellRangeType.special.getDisplayName();
        }
        // Prefer if over enum switch: javac's SpellMapper$1 switch-map breaks under DevTools reloads.
        if (rangeType == SpellRangeType.ranged) {
            return spell.getRangeFeet() != null ? spell.getRangeFeet() + " feet" : rangeType.getDisplayName();
        }
        return rangeType.getDisplayName();
    }

    private static String joinLines(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return null;
        }
        return lines.stream()
                .filter(line -> line != null && !line.isBlank())
                .collect(Collectors.joining("\n\n"));
    }
}
