package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.FriendRequest;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.services.UserDetailsLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.Timestamp;
import java.util.List;

@Controller
public class FriendRequestsController {
    @GetMapping("/friend_requests")
    public String friendRequestsGET(){
        return "/users/friendsSearch";
    }
    @GetMapping(path ="friends_list") {
        List<User> user = UserDetailsLoader.loadByUsername();
    }

    @PostMapping("/friend_request")
    public String friendRequestPost(User userFrom, User userTo){
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        FriendRequest fr = new FriendRequest(ts, userFrom, userTo);
        return "/users/friendsSearch";
    }
}
//Goal is to list users to add, allow logged in user to add.
/* Add users get sent to user_friend_requests database
friends already added are in user_friends database.

List all users in "Main container" of friendsSearch view.
Update said list based on paramaters of the form that gets submitted.

List already added friends in User list at the bottom of the page

 */