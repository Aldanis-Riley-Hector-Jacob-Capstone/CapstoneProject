package com.healthpointsfitness.healthpointsfitness.models;

import com.healthpointsfitness.healthpointsfitness.enumerations.ExerciseDifficulty;
import com.healthpointsfitness.healthpointsfitness.enumerations.ExerciseTypes;
import com.healthpointsfitness.healthpointsfitness.enumerations.Muscles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Exercise implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private ExerciseTypes type;
    private Muscles muscle;

    private String equipment;

    private ExerciseDifficulty difficulty;

    private String instructions;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @Transient
    private String challengeid;

    @Transient
    private Boolean completed = false;

    //Required by GSON
    public String toString() {
        return "{\"name\":\""
                + name
                + "\",\"type\":\""
                + type.name()
                + "\",\"equipment\":\""
                + equipment
                + "\",\"difficulty\":\""
                + difficulty
                + "\",\"instructions\":\""
                + instructions
                + "\"}";
    }

    public Exercise(Exercise copy){
        id = copy.id;
        name = copy.name;
        type = copy.type;
        muscle = copy.muscle;
        equipment = copy.equipment;
        difficulty = copy.difficulty;
        instructions = copy.instructions;
        challenge = copy.challenge;
        challengeid = copy.challengeid;
    }
}
