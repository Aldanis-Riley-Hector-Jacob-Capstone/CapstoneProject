package com.healthpointsfitness.healthpointsfitness.services;

import com.healthpointsfitness.healthpointsfitness.models.Goal;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalsService {
    @Autowired
    GoalRepository goalRepository;

    public List<Goal> getGoalsForUser(User user){
        try {
            return goalRepository.getGoalsByUserId(user.getId());
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Boolean saveGoal(Goal goal){
        try{
            goalRepository.save(goal);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Boolean deleteGoal(Long goalId) {
        try {
            goalRepository.deleteById(goalId);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
