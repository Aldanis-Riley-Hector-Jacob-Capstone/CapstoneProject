package com.healthpointsfitness.healthpointsfitness.controllers.PathsApi;

import com.google.gson.GsonBuilder;
import com.healthpointsfitness.healthpointsfitness.models.Challenge;
import com.healthpointsfitness.healthpointsfitness.models.Exercise;
import com.healthpointsfitness.healthpointsfitness.models.Path;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import com.healthpointsfitness.healthpointsfitness.services.ExercisesService;
import com.healthpointsfitness.healthpointsfitness.services.PathsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path="/admin/api/v1")
public class PathsApi {

    @Autowired
    ExercisesService service;

    @Autowired
    PathsService pathService;

    @Autowired
    UserRepository userRepository;

    @PostMapping(
            name = "createPath",
            path="/createPath",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private Boolean addPath(
            @RequestParam MultipartFile badge,
            @RequestParam String challenges,
            @RequestParam String title,
            @RequestParam String description
    ) throws IOException {
        try {
            System.out.println(badge.getName());
            System.out.println(badge.getBytes().length);

            //Get a gson builder to deserialize the challenges string
            GsonBuilder builder = new GsonBuilder();

            //Deserialize a list of challenges from the challenges string of json
            Challenge[] challengeList = builder.create().fromJson(challenges, Challenge[].class);

            //Create a new empty path
            Path newPath = new Path();

            //Iterate trough all the challenges
            for (Challenge challenge : challengeList) {
                //And print out the title
                System.out.println(challenge.getTitle());
                challenge.setPath(newPath);
            }

            //Set the path's title
            newPath.setTitle(title);

            //Set the path's description
            newPath.setDescription(description);

            //Set the challenges in the path to the deserialized version of the challenges
            newPath.setChallenges(Arrays.stream(challengeList).toList());

            //Get the currently authenticated user
            User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            User meme = userRepository.findById(me.getId()).get();

            //Set the admin for the new path
            newPath.setAdmin(meme);

            //Turn the image file into a blob
            if (!badge.isEmpty()) { //If there's a new image, set it to the new one
                //Grab the bytes from the input stream of the path image
                byte[] pathImageBytes = badge.getInputStream().readAllBytes();

                //Set them in the path object instance
                newPath.setImageBlob(pathImageBytes);
            }

            //Use the path service to save the new path with the image and everything
            pathService.createOrUpdatePath(newPath);


            //Return the saved path to the client as JSON
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @RequestMapping(
            name = "findExerciseByMuscle",
            path = "/findExerciseByMuscle",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST
    )
    public List<Exercise> findExerciseByMuscle(@RequestBody String muscle){

        try {
            List<Exercise> matchingExercises = service.getExercisesByMuscle(muscle);
            return matchingExercises;
        }catch(Exception e){
            System.out.println("Error in findExerciseByMuscle");
            e.printStackTrace();
        }
    ;    return null;
    }

    @GetMapping(
            name = "test",
            path = "/test",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String test(){
        return "I work";
    }
}
