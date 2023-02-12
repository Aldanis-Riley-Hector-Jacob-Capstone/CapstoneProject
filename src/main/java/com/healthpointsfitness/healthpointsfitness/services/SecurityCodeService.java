package com.healthpointsfitness.healthpointsfitness.services;

import org.springframework.stereotype.Service;

@Service
public class SecurityCodeService {
    public String generateSecurityCode(Integer length){
        //Character set for security code
        char[] possibleCharacters = new char[]{
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
                'W', 'X', 'Y', 'Z','0','1','2','3','4','5','6','7','8','9'
        };

        //String builder for appending random characters to the security code
        StringBuilder randomSecCode = new StringBuilder();

        //Generate length number of random characters and append them using the StringBuilder
        for(int i = 0; i < length; i++){
            Character randomChar = possibleCharacters[(int) Math.floor(Math.random() * possibleCharacters.length)];
            randomSecCode.append(randomChar);
        }

        //Return the generated security code
        return randomSecCode.toString();
    }
}
