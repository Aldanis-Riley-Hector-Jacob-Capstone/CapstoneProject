package com.healthpointsfitness.healthpointsfitness.controllers.PathsApi;

import com.google.gson.GsonBuilder;
import com.healthpointsfitness.healthpointsfitness.models.Challenge;
import com.healthpointsfitness.healthpointsfitness.models.Exercise;
import com.healthpointsfitness.healthpointsfitness.models.Path;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.ChallengeRepository;
import com.healthpointsfitness.healthpointsfitness.repositories.ExerciseRepository;
import com.healthpointsfitness.healthpointsfitness.repositories.PathRepository;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import com.healthpointsfitness.healthpointsfitness.services.ExercisesService;
import com.healthpointsfitness.healthpointsfitness.services.PathsService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@RequestMapping(path="/admin/api/v1")
public class PathsApi {

    @Autowired
    ExercisesService service;

    @Autowired
    PathsService pathService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PathRepository pathRepository;
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;

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
    ) {
        try {
            //Get a gson builder to deserialize the challenges string
            GsonBuilder builder = new GsonBuilder();

            //Deserialize a list of challenges from the challenges string of json
            Challenge[] challengeList = builder.create().fromJson(challenges, Challenge[].class);

            //Create a new empty path
            Path newPath = new Path();

            //Iterate through all the challenges
            for (Challenge challenge : challengeList) {
                //And print out the title
                System.out.println(challenge.getTitle());

                //Create a reference to the challenge for each exercise for hibernate
                challenge.getExercises().forEach(exercise->{
                    exercise.setChallenge(challenge);
                });

                //Set the path for the challenge for hibernate
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

            //Set the admin for the new path
            newPath.setAdmin(me);

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

    @PatchMapping(
            name = "updatePath",
            path="/updatePath",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private Boolean updatePath(
            @RequestParam(required = false) MultipartFile badge,
            @RequestParam String challenges,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Long pathId
    ) {
        try {
            //Get a gson builder to deserialize the challenges string
            GsonBuilder builder = new GsonBuilder();

            //Deserialize a list of challenges from the challenges string of json
            Challenge[] challengeList = builder.create().fromJson(challenges, Challenge[].class);

            //Get the path and delete it
            Path path = pathService.findPathById(pathId);

            //Remove challenges that have been deleted
            List<Long> challengeIds = Arrays.stream(challengeList).map(Challenge::getId).toList();

            //Iterate through all the challenges in the path
            path.getChallenges().forEach(c->{

                //Grab the id of the current challenge
                Long id = c.getId();

                //If the new challenges does not contain that id
                if(!challengeIds.contains(id)){

                    //Deletes all exercises related to this challenge
                    exerciseRepository.deleteAll(c.getExercises());

                    //Clear the path association for deletion
                    c.setPath(null);

                    //And delete the challenge from the db
                    challengeRepository.delete(c);
                }
            });

            //Remove exercises that are not included in the path per challenge
            //Iterate through the current challenges
            path.getChallenges().forEach(challenge->{

                //Iterate through each challenge's exercises
                challenge.getExercises().forEach(exercise -> {

                    //Go through each new challenge
                    Arrays.stream(challengeList).forEach(nChallenge->{

                        //If the id's match
                        if(Objects.equals(nChallenge.getId(), challenge.getId())){

                            //Get the list of current exercises
                            List<Exercise> currentExercises = challenge.getExercises();

                            //Get the list of new exercises
                            List<Exercise> newExercises = nChallenge.getExercises();

                            //Iterate through the current exercises
                            currentExercises.forEach(curEx -> {

                                //If the list of new exercises does not contain the current exercise
                                if(!newExercises.stream().map(Exercise::getId).toList().contains(curEx.getId())){

                                    //Remove reference to the challenge for deletion
                                    curEx.setChallenge(null);

                                    //Get rid of it
                                    exerciseRepository.delete(curEx);
                                }
                            });
                        }
                    });
                });
            });


            //Set the path in the challenges list items
            Path finalPath = path;

            //Iterate through the new list of challenges
            Arrays.stream(challengeList).forEach(challenge -> {

                //If the id of the current challenge is null
                if(challenge.getId() == null){

                    //Go through its exercises and set their reference to this challenge
                    challenge.getExercises().forEach(e->e.setChallenge(challenge));

                    //And then save the challenge to the db
                    challengeRepository.save(challenge);

                }else{ //Otherwise

                    //Iterate through it's exercises
                    challenge.getExercises().forEach(exercise -> {

                        //If the exercise does not have a reference to the challenge
                        if(exercise.getChallenge() == null){

                            //Create the reference
                            exercise.setChallenge(challenge);

                            //And save the exercise to the db
                            exerciseRepository.save(exercise);
                        }
                    });
                }

                //Set the path for the challenge
                challenge.setPath(finalPath);
            });

            //Pull the path with the updates
            path = pathService.findPathById(pathId);

            //Set the path's title
            path.setTitle(title);

            //Set the path's description
            path.setDescription(description);

            //Turn the image file into a blob
            if(badge != null) {
                if (!badge.isEmpty()) { //If there's a new image, set it to the new one

                    //Grab the bytes from the input stream of the path image
                    byte[] pathImageBytes = badge.getInputStream().readAllBytes();

                    //Set them in the path object instance
                    path.setImageBlob(pathImageBytes);
                }
            }

            //Set the challenges in the path
            path.setChallenges(challengeRepository.saveAllAndFlush(Arrays.stream(challengeList).toList()));

            //save the path
            pathRepository.save(path);

            //Notify the caller of success
            return true;
        }catch(Exception e){ //Catch any exceptions

            //And print the errors to the terminal
            e.printStackTrace();

            //Notify caller of failure
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



    /*
     * Post Mapping for editing/updating a path
     */
    @RequestMapping(
            name = "editPath",
            path = "/editPath",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST
    )
    public String editPathPost(@RequestBody Path updatedPath) {
        return "";
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class CompleteExerciseRequest{
        Long exerciseId;
        Boolean completed;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class CompleteExerciseResponse{
        Boolean success;
        String message;
    }

    @RequestMapping(name = "completeExercise",
            path = "/completeExercise",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST
    )
    public CompleteExerciseResponse completed(@RequestBody CompleteExerciseRequest request){
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Optional<User> currentUserOpt = userRepository.findById(currentUser.getId());
            if (currentUserOpt.isPresent()) {
                currentUser = currentUserOpt.get();
            }
            Optional<Exercise> exerciseOpt = exerciseRepository.findById(request.exerciseId);
            if (exerciseOpt.isPresent()) {
                Exercise myExercise = exerciseOpt.get();
                if (request.completed) {
                    currentUser.getCompletedExercises().add(myExercise);
                    userRepository.save(currentUser);
                } else {
                    currentUser.getCompletedExercises().remove(myExercise);
                    userRepository.save(currentUser);
                }
            }
            return new CompleteExerciseResponse(true, "Exercise marked " + String.valueOf(request.completed ?
                    "complete" :
                    "incomplete"));
        } catch (Exception e){
            e.printStackTrace();
        }
        return new CompleteExerciseResponse(false,"There was an error completing challenge");
    }
}
