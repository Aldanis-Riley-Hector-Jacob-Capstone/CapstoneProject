package com.healthpointsfitness.healthpointsfitness.repositories;

import com.healthpointsfitness.healthpointsfitness.models.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise,Long> {

}
