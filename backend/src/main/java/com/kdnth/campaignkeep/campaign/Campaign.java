package com.kdnth.campaignkeep.campaign;

import com.kdnth.campaignkeep.base.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="campaigns")
@Getter
@Setter
@NoArgsConstructor
public class Campaign extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 75)
    private String title;

    @Column(nullable = false, length = 3000)
    private String description;

    @Column(name="finished_on")
    private LocalDateTime finishedOn;
}
