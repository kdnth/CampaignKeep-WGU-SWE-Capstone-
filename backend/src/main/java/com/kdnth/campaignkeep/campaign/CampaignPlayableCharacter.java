package com.kdnth.campaignkeep.campaign;

import com.kdnth.campaignkeep.character.Character;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "campaign_playable_characters")
@IdClass(CampaignPlayableCharacter.CampaignPlayableCharacterId.class)
@Getter
@Setter
@NoArgsConstructor
public class CampaignPlayableCharacter {

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
    public static class CampaignPlayableCharacterId implements Serializable {
        private Long campaign;
        private Long character;

        public CampaignPlayableCharacterId(Long campaign, Long character) {
            this.campaign = campaign;
            this.character = character;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CampaignPlayableCharacterId that)) return false;
            return Objects.equals(campaign, that.campaign) && Objects.equals(character, that.character);
        }

        @Override
        public int hashCode() {
            return Objects.hash(campaign, character);
        }
    }
}