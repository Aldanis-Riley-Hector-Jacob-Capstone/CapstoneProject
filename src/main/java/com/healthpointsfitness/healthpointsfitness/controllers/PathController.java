package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.Path;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.models.UserWithRoles;
import com.healthpointsfitness.healthpointsfitness.repositories.PathRepository;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
public class PathController {
    @Autowired
    private PathRepository pathRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin/path/create")
    private String createPathGetRequest(Model model){
        model.addAttribute("newPath",new Path());
        System.out.println("Visiting path create now.");
        return "/admin/path/create";
    }

    @PostMapping("/admin/path/create")
    private String createPathPostRequest(@ModelAttribute("newPath") Path path,
                                  @RequestParam("pathImage") MultipartFile pathImage){
        try{
            if(!pathImage.isEmpty()){ //If there's a new image, set it to the new one
                //Grab the bytes from the input stream of the path image
                byte[] pathImageBytes = pathImage.getInputStream().readAllBytes();

                //Set them in the path object instance
                path.setImageBlob(pathImageBytes);
            }else{ //Set it to whatever was already there
                //Grab the current path by id from the path repository
                Optional<Path> currentPath = pathRepository.findById(path.getId());

                //Since it's an optional, let's check if it's present before attempting to get the blob
                if(currentPath.isPresent()){
                    //Grab the blob if it's present
                    byte[] img = currentPath.get().getImageBlob();

                    //And set it in the incoming path
                    path.setImageBlob(img);
                }
            }

            //Grab the logged in principal
            UserWithRoles principal = (UserWithRoles) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            //Grab the associated user from the repository
            Optional<User> user = userRepository.findById(principal.getId());

            //Set the admin of the path to the currently logged in user
            path.setAdmin(user.get());

            //Save the new path with the image to the db
            pathRepository.save(path);

        } catch (IOException e) { //Catch any exceptions in the process of saving the path
            throw new RuntimeException(e); //And throw them
        }

        //When completed, simply redirect the user the admins index page
        return "redirect:/";
    }
}
