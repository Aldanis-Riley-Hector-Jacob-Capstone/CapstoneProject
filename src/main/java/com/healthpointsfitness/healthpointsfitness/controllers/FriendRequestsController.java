package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.FriendRequest;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.FriendRequestsRepository;
import com.healthpointsfitness.healthpointsfitness.services.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.ListUtils;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Controller
public class FriendRequestsController {
    @Autowired
    private UserService userService;

    @Autowired
    private FriendRequestsRepository friendRequestsRepository;

    String defaultProfileImage = "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";

    @GetMapping("user/friend_requests")
    public String friendRequestsGET(Model model){

//        //Get the logged in user
        User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        // Grab all the friend requests
//        List<FriendRequest> allRequest = friendRequestsRepository.findAll();
//
//        // Approved sent and received requests
//        List<FriendRequest> approvedRequests = allRequest.stream().filter(friendRequest -> {
//            // Check if the request was approved
//            Boolean approved = friendRequest.getRequestApproved();
//
//            // Get the user the request was sent to
//            User to = friendRequest.getTo();
//
//            if(to.getProfileImage() != null) {
//                // Set the profile image data url for the to user
//                byte[] encodeBase64 = Base64.getEncoder().encode(to.getProfileImage());
//                String base64Encoded = new String(encodeBase64, StandardCharsets.UTF_8);
//                to.setProfileImageDataUrl("data:image/jpeg;base64," + base64Encoded);
//            }else{
//                to.setProfileImageDataUrl(defaultProfileImage);
//            }
//
//            // Get the user the request was sent by
//            User from = friendRequest.getFrom();
//
//            if(from.getProfileImage() != null) {
//                // Set the profile image data url for the to user
//                byte[] encodeBase64_from = Base64.getEncoder().encode(from.getProfileImage());
//                String base64Encoded_from = new String(encodeBase64_from, StandardCharsets.UTF_8);
//                from.setProfileImageDataUrl("data:image/jpeg;base64," + base64Encoded_from);
//            }else{
//                from.setProfileImageDataUrl(defaultProfileImage);
//            }
//
//            // Any approved requests sent or received by me
//            return approved && ((Objects.equals(to.getId(), me.getId())) || (Objects.equals(from.getId(), me.getId())));
//        }).toList().stream().map(item->{
//            //Clear all sensitive data
//            item.getTo().setPassword(null);
//            item.getTo().setIsAdmin(null);
//            item.getTo().setEmail(null);
//
//            item.getFrom().setPassword(null);
//            item.getFrom().setIsAdmin(null);
//            item.getFrom().setEmail(null);
//
//            return item;
//        }).toList();
//
//        model.addAttribute("friends", approvedRequests);
//
//        // Denied Sent Requests
//        List<FriendRequest> sentDeniedRequests = allRequest.stream().filter(friendRequest -> {
//            // Check if the request was approved
//            Boolean approved = friendRequest.getRequestApproved();
//
//            // Get the user the request was sent by
//            User from = friendRequest.getFrom();
//
//            // Any denied requests sent by me
//            return !approved &&  (Objects.equals(from.getId(), me.getId()));
//        }).toList();
//
//        //Attach the sent denied requests to the model
//        model.addAttribute("sent_denied", sentDeniedRequests);
//
//        // Denied Received Requests
//        List<FriendRequest> receivedDeniedRequests = allRequest.stream().filter(friendRequest -> {
//            //Check if the request was approved
//            Boolean approved = friendRequest.getRequestApproved();
//
//            //Get the user the request was sent to
//            User to = friendRequest.getTo();
//
//            //Any denied requests sent by me
//            return !approved &&  (Objects.equals(to.getId(), me.getId()));
//        }).toList();
//
//        // Received denied request
//        model.addAttribute("received_denied", receivedDeniedRequests);
//
//        List<FriendRequest> pending = new ArrayList<>();
//        pending.addAll(receivedDeniedRequests);
//        pending.addAll(sentDeniedRequests);
//        model.addAttribute("pending",pending);
//
//        // Pass the currently logged-in user
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
