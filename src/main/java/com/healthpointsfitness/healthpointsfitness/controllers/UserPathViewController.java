package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.Challenge;
import com.healthpointsfitness.healthpointsfitness.models.Exercise;
import com.healthpointsfitness.healthpointsfitness.models.Path;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.PathRepository;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import com.healthpointsfitness.healthpointsfitness.services.PathsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class UserPathViewController {
    @Autowired
    PathRepository pathRepo;
    @Autowired
    PathsService pathServ;
    @Autowired
    private UserRepository userDao;
    private Authentication auth;
    private User curUser;

//    Moved to function to use multiple times
    private User getUser(){
        auth = SecurityContextHolder.getContext().getAuthentication();
        curUser = userDao.findUserByUsername(auth.getName());

        return curUser;
    }

    @GetMapping("/paths/{pathnumber}")
    public String paths02(@PathVariable Long pathnumber, Model model){
        try {
//            IF THE PATH EXISTS
            if (pathnumber != null) {
                Path myPath = pathRepo.getReferenceById(pathnumber);
                myPath.setImageDataUrl(pathServ.getPathImage(myPath));
                curUser = getUser();
                model.addAttribute("enrolled", pathServ.isEnrolled(curUser, myPath));
                model.addAttribute("path", myPath);
                model.addAttribute("points", pathServ.getTotalPathPoints(myPath));
//                IF ENROLLED IN PATH
            } else {
                return "/users/index";
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return "/users/viewPath";
    }

    @PostMapping("/paths/enroll/{pathNumber}")
//    TODO: Prevent user from repreatedly enrolling in the same path forever (JJ)
    public String enrollInPath(@PathVariable Long pathNumber) {
        Path path = pathRepo.getReferenceById(pathNumber);
        curUser = getUser();

//        USER IS ENROLLED
        if (pathServ.isEnrolled(curUser, path)) {
            List<Path> followedPaths = curUser.getFollowed_paths();
            followedPaths.remove(path);
            curUser.setFollowed_paths(followedPaths);
//            TODO: CONFIRM USER WANTS TO DELETE PATH (AND PROGRESS) (JJ)
//        USER IS NOT ENROLLED
        } else {
            List<Path> followedPaths = curUser.getFollowed_paths();
            followedPaths.add(path);
            curUser.setFollowed_paths(followedPaths);
        }
        userDao.save(curUser);
        return "redirect:/profile";
    }

    @PostMapping("/paths/update/{pathNumber}")
    public String updatePath(@PathVariable Long pathNumber, Model model){
        Path path = pathRepo.getReferenceById(pathNumber);
        curUser = getUser();

        for (Challenge challenge : path.getChallenges()){
            for (Exercise exercise : challenge.getExercises()){
                System.out.println(model.getAttribute(exercise.getName()));
            }
        }

        return "redirect:/paths/{pathNumber}";
    }

    @GetMapping("/allPaths")
    public String allPaths(Model model){
        try {
            List<Path> paths = pathRepo.findAll();

            // Iterate through all path objects and assign image
            for (Path path : paths){
                path.setImageDataUrl(pathServ.getPathImage(path));
            }

            model.addAttribute("paths", paths);

            return "/users/allPaths";
        }catch (Exception e){
            e.printStackTrace();
        }

        return "/profile";
    }
}
