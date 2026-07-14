package com.kdnth.campaignkeep.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@DiscriminatorValue("weapon")
@PrimaryKeyJoinColumn(name = "item_id")
@Table(name = "weapons")
@Getter
@Setter
@NoArgsConstructor
public class Weapon extends Item {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weapon_category_id")
    private WeaponCategory weaponCategory;

    @Column(length = 10)
    private String damage;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "damage_type")
    private DamageType damageType;

    private Short range;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "weapon_range")
    private WeaponRange weaponRange;

    @Override
    public String getSummary() {
        return getName() + " (" + damage + ")";
    }

    public boolean isFinesse() {
        String n = getName() != null ? getName().toLowerCase() : "";
        return n.contains("dagger") || n.contains("rapier") || n.contains("shortsword");
    }
}
