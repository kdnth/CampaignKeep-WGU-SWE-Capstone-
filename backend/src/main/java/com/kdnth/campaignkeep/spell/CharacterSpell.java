package com.kdnth.campaignkeep.spell;

import com.kdnth.campaignkeep.character.Character;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "character_spells")
@IdClass(CharacterSpell.CharacterSpellId.class)
@Getter
@Setter
@NoArgsConstructor
public class CharacterSpell {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    private Character character;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spell_id")
    private Spell spell;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CharacterSpellId implements Serializable {
        private Long character;
        private Long spell;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CharacterSpellId that)) return false;
            return Objects.equals(character, that.character) && Objects.equals(spell, that.spell);
        }

        @Override
        public int hashCode() {
            return Objects.hash(character, spell);
        }
    }
}
