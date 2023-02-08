package com.healthpointsfitness.healthpointsfitness.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title", nullable = false, length = 255)
    private String title;

    @Column(name="instructions", nullable = false, length = 255)
    private String instructions;

    @Column(name="points", nullable = false)
    private Integer points;

    @Column(name="icon", nullable = true, length = 255)
    private String icon;

    //Relationships
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "path_id")
    private Path path;
}
