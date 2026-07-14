package com.kdnth.campaignkeep.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("armor")
@PrimaryKeyJoinColumn(name = "item_id")
@Table(name = "armor")
@Getter
@Setter
@NoArgsConstructor
public class Armor extends Item {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "armor_category_id", nullable = false)
    private ArmorCategory armorCategory;

    @Column(name = "base_ac", nullable = false)
    private Short baseAc;

    @Column(name = "max_dex_bonus")
    private Short maxDexBonus;

    @Column(name = "str_minimum")
    private Short strMinimum;

    @Override
    public String getSummary() {
        return getName() + " (AC " + baseAc + ")";
    }

    public boolean isShield() {
        return armorCategory != null && "shield".equals(armorCategory.getName());
    }

    public String getArmorCategoryName() {
        return armorCategory != null ? armorCategory.getName() : null;
    }
}
