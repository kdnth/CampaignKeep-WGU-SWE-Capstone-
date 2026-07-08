package com.kdnth.campaignkeep.dndclass;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="classes")
@Getter
@Setter
@NoArgsConstructor
public class DndClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 25)
    private String name;

    @Column(name = "hit_dice", nullable = false)
    private Short hitDice;

    @Column(length=1000)
    private String description;

    @Column(length=50)
    private String index;
}
