package com.healthpointsfitness.healthpointsfitness.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.healthpointsfitness.healthpointsfitness.enumerations.Muscles;
import com.healthpointsfitness.healthpointsfitness.models.Exercise;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.UriEncoder;

import java.util.List;

import static org.apache.logging.log4j.util.Strings.join;

@Service
public class ExercisesService {
    public void getExercisesByMuscle(Muscles muscle){
        try {
            AsyncHttpClient client = new DefaultAsyncHttpClient();
            client.prepareGet("https://exercises-by-api-ninjas.p.rapidapi.com/v1/exercises?muscle=" + UriEncoder.encode(muscle.name()))
                    .setHeader("X-RapidAPI-Key", "afab82c435msh7fe14b32571cb10p1844b4jsnadcc74159ad9")
                    .setHeader("X-RapidAPI-Host", "exercises-by-api-ninjas.p.rapidapi.com")
                    .execute()
                    .toCompletableFuture()
                    .thenAccept(response -> {
                        String body = response.getResponseBody();
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Exercise[] exercises = gsonBuilder.create().fromJson(body,Exercise[].class);
                        System.out.println("========= TEMPLATE BODY ===========");
                        System.out.println(body);
                        System.out.println("===================================");

                        for(Exercise e : exercises){
                            System.out.println(e.getName());
                            System.out.println(e.getMuscle().name());
                            System.out.println(e.getEquipment());
                            System.out.println(e.getDifficulty());
                            System.out.println(e.getInstructions());
                        }

                    })
                    .join();


            client.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}

