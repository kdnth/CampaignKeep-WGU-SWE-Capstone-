package com.kdnth.campaignkeep.item;

public enum ItemRarity {
    common,
    uncommon,
    rare,
    very_rare,
    legendary,
    artifact;

    public int costMultiplier() {
        return switch (this) {
            case common -> 1;
            case uncommon -> 2;
            case rare -> 5;
            case very_rare -> 10;
            case legendary -> 25;
            case artifact -> 50;
        };
    }
}
