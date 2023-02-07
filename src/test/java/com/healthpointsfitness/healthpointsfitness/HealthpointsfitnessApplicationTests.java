package com.healthpointsfitness.healthpointsfitness;

import com.healthpointsfitness.healthpointsfitness.services.ExercisesService;
import com.healthpointsfitness.healthpointsfitness.enumerations.Muscles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HealthpointsfitnessApplicationTests {

	@Autowired
	private ExercisesService service;

	@Test
	void contextLoads() {
		System.out.println("Context is loading");
		System.out.println("Testing api calls");
		service.getExercisesByMuscle(Muscles.biceps);
	}

}