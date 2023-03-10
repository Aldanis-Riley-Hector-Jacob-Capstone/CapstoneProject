package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.Goal;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.GoalRepository;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import com.healthpointsfitness.healthpointsfitness.services.GoalsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class GoalsController {
    @Autowired
    GoalsService service;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private GoalRepository goalRepository;

    @GetMapping("user/goals")
    private String getGoalsView(Model model){
        try {
            User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<Goal> myGoals = service.getGoalsForUser(loggedInUser);
            myGoals.forEach(goal->{
                if(goal.isCompleted()){
                    goal.setStatus("COMPLETE");
                }else{
                    goal.setStatus("PENDING");
                }
            });
            myGoals.forEach(goal->goal.setUser(null));
            model.addAttribute("goals",myGoals);
            model.addAttribute("goal", new Goal());
            return "users/goals";
        }catch(Exception e){
            e.printStackTrace();
        }

        return "users/goals";
    }


    @PostMapping("/users/goals/add")
    private String postGoals(@ModelAttribute("goal") Goal goal, @RequestParam(name = "goalCompleted", required = false) Boolean goalCompleted) {
        try{ //Try to

//            goalCompleted.stream().forEach(System.out::println);
            if(goalCompleted != null) {
                System.out.println(goalCompleted);
                goal.setCompleted(goalCompleted);
            }

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
        return "redirect:/user/goals";
    }

    @PostMapping ("/users/goals/delete/{id}")
    private String deleteGoal(@PathVariable("id") Long goalId) {
        try {
            service.deleteGoal(goalId);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "redirect:/user/goals";
    }

    @PostMapping("/users/goals/update")
    private String updateGoal(@ModelAttribute("goal") Goal goal, @RequestParam(name = "goalCompleted",required = false) Boolean[] completed) {
        try {
            System.out.println("Goal Completed?");
            if(completed != null){
                goal.setCompleted(true);
            }else{
                goal.setCompleted(false);
            }
            User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            me = userRepository.findUserById(me.getId());
            goal.setUser(me);
            goalRepository.save(goal);
            List<Goal> newGoals = me.getGoals();
            newGoals.add(goal);
            me.setGoals(newGoals);
            userRepository.save(me);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "redirect:/user/goals";
    }
}
