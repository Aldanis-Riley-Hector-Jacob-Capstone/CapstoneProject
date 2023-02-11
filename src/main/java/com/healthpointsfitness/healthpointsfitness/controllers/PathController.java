package com.healthpointsfitness.healthpointsfitness.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthpointsfitness.healthpointsfitness.models.*;
import com.healthpointsfitness.healthpointsfitness.repositories.PathRepository;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.healthpointsfitness.healthpointsfitness.services.PathsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller
public class PathController {
    @Autowired
    private PathRepository pathRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PathsService pathService;

    @GetMapping("/admin/path/create")
    private String createPathGetRequest(Model model){
        model.addAttribute("newPath",new Path());
        return "/admin/path/create";
    }

    @GetMapping("/admin/path/createapitest")
    private String createApiTest(){
        return "/admin/path/createapitest";
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

    /*
     * GET Mapping for edit/update path
     */
    @GetMapping("/admin/path/edit/{id}")
    public String editPathGet(@PathVariable("id") Long pathId, Model model){
        try {
            //Grab the path using the path service
            Path path = pathService.findPathById(pathId);

<<<<<<< HEAD
            //Encode the image blob into a base64 string
            byte[] encodeBase64 = Base64.getEncoder().encode(path.getImageBlob());

            //Get the base 64 UTF-8 encoded version
            String base64Encoded = new String(encodeBase64, StandardCharsets.UTF_8);

            //Set the data url in the path object
            path.setImageDataUrl(base64Encoded);
=======
            path.setImageDataUrl(pathService.getPathImage(path));
>>>>>>> 049af9a22a47fce6e3ee28563b0494183e7ac9cd

            //Add the path to the model so the frontend can display it
            model.addAttribute("path", path);

            //Encode the list of challenges into a json array
            List<Challenge> pathChallenges = path.getChallenges();

            //Turn the challenge list into a json string
            pathChallenges.forEach(challenge->{ //Ninja-ish
                challenge.setPath(null); //Clear the path to allow json conversion without circular references

                //Iterate through the challenge exercises and clear their challenge field
                challenge.getExercises().forEach(exercise -> {
                    exercise.setChallenge(null);
                });
            });

            String challengesArray = new ObjectMapper().writeValueAsString(pathChallenges);

            //Attach the challenges array json to the model
            model.addAttribute("challenges",challengesArray);
<<<<<<< HEAD
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
=======
        } catch (Exception e){
            e.printStackTrace();
>>>>>>> 049af9a22a47fce6e3ee28563b0494183e7ac9cd
        }
        //Return the model and view
        return "/admin/path/edit";
    }

    /*
     * Handle DELETE Mapping for paths
     */
    @GetMapping("/admin/path/delete/{id}")
    public String deletePath(@PathVariable("id") Long pathId){
        try { //This might cause some whales
            //Grab the path and delete it using the path service
            pathService.deletePathById(pathId);
        }catch(Exception e){ //Properly hook them in the jaw
            e.printStackTrace();
        }

        return "redirect:/admin/index";
    }
}
