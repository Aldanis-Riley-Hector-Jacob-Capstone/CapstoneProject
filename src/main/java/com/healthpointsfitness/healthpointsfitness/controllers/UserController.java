package com.healthpointsfitness.healthpointsfitness.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/*
    Currently exists to allow basic view swapping with a navbar
 */

@Controller
public class UserController {
    @RequestMapping("profile")
    public String uc01(){
        return "/users/index";
    }
    @RequestMapping("fsearch")
    public String uc02(){
        return "/users/friendsSearch";
    }
}
