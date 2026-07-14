package com.kdnth.campaignkeep.spell;

import lombok.Getter;

@Getter
public enum SpellRangeType {
    self("Self"),
    touch("Touch"),
    ranged("Ranged"),
    special("Special"),
    sight("Sight"),
    unlimited("Unlimited");

    private final String displayName;

    SpellRangeType(String displayName) {
        this.displayName = displayName;
    }

}
