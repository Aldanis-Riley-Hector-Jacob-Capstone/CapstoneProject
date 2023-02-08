package com.healthpointsfitness.healthpointsfitness.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.healthpointsfitness.healthpointsfitness.enumerations.Muscles;
import com.healthpointsfitness.healthpointsfitness.models.Exercise;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.asynchttpclient.*;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.UriEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.apache.logging.log4j.util.Strings.join;

@Service
public class ExercisesService {
    public List<Exercise> getExercisesByMuscle(String muscle){
        try {
            AsyncHttpClient client = new DefaultAsyncHttpClient();
            Future<Response> request =  client.prepareGet("https://exercises-by-api-ninjas.p.rapidapi.com/v1/exercises?muscle=" + muscle)
                    .setHeader("X-RapidAPI-Key", "afab82c435msh7fe14b32571cb10p1844b4jsnadcc74159ad9")
                    .setHeader("X-RapidAPI-Host", "exercises-by-api-ninjas.p.rapidapi.com")
                    .execute();
            Response response = request.get();
            Exercise[] exercises;
            String body = response.getResponseBody();
            GsonBuilder gsonBuilder = new GsonBuilder();
            exercises = gsonBuilder.create().fromJson(body,Exercise[].class);
            client.close();
            return Arrays.stream(exercises).toList();
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}

