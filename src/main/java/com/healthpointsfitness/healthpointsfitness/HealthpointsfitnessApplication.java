package com.healthpointsfitness.healthpointsfitness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
public class HealthpointsfitnessApplication {
	public static void main(String[] args) {
		SpringApplication.run(HealthpointsfitnessApplication.class, args);
	}

}
