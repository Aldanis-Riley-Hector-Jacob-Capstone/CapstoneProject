package com.healthpointsfitness.healthpointsfitness.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Path {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title", nullable = false, length = 255, unique = true)
    private String title;

    @Column(name="description", nullable = false, length = 255, unique = false)
    private String description;

    @Lob
    @Column(name = "path_image", length = Integer.MAX_VALUE, nullable = true)
    private byte[] imageBlob;

    //Relationships
    //Path <> User(Admin)
    @ManyToOne
    private User admin;


    // Path <> Challenge
    @OneToMany(mappedBy = "path", cascade = CascadeType.ALL)
    private List<Challenge> challenges;

    @Transient
    String imageDataUrl;
}
