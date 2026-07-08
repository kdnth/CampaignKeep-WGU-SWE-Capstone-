package com.kdnth.campaignkeep.campaign;

import com.kdnth.campaignkeep.character.Character;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "campaign_nonplayable_characters")
@IdClass(CampaignNonplayableCharacter.CampaignNonplayableCharacterId.class)
@Getter
@Setter
@NoArgsConstructor
public class CampaignNonplayableCharacter {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    private Character character;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CampaignNonplayableCharacterId implements Serializable {
        private Long campaign;
        private Long character;

        public CampaignNonplayableCharacterId(Long campaign, Long character) {
            this.campaign = campaign;
            this.character = character;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CampaignNonplayableCharacterId that)) return false;
            return Objects.equals(campaign, that.campaign) && Objects.equals(character, that.character);
        }

        @Override
        public int hashCode() {
            return Objects.hash(campaign, character);
        }
    }
}