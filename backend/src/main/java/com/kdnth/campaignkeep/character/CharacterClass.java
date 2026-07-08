package com.kdnth.campaignkeep.character;

import com.kdnth.campaignkeep.dndclass.DndClass;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "character_classes")
@IdClass(CharacterClass.CharacterClassId.class)
@Getter
@Setter
@NoArgsConstructor
public class CharacterClass {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    private Character character;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private DndClass dndClass;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CharacterClassId implements Serializable {
        private Long character;
        private Long dndClass;

        public CharacterClassId(Long character, Long dndClass) {
            this.character = character;
            this.dndClass = dndClass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CharacterClassId that)) return false;
            return Objects.equals(character, that.character) && Objects.equals(dndClass, that.dndClass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(character, dndClass);
        }
    }
}