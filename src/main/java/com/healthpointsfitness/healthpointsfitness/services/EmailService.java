package com.healthpointsfitness.healthpointsfitness.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("mailService")
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.from}")
    private String from;

    public Boolean prepareAndSend(String to,String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);

        try{
            this.emailSender.send(msg);
            return true;
        }
        catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
            return false;
        }
    }
}