package com.kdnth.campaignkeep.race;

import com.kdnth.campaignkeep.ability.Ability;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "subrace_ability_point_bonuses")
@IdClass(SubraceAbilityPointBonus.SubraceAbilityPointBonusId.class)
@Getter
@Setter
@NoArgsConstructor
public class SubraceAbilityPointBonus {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subrace_id")
    private Subrace subrace;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ability_id")
    private Ability ability;

    @Column(nullable = false)
    private Short value;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SubraceAbilityPointBonusId implements Serializable {
        private Long subrace;
        private Long ability;

        public SubraceAbilityPointBonusId(Long subrace, Long ability) {
            this.subrace = subrace;
            this.ability = ability;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SubraceAbilityPointBonusId that)) return false;
            return Objects.equals(subrace, that.subrace) && Objects.equals(ability, that.ability);
        }

        @Override
        public int hashCode() {
            return Objects.hash(subrace, ability);
        }
    }
}
