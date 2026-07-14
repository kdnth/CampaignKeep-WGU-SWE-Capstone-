package com.kdnth.campaignkeep.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("clothing")
@PrimaryKeyJoinColumn(name = "item_id")
@Table(name = "clothing_items")
@Getter
@Setter
@NoArgsConstructor
public class ClothingItem extends Item {

    @Override
    public String getSummary() {
        return getName();
    }
}
