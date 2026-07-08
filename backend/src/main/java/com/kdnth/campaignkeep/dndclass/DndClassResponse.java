package com.kdnth.campaignkeep.dndclass;

public record DndClassResponse(
        Long id,
        String name,
        Short hitDice,
        String description,
        String index
) {
}
