package com.kdnth.campaignkeep.item;

import com.kdnth.campaignkeep.campaign.Campaign;
import com.kdnth.campaignkeep.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "items")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "item_type")
@Getter
@Setter
@NoArgsConstructor
public abstract class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "item_type", nullable = false, insertable = false, updatable = false)
    private ItemType itemType;

    @Column(length = 75)
    private String name;

    @Column(length = 500)
    private String description;

    private Integer value;

    private Short weight;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private ItemRarity rarity = ItemRarity.common;

    @Column(name = "is_magical", nullable = false)
    private Boolean isMagical = false;

    @Column(name = "api_index", length = 100)
    private String apiIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    public int getEffectiveValue() {
        int base = value != null ? value : 0;
        ItemRarity r = rarity != null ? rarity : ItemRarity.common;
        return base * r.costMultiplier();
    }

    public abstract String getSummary();
}
