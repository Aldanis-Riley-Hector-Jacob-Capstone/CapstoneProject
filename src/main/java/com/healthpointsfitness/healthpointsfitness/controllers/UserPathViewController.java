package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.Path;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.PathRepository;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import com.healthpointsfitness.healthpointsfitness.services.PathsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class UserPathViewController {
    @Autowired
    PathRepository pathRepo;
    @Autowired
    PathsService pathServ;
    @Autowired
    private UserRepository userDao;

    @GetMapping("/paths/{pathnumber}")
    public String paths02(@PathVariable Long pathnumber, Model model){
        try {
            if (pathnumber != null) {
                Path myPath = pathRepo.getReferenceById(pathnumber);
                myPath.setImageDataUrl(pathServ.getPathImage(myPath));
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User curUser = userDao.findUserByUsername(auth.getName());
                model.addAttribute("enrolled", pathServ.isEnrolled(curUser, myPath));
                model.addAttribute("path", myPath);
                model.addAttribute("points", pathServ.getTotalPathPoints(myPath));
            } else {
                return "/profile";
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return "/users/viewPath";
    }

    @GetMapping("/allPaths")
    public String allPaths(Model model){
        try {
            List<Path> paths = pathRepo.findAll();

            // Iterate through all path objects and assign image
            for (Path path : paths){
                path.setImageDataUrl(pathServ.getPathImage(path));
            }

            model.addAttribute("paths", paths);

            return "/users/allPaths";
        }catch (Exception e){
            e.printStackTrace();
        }

        return "/profile";
    }
}
