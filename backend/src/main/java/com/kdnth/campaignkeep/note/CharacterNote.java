package com.kdnth.campaignkeep.note;

import com.kdnth.campaignkeep.character.Character;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "character_notes")
@Getter
@Setter
@NoArgsConstructor
public class CharacterNote {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "character_id")
    private Character character;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "note_id", unique = true)
    private Note note;
}
