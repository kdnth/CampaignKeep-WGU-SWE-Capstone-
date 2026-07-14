package com.kdnth.campaignkeep.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("gear")
@PrimaryKeyJoinColumn(name = "item_id")
@Table(name = "gear_items")
@Getter
@Setter
@NoArgsConstructor
public class GearItem extends Item {

    @Override
    public String getSummary() {
        return getName();
    }
}
