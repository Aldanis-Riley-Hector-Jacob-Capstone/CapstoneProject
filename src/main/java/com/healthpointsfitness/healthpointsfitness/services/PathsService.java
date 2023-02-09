package com.healthpointsfitness.healthpointsfitness.services;

import com.healthpointsfitness.healthpointsfitness.models.Challenge;
import com.healthpointsfitness.healthpointsfitness.models.Path;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.PathRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public List<Path> getAllPaths() {
        return pathRepository.findAll();
    }

    public Path findPathById(Long id){
        try {
            Optional<Path> p = pathRepository.findById(id);
            if (p.isPresent()) {
                return p.get();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void deletePathById(Long id){
        try { //Attempt to delete the path by it's id
            pathRepository.deleteById(id);
        } catch(Exception e){ //Catch any whales
            e.printStackTrace(); //Send them fishing
        }
    }
}
