package com.healthpointsfitness.healthpointsfitness.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "completed", nullable = false, unique = false)
    private Boolean completed;

    @Column(name = "description", nullable = false, unique = false)
    private String description;

    //Relationships
    @ManyToOne
    private User user;
}