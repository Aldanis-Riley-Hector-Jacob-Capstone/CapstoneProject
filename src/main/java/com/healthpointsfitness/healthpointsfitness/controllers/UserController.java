package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.Path;
import com.healthpointsfitness.healthpointsfitness.services.PathsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/*
    Currently exists to allow basic view swapping with a navbar
 */



@Controller
public class UserController {
    @Autowired
    PathsService repo;
    private ArrayList<Path> pathList;

    @RequestMapping("profile")
    public String UserController_UniqueName_01(){
        pathList = new ArrayList<Path>();
        for (Path path : repo.getPaths()){
            pathList.add(path);
        }
        return "/users/index";
    }

    @RequestMapping("fsearch")
    public String UserController_UniqueName_02(){
        return "/users/friendsSearch";
    }
}
