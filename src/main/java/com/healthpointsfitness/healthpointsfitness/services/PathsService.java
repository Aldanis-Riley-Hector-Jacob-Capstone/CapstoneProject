package com.healthpointsfitness.healthpointsfitness.services;

import com.healthpointsfitness.healthpointsfitness.models.Challenge;
import com.healthpointsfitness.healthpointsfitness.models.Path;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.PathRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

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

    public int getTotalPathPoints(Path path){
        AtomicInteger points = new AtomicInteger();
        try {
            path.getChallenges().forEach(challenge -> points.addAndGet(challenge.getPoints()));
        } catch (Exception e){
            e.printStackTrace();
        }
        return points.intValue();
    }

    public String getPathImage(Path myPath){
        try {
            byte[] encodeBase64 = Base64.getEncoder().encode(myPath.getImageBlob());
            String base64Encoded;
            base64Encoded = new String(encodeBase64, StandardCharsets.UTF_8);
            return base64Encoded;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean isEnrolled(User user, Path path){
        return user.getFollowed_paths().contains(path);
    }

}
