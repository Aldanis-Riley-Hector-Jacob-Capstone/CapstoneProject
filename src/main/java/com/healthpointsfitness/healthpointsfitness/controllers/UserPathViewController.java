package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.Path;
import com.healthpointsfitness.healthpointsfitness.repositories.PathRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserPathViewController {
    @Autowired
    PathRepository pathRepo;

    @GetMapping("/paths/{pathnumber}")
    public String paths02(@PathVariable Long pathnumber, Model model){
        try {
            if (pathnumber != null) {
                Path myPath = pathRepo.getReferenceById(pathnumber);
                model.addAttribute("path", myPath);
            } else {
                return "/profile";
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return "/users/viewPath";
    }
}
