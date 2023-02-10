package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.FriendRequest;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import com.healthpointsfitness.healthpointsfitness.services.FriendRequestService;
import com.healthpointsfitness.healthpointsfitness.services.UserDetailsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.util.List;

@Controller
public class FriendRequestsController {
    @Autowired
    private FriendRequestService friendRequestService;
    private UserRepository userRepository;
    @GetMapping("/friend_requests")
    public String friendRequestsGET(){
        return "/users/friendsSearch";
    }

   @GetMapping(path = "/friends_list")
            public String viewUsers(Model model) {
        List<User> users = friendRequestService.getAllUsers();
        model.addAttribute("allUsers", users);
        model.addAttribute("test", "test");
       return "/users/friendsSearch";
    }
    //working code for searching for name
    @GetMapping("/friends_list/searched")
    public String listSearch(Model model, @RequestParam String name) {
        List<User> allusers = userRepository.findAll();
        if (name != null) {
            User searchedUser = userRepository.findUserByUsername(name);
            model.addAttribute("searchedUser", searchedUser);
        } else {
            model.addAttribute("allUsers", allusers);
        }
        return "/users/friendsSearch";
    }
    /*@GetMapping("/friends_list")
    public String searchFriends(Model model, @RequestParam(required = false) String name) {
        List<User> allUsers = userRepository.findAll();
        if (name != null) {
            User searchedUser = userRepository.findUserByUsername(name);
            model.addAttribute("searchedUser", searchedUser);
        } else {
            model.addAttribute("allUsers", allUsers);
        }
        return "/users/friendsSearch";
    }*/


    @PostMapping("/friends_list")
    public String searchFriends(Model model, @RequestParam String name) {
        User searchedUser = userRepository.findUserByUsername(name);
        if (searchedUser == null) {
            List<User> users = friendRequestService.getAllUsers();
            model.addAttribute("allUsers", users);
        } else {
            model.addAttribute("searchedUser", searchedUser);
        }
        return "/users/friendsSearch";
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

List all users in "Main container" of friendsSearch view. done

Update said list based on paramaters of the form that gets submitted.
            Search by name(user database)
            Search by location(user database)
            search by point range(challenge database)
            search by paths completed check(goals database
            search if mentor or not(not yet implemented)

List already added friends in User list at the bottom of the page

 */