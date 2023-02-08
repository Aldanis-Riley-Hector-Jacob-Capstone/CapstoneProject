package com.healthpointsfitness.healthpointsfitness;

import com.healthpointsfitness.healthpointsfitness.models.Exercise;
import com.healthpointsfitness.healthpointsfitness.services.ExercisesService;
import com.healthpointsfitness.healthpointsfitness.enumerations.Muscles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class HealthpointsfitnessApplicationTests {

	@Autowired
	private ExercisesService service;

	@Test
	void contextLoads() {
		System.out.println("Context is loading");
		System.out.println("Testing api calls");
		List<Exercise> exercises = service.getExercisesByMuscle(Muscles.calves);
		for(Exercise e : exercises){
//			System.out.println(e.getInstructions());
			System.out.println(e.getName());
		}
	}

}