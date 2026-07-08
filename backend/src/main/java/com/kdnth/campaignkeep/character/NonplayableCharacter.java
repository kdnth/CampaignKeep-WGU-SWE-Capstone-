package com.kdnth.campaignkeep.character;

import com.kdnth.campaignkeep.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("NPC")
@Getter
@Setter
@NoArgsConstructor
public class NonplayableCharacter extends Character {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @Override
    public boolean canBeEditedBy(Long userId) {
        return createdBy.getId().equals(userId);
    }
}