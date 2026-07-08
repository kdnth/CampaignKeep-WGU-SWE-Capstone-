package com.kdnth.campaignkeep.character;

import com.kdnth.campaignkeep.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("PC")
@Getter
@Setter
@NoArgsConstructor
public class PlayableCharacter extends Character {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private User player;

    @Override
    public boolean canBeEditedBy(Long userId) {
        return player.getId().equals(userId);
    }
}