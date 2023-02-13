package com.healthpointsfitness.healthpointsfitness.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthpointsfitness.healthpointsfitness.models.RecoveryRequest;
import com.healthpointsfitness.healthpointsfitness.repositories.RecoveryRequestRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/user/api/v1")
public class RecoveryCodeApiController {

    @Autowired
    private RecoveryRequestRepository recoveryRequestRepository;

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    private static class CodeCheckRequest {
        private String code;
        private String email;
    }
    @RequestMapping(
            name="recoveryCodeCheck",
            path="/recoveryCodeCheck",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private String recoveryCodeCheck(@RequestBody CodeCheckRequest codeCheckRequest){
        System.out.println(codeCheckRequest.code);
        System.out.println(codeCheckRequest.email);
        try {
            //Grab the recovery request
            RecoveryRequest request = recoveryRequestRepository.findRecoveryRequestByCode(codeCheckRequest.code);

            //If the request is not null
            if (request != null) {
                //Return valid
                return "{\"valid\":\"true\"}";
            } else { //Otherwise
                //Return invalid
                return "{\"valid\":\"false\"}";
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        //If all fails return code invalid
        return "{\"valid\":\"false\"}";
    }


}
