package com.kdnth.campaignkeep.spell;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Dnd5eSpellResponse(
        String index,
        String name,
        List<String> desc,
        List<String> higher_level,
        String range,
        List<String> components,
        String material,
        boolean ritual,
        String duration,
        boolean concentration,
        String casting_time,
        int level,
        Dnd5eNamedResource school,
        List<Dnd5eNamedResource> classes
) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record Dnd5eNamedResource(
        String index,
        String name
) {
}
