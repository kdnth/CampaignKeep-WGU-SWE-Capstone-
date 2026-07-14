package com.kdnth.campaignkeep.character;

import com.kdnth.campaignkeep.base.Auditable;
import com.kdnth.campaignkeep.race.Race;
import com.kdnth.campaignkeep.race.Subrace;
import com.kdnth.campaignkeep.background.Background;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "characters")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "character_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public abstract class Character extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subrace_id")
    private Subrace subrace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "background_id")
    private Background background;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private CharacterStatus status;

    @Column(nullable = false)
    private Short strength;

    @Column(nullable = false)
    private Short dexterity;

    @Column(nullable = false)
    private Short constitution;

    @Column(nullable = false)
    private Short intelligence;

    @Column(nullable = false)
    private Short wisdom;

    @Column(nullable = false)
    private Short charisma;

    @Column(name = "hit_points", nullable = false)
    private Short hitPoints;

    @Column(name = "armor_class", nullable = false)
    private Short armorClass;

    @Column(name = "initiative_bonus", nullable = false)
    private Short initiativeBonus;

    @Column(nullable = false)
    private Short speed;

    @Column(nullable = false)
    private Integer gold = 0;

    @Column(name = "starting_equipment_chosen", nullable = false)
    private Boolean startingEquipmentChosen = false;

    public abstract boolean canBeEditedBy(Long userId);
}