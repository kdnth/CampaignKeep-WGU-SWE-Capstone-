package com.kdnth.campaignkeep.race;

import com.kdnth.campaignkeep.ability.Ability;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "race_ability_point_bonuses")
@IdClass(RaceAbilityPointBonus.RaceAbilityPointBonusId.class)
@Getter
@Setter
@NoArgsConstructor
public class RaceAbilityPointBonus {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id")
    private Race race;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ability_id")
    private Ability ability;

    @Column(nullable = false)
    private Short value;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RaceAbilityPointBonusId implements Serializable {
        private Long race;
        private Long ability;

        public RaceAbilityPointBonusId(Long race, Long ability) {
            this.race = race;
            this.ability = ability;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RaceAbilityPointBonusId that)) return false;
            return Objects.equals(race, that.race) && Objects.equals(ability, that.ability);
        }

        @Override
        public int hashCode() {
            return Objects.hash(race, ability);
        }
    }
}