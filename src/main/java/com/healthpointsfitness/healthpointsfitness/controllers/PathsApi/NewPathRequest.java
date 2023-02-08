package com.healthpointsfitness.healthpointsfitness.controllers.PathsApi;

import com.healthpointsfitness.healthpointsfitness.models.Challenge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewPathRequest implements Serializable {
    String title;
    String description;
    Challenge[] challenges;

    int adminId;
}
