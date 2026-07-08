package com.kdnth.campaignkeep.race;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subraces")
@Getter
@Setter
@NoArgsConstructor
public class Subrace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 1000)
    private String description;
}