package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.FriendRequest;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.Timestamp;
import java.time.Instant;


@Controller
public class FriendRequestController {
    @Autowired
    private UserRepository userDao;

    @GetMapping("/friend_request")
    public String redirect(){
        return "/users/request";
    }

    @PostMapping("/friend_request")
    public String handleFriendRequest(User fromUser, User toUser){
        Timestamp ts = Timestamp.from(Instant.now());
        FriendRequest fr = new FriendRequest(ts, fromUser, toUser);
        // TODO: Commit the FriendRequest to the database
        return ("redirect:/users/request");
    }

}