package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.Path;
import com.healthpointsfitness.healthpointsfitness.services.PathsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;

@Controller
public class LandingController {

    @Autowired
    PathsService pathsService;


    @GetMapping("/landing")
    public String welcome(Model model) {
        List<Path> paths = pathsService.getAllPaths();
        for (Path path : paths) {

            byte[] encodeBase64 = Base64.getEncoder().encode(path.getImageBlob());
            String base64Encoded;
            try {
                base64Encoded = new String(encodeBase64, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            path.setImageDataUrl(base64Encoded);
        }
        model.addAttribute("paths", paths);
        return "landing";
    }


}
