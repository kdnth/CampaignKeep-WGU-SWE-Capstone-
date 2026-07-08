package com.kdnth.campaignkeep.race;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "races")
@Getter
@Setter
@NoArgsConstructor
public class Race {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 25)
    private String name;

    @Column(name = "age_desc", length = 1000)
    private String ageDesc;

    @Column(name = "alignment_desc", length = 1000)
    private String alignmentDesc;

    @Column(name = "language_desc", length = 1000)
    private String languageDesc;

    @Column(length = 25)
    private String size;

    @Column(name = "size_desc", length = 1000)
    private String sizeDesc;

    private Short speed;

    @Column(length = 25)
    private String index; // dnd5eapi.co lookup key
}