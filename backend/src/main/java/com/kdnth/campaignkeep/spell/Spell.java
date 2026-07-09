package com.kdnth.campaignkeep.spell;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "spells")
@Getter
@Setter
@NoArgsConstructor
public class Spell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_index", unique = true, length = 100)
    private String apiIndex;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Short level;

    @Column(name = "casting_time", length = 50)
    private String castingTime;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "range_type", nullable = false)
    private SpellRangeType rangeType;

    @Column(name = "range_feet")
    private Short rangeFeet;

    @Column(length = 10)
    private String components;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private MagicSchool school;

    @Column(nullable = false)
    private Boolean concentration = false;

    @Column(name = "has_verbal", nullable = false)
    private Boolean hasVerbal = false;

    @Column(name = "has_somatic", nullable = false)
    private Boolean hasSomatic = false;

    @Column(name = "has_material", nullable = false)
    private Boolean hasMaterial = false;

    @Column(name = "material_desc", columnDefinition = "TEXT")
    private String materialDesc;

    @Column(nullable = false)
    private Boolean ritual = false;

    @Column(length = 100)
    private String duration;

    @Column(name = "higher_level", columnDefinition = "TEXT")
    private String higherLevel;
}
