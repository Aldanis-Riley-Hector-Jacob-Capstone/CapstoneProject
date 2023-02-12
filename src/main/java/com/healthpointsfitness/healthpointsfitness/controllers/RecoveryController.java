package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import com.healthpointsfitness.healthpointsfitness.services.EmailService;
import com.healthpointsfitness.healthpointsfitness.services.SecurityCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RecoveryController {
    @Autowired
    EmailService emailService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityCodeService securityCodeService;

    @GetMapping("/recover")
    private String getRecoveryView(){
        return "/recover";
    }

    @PostMapping("/recover")
    private String postRecovery(@RequestParam("email") String email){
        try {
            if (userRepository.findUserByEmail(email) != null) {
                //Generate a password recovery code
                String code = securityCodeService.generateSecurityCode(8);

                return "redirect:/recover";
            }else{
                return "redirect:/recover?error=no_account_found";
            }
        }catch(Exception e){
            e.printStackTrace();
            return "redirect:/recover?error=error_try_again";
        }
    }
}
