package com.kdnth.campaignkeep.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("jewelry")
@PrimaryKeyJoinColumn(name = "item_id")
@Table(name = "jewelry_items")
@Getter
@Setter
@NoArgsConstructor
public class JewelryItem extends Item {

    @Override
    public String getSummary() {
        return getName();
    }
}
