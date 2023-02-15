package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.Goal;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.GoalRepository;
import com.healthpointsfitness.healthpointsfitness.services.GoalsService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/goals/api/v1")
public class GoalsApi {

    @Autowired
    GoalsService service;

    @Autowired
    GoalRepository repository;

    @Getter
    @Setter
    @AllArgsConstructor
    private static class GoalStatusUpdate{
        Boolean complete;
        Long goalId;
    }


    @Getter
    @Setter
    @AllArgsConstructor
    private static class GoalsApiResponse {
        Boolean error;
        String message;
    }
    @RequestMapping(
            name = "updateGoalStatus",
            path = "/updateGoalStatus",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST
    )
    private GoalsApiResponse updateGoalStatus(@RequestBody GoalStatusUpdate update){
        try {
            Optional<Goal> goalOpt = repository.findById(update.goalId);
            if (goalOpt.isPresent()) {
                Goal goal = goalOpt.get();
                goal.setCompleted(update.complete);
                repository.save(goal);
            }

            return new GoalsApiResponse(false,"Goal status updated.");
        }catch(Exception e){
            e.printStackTrace();
        }

        return new GoalsApiResponse(true,"Error updating goal status.");
    }

    @RequestMapping(
            name = "addGoal",
            path = "/addGoal",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST
    )
    private GoalsApiResponse addGoal(@RequestBody Goal goal){
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            goal.setUser(user);
            repository.save(goal);
            return new GoalsApiResponse(false, "Goal saved.");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new GoalsApiResponse(true,"Error saving goal.");
    }

    @RequestMapping(
            name = "deleteGoal",
            path = "/deleteGoal",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST
    )
    private GoalsApiResponse deleteGoal(@RequestBody Long id){
        try {
            Optional<Goal> goal = repository.findById(id);
            if(goal.isPresent()) {

                Goal g = goal.get();
                g.setUser(null);
                repository.delete(g);
                return new GoalsApiResponse(false, "Goal deleted.");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new GoalsApiResponse(true,"Error saving goal.");
    }

    @RequestMapping(
            name = "getGoals",
            path = "/getGoals",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST
    )
    private List<Goal> getGoals(){
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return service.getGoalsForUser(user);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}
