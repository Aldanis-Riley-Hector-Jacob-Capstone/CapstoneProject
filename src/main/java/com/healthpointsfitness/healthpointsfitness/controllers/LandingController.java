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

    @GetMapping("landing")
    public String welcome(Model model) {
        List<Path> paths = pathsService.getAllPaths();
        for (Path path : paths) {
            path.setImageDataUrl(pathsService.getPathImage(path));
        }
        model.addAttribute("paths", paths);
        return "landing";
    }
}
