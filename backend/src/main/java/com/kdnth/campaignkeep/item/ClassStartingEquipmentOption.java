package com.kdnth.campaignkeep.item;

import com.kdnth.campaignkeep.dndclass.DndClass;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "class_starting_equipment_options")
@Getter
@Setter
@NoArgsConstructor
public class ClassStartingEquipmentOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private DndClass dndClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "option_group", nullable = false, length = 30)
    private String optionGroup;

    @Column(name = "sort_order", nullable = false)
    private Short sortOrder = 0;
}
