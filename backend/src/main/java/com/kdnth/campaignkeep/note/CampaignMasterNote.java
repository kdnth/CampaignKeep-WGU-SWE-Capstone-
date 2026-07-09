package com.kdnth.campaignkeep.note;

import com.kdnth.campaignkeep.campaign.Campaign;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "campaign_master_notes")
@IdClass(CampaignMasterNote.CampaignMasterNoteId.class)
@Getter
@Setter
@NoArgsConstructor
public class CampaignMasterNote {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    private Note note;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CampaignMasterNoteId implements Serializable {
        private Long campaign;
        private Long note;

        public CampaignMasterNoteId(Long campaign, Long note) {
            this.campaign = campaign;
            this.note = note;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CampaignMasterNoteId that)) {
                return false;
            }
            return Objects.equals(campaign, that.campaign) && Objects.equals(note, that.note);
        }

        @Override
        public int hashCode() {
            return Objects.hash(campaign, note);
        }
    }
}
