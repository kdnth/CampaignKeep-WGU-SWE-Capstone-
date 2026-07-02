package com.kdnth.campaignkeep.campaign;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
public class CampaignMemberId implements Serializable {
    private Long campaign;
    private Long user;

    public CampaignMemberId(Long campaign, Long user) {
        this.campaign = campaign;
        this.user = user;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CampaignMemberId that)) return false;
        return Objects.equals(campaign, that.campaign) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(campaign, user);
    }
}

