package com.healthpointsfitness.healthpointsfitness.services;

import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository repository;

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
}
