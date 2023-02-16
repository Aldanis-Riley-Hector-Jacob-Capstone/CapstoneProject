package com.healthpointsfitness.healthpointsfitness.services;

import com.healthpointsfitness.healthpointsfitness.models.Path;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository repository;
    @Autowired
    PathsService pathServ;

    public boolean userLoggedIn(){
        return getUserCurrentlyLoggedIn() != null;
    }

    public User getUserCurrentlyLoggedIn(){
        try {
            return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public User findUserByUsername(String username){
        try{
            User user = repository.findUserByUsername(username);
            return user;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void updateProfile(User user) {
        try {
            //Update the user in the database
            repository.save(user);
        } catch (Exception e){ //Catch any exceptions
            //Print the stack trace to the terminal
            e.printStackTrace();
        }
    }

    public void updateUserPoints(User user,  List<Path> completedPaths){
        Long totalPoints = 0L;
        for (Path path  :  completedPaths){
            totalPoints += pathServ.getTotalPathPoints(path);
        }
        user.setTotalPoints(totalPoints);
    }

    public Long getUserPoints(User user){
        return user.getTotalPoints();
    }
}
