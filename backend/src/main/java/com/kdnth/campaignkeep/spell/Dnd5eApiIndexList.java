package com.kdnth.campaignkeep.spell;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Dnd5eApiIndexList(
        int count,
        List<Dnd5eApiIndexItem> results
) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record Dnd5eApiIndexItem(
        String index,
        String name,
        Short level,
        String url
) {
}
