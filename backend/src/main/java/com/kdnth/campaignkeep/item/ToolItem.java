package com.kdnth.campaignkeep.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("tool")
@PrimaryKeyJoinColumn(name = "item_id")
@Table(name = "tool_items")
@Getter
@Setter
@NoArgsConstructor
public class ToolItem extends Item {

    @Override
    public String getSummary() {
        return getName();
    }
}
