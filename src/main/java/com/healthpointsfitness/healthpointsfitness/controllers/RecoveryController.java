package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.RecoveryRequest;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.RecoveryRequestRepository;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import com.healthpointsfitness.healthpointsfitness.services.EmailService;
import com.healthpointsfitness.healthpointsfitness.services.SecurityCodeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.util.UriEncoder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class RecoveryController {
    @Autowired
    EmailService emailService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityCodeService securityCodeService;

    @Autowired
    RecoveryRequestRepository recoveryRequestRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("recover")
    private String getRecoveryView(){
        return "recover";
    }

    @PostMapping("recover")
    private String postRecovery(@RequestParam("email") String email, Model model){
        try { // Try to recover the account
            User user = userRepository.findUserByEmail(email);

            //If the email is not null
            if (user != null) {
                // Generate a password recovery code
                String code = securityCodeService.generateSecurityCode(8);

                // Save the recovery request to the database for the next step
                RecoveryRequest newRecoveryRequest = new RecoveryRequest();
                newRecoveryRequest.setCode(code);
                newRecoveryRequest.setUser(user);
                recoveryRequestRepository.save(newRecoveryRequest);

                // Send the code to the users email
                emailService.prepareAndSend(email,"Here's your recovery code.","Your recovery code is: " + code + " please enter it in the page you were at.");

                // Send back to the recovery page
                return "redirect:recover?sent=true&email=" + URLEncoder.encode(email, StandardCharsets.UTF_8);
            }else{
                // Return to the recover
                return "redirect:recover?error=no_account_found";
            }
        }catch(Exception e){ // If there's any exception

            // Print the stack trace
            e.printStackTrace();

            // Redirect to the recover
            return "redirect:recover?error=error_try_again";
        }
    }

    @GetMapping("changePassword")
    private String changePasswordView(){
        return "changePassword";
    }

    @PostMapping("changePass")
    private String changePass(
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            @RequestParam("code") String code)
    {
        try {
            //Get the user by email
            User user = userRepository.findUserByEmail(email);

            //Set the new password
            user.setPassword(passwordEncoder.encode(password));

            //Save the user back the database duuuuh!
            userRepository.save(user);

            //Delete the recovery request for the given code just to keep things clean
            recoveryRequestRepository.delete(recoveryRequestRepository.findRecoveryRequestByCode(code));

            //Get the user by username
            return "redirect:login";
        }catch(Exception e){ //If there's an exception

            //Print the stack trace to the terminal
            e.printStackTrace();
        }

        //Otheriwse show an error page
        return "redirect:login";
    }
}
