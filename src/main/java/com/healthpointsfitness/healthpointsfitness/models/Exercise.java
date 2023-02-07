package com.healthpointsfitness.healthpointsfitness.models;

import com.healthpointsfitness.healthpointsfitness.enumerations.ExerciseDifficulty;
import com.healthpointsfitness.healthpointsfitness.enumerations.ExerciseTypes;
import com.healthpointsfitness.healthpointsfitness.enumerations.Muscles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Exercise implements Serializable {
    private String name;
    private ExerciseTypes type;
    private Muscles muscle;

    private String equipment;

    private ExerciseDifficulty difficulty;

    private String instructions;

}
