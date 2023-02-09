package com.healthpointsfitness.healthpointsfitness.services;

import com.healthpointsfitness.healthpointsfitness.models.Challenge;
import com.healthpointsfitness.healthpointsfitness.models.Path;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.PathRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PathsService {
    @Autowired
    PathRepository pathRepository;

    public void createOrUpdatePath(Path path){
        pathRepository.save(path);
    }

    public void addChallenge(Path path,Challenge challenge){
        List<Challenge> currentChallenges = path.getChallenges();
        currentChallenges.add(challenge);
        path.setChallenges(currentChallenges);
    }

    public void removeChallenge(Path path, Challenge challenge){
        List<Challenge> currentChallenges = path.getChallenges();
        currentChallenges.remove(challenge);
        path.setChallenges(currentChallenges);
    }

    public Path getPathById(Long id){
        try {
            return pathRepository.findById(id).get();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

//    public List<Path> getPathsByUser(User user){
//        try {
//            return pathRepository.findByUser();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }

}
