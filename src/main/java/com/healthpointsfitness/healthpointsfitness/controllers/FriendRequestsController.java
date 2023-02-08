package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.FriendRequest;
import com.healthpointsfitness.healthpointsfitness.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.Timestamp;

@Controller
public class FriendRequestsController {
    @GetMapping("/friend_requests")
    public String friendRequestsGET(){
        return "/users/friendsSearch";
    }

    @PostMapping("/friend_request")
    public String friendRequestPost(User userFrom, User userTo){
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        FriendRequest fr = new FriendRequest(ts, userFrom, userTo);
        return "/users/friendsSearch";
    }
}
