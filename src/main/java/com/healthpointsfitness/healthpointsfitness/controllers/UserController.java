package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.repositories.PathRepository;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import com.healthpointsfitness.healthpointsfitness.services.UserDetailsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/*
    Currently exists to allow basic view swapping with a navbar
 */



@Controller
public class UserController {
    @Autowired
    PathRepository pathRepo;
    @Autowired
    UserRepository userRepo;
    @Autowired
    private UserDetailsLoader userDetailsLoader;

    @GetMapping("/profile")
    public String UserController_UniqueName_01(Model model){
        model = userDetailsLoader.getUserData(model);
        return "/users/index";
    }

    @RequestMapping("/fsearch")
    public String UserController_UniqueName_02(){
        return "/users/friendsSearch";
    }
}
