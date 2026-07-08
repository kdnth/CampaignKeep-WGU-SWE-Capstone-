package com.kdnth.campaignkeep.character;

import com.kdnth.campaignkeep.language.Language;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "character_languages")
@IdClass(CharacterLanguage.CharacterLanguageId.class)
@Getter
@Setter
@NoArgsConstructor
public class CharacterLanguage {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    private Character character;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CharacterLanguageId implements Serializable {
        private Long character;
        private Long language;

        public CharacterLanguageId(Long character, Long language) {
            this.character = character;
            this.language = language;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CharacterLanguageId that)) return false;
            return Objects.equals(character, that.character) && Objects.equals(language, that.language);
        }

        @Override
        public int hashCode() {
            return Objects.hash(character, language);
        }
    }
}