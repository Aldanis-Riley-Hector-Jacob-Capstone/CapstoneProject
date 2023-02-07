package com.healthpointsfitness.healthpointsfitness.services;

import com.healthpointsfitness.healthpointsfitness.models.Challenge;
import com.healthpointsfitness.healthpointsfitness.models.Path;
import com.healthpointsfitness.healthpointsfitness.repositories.PathRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathsService {
    @Autowired
    PathRepository pathRepository;

    private void createOrUpdatePath(Path path){
        pathRepository.save(path);
    }

    private void addChallenge(Path path,Challenge challenge){
        List<Challenge> currentChallenges = path.getChallenges();
        currentChallenges.add(challenge);
        path.setChallenges(currentChallenges);
    }

    private void removeChallenge(Path path, Challenge challenge){
        List<Challenge> currentChallenges = path.getChallenges();
        currentChallenges.remove(challenge);
        path.setChallenges(currentChallenges);
    }
}
