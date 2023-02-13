package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.Challenge;
import com.healthpointsfitness.healthpointsfitness.models.Exercise;
import com.healthpointsfitness.healthpointsfitness.models.Path;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.ExerciseRepository;
import com.healthpointsfitness.healthpointsfitness.repositories.PathRepository;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import com.healthpointsfitness.healthpointsfitness.services.PathsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserPathViewController {
    @Autowired
    PathRepository pathRepo;
    @Autowired
    PathsService pathServ;
    @Autowired
    private UserRepository userDao;
    @Autowired
    private ExerciseRepository exRepo;

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

//    The form has at least one checkbox that have been clicked on
    @PostMapping(value = "/paths/update/{pathNumber}", params = {"exercises[]"})
    public String updatePath(@PathVariable Long pathNumber, @RequestParam("exercises[]") List<String> exercises){
        Path path = pathRepo.getReferenceById(pathNumber);
        curUser = getUser();
        List<Boolean> state = new ArrayList<Boolean>();

//        exercises.forEach(exercise -> System.out.println(exercise));
        int count = 0;
        for (Challenge challenge : path.getChallenges()){
            for (Exercise exercise : challenge.getExercises()){
                    exercise.setComplete(Boolean.valueOf(exercises.get(count)));
                    exRepo.save(exercise);
            }
            count++;
        }

        pathRepo.save(path);

        return "redirect:/paths/{pathNumber}";
    }
//    The form has no checkboxes clicked on -> springboot doesn't send anything at all (Form is 'null')
    @PostMapping(value = "/paths/update/{pathNumber}", params = {})
    public String updatePath(@PathVariable Long pathNumber){
        Path path = pathRepo.getReferenceById(pathNumber);
        curUser = getUser();
        List<Boolean> state = new ArrayList<Boolean>();

//        exercises.forEach(exercise -> System.out.println(exercise));
        for (Challenge challenge : path.getChallenges()){
            for (Exercise exercise : challenge.getExercises()){
                exercise.setComplete(false);
                exRepo.save(exercise);
            }
        }

        pathRepo.save(path);

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
