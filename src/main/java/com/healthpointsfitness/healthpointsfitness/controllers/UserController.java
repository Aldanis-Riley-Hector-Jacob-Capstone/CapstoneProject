package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.Path;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.PathRepository;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import com.healthpointsfitness.healthpointsfitness.services.PathsService;
import com.healthpointsfitness.healthpointsfitness.services.UserDetailsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/*
    Currently exists to allow basic view swapping with a navbar
 */



@Controller
public class UserController {
    @Autowired
    PathRepository pathRepo;
    @Autowired
    UserDetailsLoader users;
    @Autowired
    UserRepository userRepo;

    public List<Path> pathList;
    public long pointsTotal;

    @GetMapping("/profile")
    public String UserController_UniqueName_01(Model model){
        User activeUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Path> paths = pathRepo.findAll();

//        Dynamic Path icons / badges
        for (Path path : paths){
            byte[] encodeBase64 = Base64.getEncoder().encode(path.getImageBlob());
            String base64Encoded;
            try {
                base64Encoded = new String(encodeBase64, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            path.setImageDataUrl(base64Encoded);
        }

//        Gets total points
        pointsTotal = users.getUserPoints(1);



        model.addAttribute("paths", paths);
        model.addAttribute("points", pointsTotal);
        return "/users/index";
    }

    @RequestMapping("fsearch")
    public String UserController_UniqueName_02(){
        return "/users/friendsSearch";
    }
}
