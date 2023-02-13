package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.Goal;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.services.GoalsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GoalsController {
    @Autowired
    GoalsService service;

    @GetMapping("user/goals")
    private String getGoalsView(Model model){
        List<Goal> myGoals = new ArrayList<>();
        try {
            User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            myGoals = service.getGoalsForUser(loggedInUser);
            model.addAttribute("goals",myGoals);

            return "users/goals";
        }catch(Exception e){
            e.printStackTrace();
        }

        return "users/goals";
    }



    @PostMapping("user/saveGoal")
    private String postGoals(@ModelAttribute("goal") Goal goal) {
        try{ //Try to
            //Grab the logged in user
            User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            //Set the user for the goal before saving to db
            goal.setUser(loggedInUser);

            //Save the goal to the database
            service.saveGoal(goal);
        }catch(Exception e){ //If there's an error, print it out
            e.printStackTrace();
        }

        //Redirect back to the goals page
        return "redirect:users/goals";
    }
}
