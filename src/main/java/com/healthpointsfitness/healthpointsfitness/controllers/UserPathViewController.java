package com.healthpointsfitness.healthpointsfitness.controllers;

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

import java.util.*;

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

    /*
     * Retrieve a path in the path view based on the path number
     */
    @GetMapping("/paths/{id}")
    public String getPath(@PathVariable("id") Long pathid, Model model){
        System.out.println(pathid);
        try {
            if (pathid != null) {

                //Get the path using the path id
                Path myPath = pathRepo.getReferenceById(pathid);

                //Set the image data url
                myPath.setImageDataUrl(pathServ.getPathImage(myPath));

                //Get the user
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                User finalUser = userDao.findUserByUsername(user.getUsername());

                //Create temp completed exercises array
                List<Exercise> completedExercises = new ArrayList<>();

                //Iterate through all challenges
                myPath.getChallenges().forEach(challenge -> {
                    //And then all exercises in each challenge
                    challenge.getExercises().forEach(exercise -> {
                        //Grab any exercises that the user has completed
                        List<Exercise> matches = finalUser.getCompletedExercises();

                        //Mark matches completed
                        matches.forEach(match-> match.setCompleted(true));

                        //And add them to the completed exercises array
                        completedExercises.addAll(matches);
                    });
                });

//                //Convert list of completed excersises into a map to remove duplicates
//                Map<Long,Exercise> exerciseMap = new HashMap<>();
//                completedExercises.forEach(ex->exerciseMap.put(com));

                System.out.println("You have completed " + completedExercises.size() + " exercises.");

                //Add all the model attributes for the front end
                model.addAttribute("me", finalUser);
                model.addAttribute("enrolled", completedExercises);
                model.addAttribute("path", myPath);
                model.addAttribute("points", pathServ.getTotalPathPoints(myPath));
            } else {
                System.out.println("Path id was null.");
                return "landing";
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return "users/path";
    }

    @PostMapping("/paths/enroll/{pathNumber}")
    public String enrollInPath(@PathVariable Long pathNumber) {
        Path path = pathRepo.getReferenceById(pathNumber);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User curUser = userDao.findUserByUsername(user.getUsername());
        if (curUser.getFollowed_paths().contains(path)) {
            curUser.getFollowed_paths().remove(path);
        } else {
            curUser.getFollowed_paths().add(path);
        }
        userDao.save(curUser);

        return "redirect:/profile/" + curUser.getUsername();
    }

    @GetMapping("/allPaths")
    public String allPaths(Model model){
        try {
            //Get a list of all available paths
            List<Path> paths = pathRepo.findAll();

            //Get the user
            User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User finalMe = userDao.findUserById(me.getId());

            //Set the path images for the user followed paths
            finalMe.getFollowed_paths().forEach(path->{
                path.setImageDataUrl(pathServ.getPathImage(path));
            });

            //Set the path images for all the other paths
            paths.forEach(path->path.setImageDataUrl(pathServ.getPathImage(path)));

            //Add the paths and the user to the model
            model.addAttribute("paths", paths);
            model.addAttribute("me",finalMe);

            return "users/paths";
        }catch (Exception e){
            e.printStackTrace();
        }

        return "landing";
    }
}