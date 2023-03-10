package com.healthpointsfitness.healthpointsfitness.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Challenge implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title", nullable = false, length = 255)
    private String title;

    @Column(name="description", nullable = false, length = 255)
    private String description;

    @Column(name="points", nullable = false)
    private Integer points = 5;

    @Column(name="icon", nullable = true, length = 255)
    private String icon;

    //Relationships
    @ManyToOne
    @JoinColumn(name = "path_id")
    private Path path;

    @OneToMany(mappedBy = "challenge",cascade = {CascadeType.MERGE, CascadeType.ALL})
    List<Exercise> exercises;

    @Transient
    Integer challengeNumber;

    //GSON Required function for serialization
    public String toString() {
        return "{\"title\":\""
                + title
                + "\"id\":\""
                + id
                + "\",\"instructions\":\""
                + description
                + "\",\"points\":\""
                + points
                + "\",\"icon\":\""
                + icon
                + "\"}";
    }


    public Challenge(Challenge copy) {
        id = copy.id;
        title = copy.title;
        description = copy.description;
        points = copy.points;
        icon = copy.icon;
        path = copy.path;
        exercises = copy.exercises;
    }
}
