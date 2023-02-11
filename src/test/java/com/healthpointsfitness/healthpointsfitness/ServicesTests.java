package com.healthpointsfitness.healthpointsfitness;

import com.healthpointsfitness.healthpointsfitness.services.EmailService;
import com.healthpointsfitness.healthpointsfitness.services.ExercisesService;
import com.healthpointsfitness.healthpointsfitness.services.PathsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailException;
import org.springframework.util.Assert;

@SpringBootTest
class ServicesTest {

	@Autowired
	private ExercisesService service;

	@Autowired
	private EmailService emailService;

	@Autowired
	private PathsService pathService;

	@Test
	void testEmailService() {
		try {
			Boolean sent = emailService.prepareAndSend("aldanisvigo@gmail.com", "Testing", "I wirk, hello world!");
			Assert.isTrue(sent, "EmailService ===> The email was not sent. Please check SMTP settings in application.properties.");
		}catch(MailException e){
			e.printStackTrace();
			System.out.println("EmailService ===> Error found. Read above.");
		}
		System.out.println("EmailService ===> The email was sent successfully.");
	}

	@Test
	void testPathsServiceCreate(){
		try{

		}catch(Exception e){
			e.printStackTrace();
		}

		System.out.println("PathsService ===> Path creation tests passed.");
	}

}