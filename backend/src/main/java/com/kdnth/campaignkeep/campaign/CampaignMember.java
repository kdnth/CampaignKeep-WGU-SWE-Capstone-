package com.kdnth.campaignkeep.campaign;

import com.kdnth.campaignkeep.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name="campaign_members")
@IdClass(CampaignMemberId.class)
@Getter
@Setter
@NoArgsConstructor
public class CampaignMember {

    @Id
    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "role", columnDefinition = "campaign_role", nullable = false)
    private CampaignRole role;
}
