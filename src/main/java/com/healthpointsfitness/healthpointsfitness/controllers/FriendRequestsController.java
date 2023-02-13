package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.FriendRequest;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.FriendRequestsRepository;
import com.healthpointsfitness.healthpointsfitness.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Controller
public class FriendRequestsController {
    @Autowired
    private UserService userService;

    @Autowired
    private FriendRequestsRepository friendRequestsRepository;

    @GetMapping("user/friend_requests")
    public String friendRequestsGET(Model model){

        //Get the logged in user
        User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Grab all the friend requests
        List<FriendRequest> allRequest = friendRequestsRepository.findAll();

        // Approved sent and received requests
        List<FriendRequest> approvedRequests = allRequest.stream().filter(friendRequest -> {
            // Check if the request was approved
            Boolean approved = friendRequest.getRequestApproved();

            // Get the user the request was sent to
            User to = friendRequest.getTo();

            // Get the user the request was sent by
            User from = friendRequest.getFrom();

            // Any approved requests sent or received by me
            return approved && (Objects.equals(to.getId(), me.getId())) || (Objects.equals(from.getId(), me.getId()));
        }).toList();

        model.addAttribute("friends", approvedRequests);

        // Denied Sent Requests
        List<FriendRequest> sentDeniedRequests = allRequest.stream().filter(friendRequest -> {
            // Check if the request was approved
            Boolean approved = friendRequest.getRequestApproved();

            // Get the user the request was sent by
            User from = friendRequest.getFrom();

            // Any denied requests sent by me
            return !approved &&  (Objects.equals(from.getId(), me.getId()));
        }).toList();

        //Attach the sent denied requests to the model
        model.addAttribute("sent_denied", sentDeniedRequests);

        // Denied Received Requests
        List<FriendRequest> receivedDeniedRequests = allRequest.stream().filter(friendRequest -> {
            //Check if the request was approved
            Boolean approved = friendRequest.getRequestApproved();

            //Get the user the request was sent to
            User to = friendRequest.getTo();

            //Any denied requests sent by me
            return !approved &&  (Objects.equals(to.getId(), me.getId()));
        }).toList();

        // Received denied request
        model.addAttribute("received_denied", receivedDeniedRequests);

        // Pass the currently logged in user
        model.addAttribute("me",me);

        //Return the friendsSearch view
        return "users/friends";
    }

    @PostMapping("user/friend_request")
    public String friendRequestPost(@RequestParam("from") String from,@RequestParam("to") String to){

        try {
            //Get the users based on their usernames
            User userFrom = userService.findUserByUsername(from);
            User userTo = userService.findUserByUsername(to);

            //Calculate a timestamp.
            Timestamp ts = new Timestamp(System.currentTimeMillis());

            //Generate the new friend request
            FriendRequest fr = new FriendRequest(ts, userFrom, userTo);

            //Save the friend request to the db
            friendRequestsRepository.save(fr);

            //Send the user back to the friends search page
            return "users/friends";
        }catch(Exception e){
            //Return the stack trace on the terminal
            e.printStackTrace();

            //Send the user back to the friends search page
            return "users/friends";
        }
    }
}
