package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.PathRepository;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import com.healthpointsfitness.healthpointsfitness.services.UserDetailsLoader;
import com.healthpointsfitness.healthpointsfitness.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/*
    Currently exists to allow basic view swapping with a navbar
 */
@Controller
public class UserController {
    @Autowired
    PathRepository pathRepo;
    @Autowired
    UserService userService;
    @Autowired
    private UserDetailsLoader userDetailsLoader;

    String defaultProfileImage = "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";
    @Autowired
    private UserRepository userRepository;

    @GetMapping("profile/{username}")
    public String getProfileView(@PathVariable("username") String username, Model model){
        try {
            User user = userService.findUserByUsername(username);
            if(user != null) {
                model.addAttribute("userid", user.getId());

                if(user.getProfileImage() != null) {
                    byte[] encodeBase64 = Base64.getEncoder().encode(user.getProfileImage());
                    String base64Encoded = new String(encodeBase64, StandardCharsets.UTF_8);
                    user.setProfileImageDataUrl(base64Encoded);
                }else{
                    user.setProfileImageDataUrl(defaultProfileImage);
                }

                //Set the imageDataUrl for user followed paths
                user.getFollowed_paths().forEach(path->{
                    byte[] encodeBase64 = Base64.getEncoder().encode(path.getImageBlob());
                    String base64Encoded = new String(encodeBase64, StandardCharsets.UTF_8);
                    path.setImageDataUrl(base64Encoded);
                });

                model.addAttribute("user", user);

                return "users/landing";
            }else{
                return "redirect:users/profilenotfound";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return "redirect:users/profilenotfound";
    }

    @GetMapping("users/profilenotfound")
    public String profileNotFoundView(){
        return "users/profilenotfound";
    }

    @GetMapping("fsearch")
    public String UserController_UniqueName_02(){
        return "users/friends";
    }

    @GetMapping("profile/settings")
    private String getProfileSettingsView(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userFromRepo = userService.findUserByUsername(user.getUsername());
        System.out.println(userFromRepo.getFirstName());
        if(userFromRepo != null){
            System.out.println(user.getId());
            System.out.println(user.getUsername());
            model.addAttribute("username",userFromRepo.getUsername());
            model.addAttribute("user_id",userFromRepo.getId());
            model.addAttribute("firstName",userFromRepo.getFirstName());
            model.addAttribute("lastName",userFromRepo.getLastName());
            model.addAttribute("bio",userFromRepo.getBio());
            byte[] profileImage = userFromRepo.getProfileImage();
            if(profileImage != null) {
                byte[] encodeBase64 = Base64.getEncoder().encode(profileImage);
                String base64Encoded = new String(encodeBase64, StandardCharsets.UTF_8);
                model.addAttribute("profile_image", base64Encoded);
            }else{
                System.out.println("Profile image is null.");
                model.addAttribute("profile_image",defaultProfileImage);
            }
            model.addAttribute("me",userFromRepo);
        }else{
            System.out.println("User not found by id");
        }
        return "users/settings";

    }

    @PostMapping("profile/settings/update")
    private String postProfileSettingsUpdate(
            @RequestParam(required = false) MultipartFile newImage,
            @ModelAttribute("me") User me
//            Model model
    ) throws IOException {
        try{
            User currentProfile = userRepository.findUserByUsername(me.getUsername());
            System.out.println("profileImageDataUrl: " + me.getProfileImageDataUrl());
            System.out.println(me.getProfileImageDataUrl());
            System.out.println(me.getBio());
            if(newImage != null && !newImage.isEmpty()) {
                currentProfile.setProfileImage(newImage.getBytes());
                byte[] encodeBase64 = Base64.getEncoder().encode(newImage.getBytes());
                String base64Encoded = new String(encodeBase64, StandardCharsets.UTF_8);
                currentProfile.setProfileImageDataUrl(base64Encoded);
            }
//            }else{
//                String url = me.getProfileImageDataUrl();
//                if(!url.isEmpty()) {
//                    System.out.println("Setting the existing profile image url.");
//                    currentProfile.setProfileImageDataUrl(me.getProfileImageDataUrl());
//                }else if(!me.getProfileImageDataUrl().isEmpty()) {
//                    System.out.println("Setting passed profile image url.");
//                    System.out.println(me.getProfileImageDataUrl());
//                    currentProfile.setProfileImageDataUrl(me.getProfileImageDataUrl());
//                }else{
//                    System.out.println("Setting default profile image url.");
//                    currentProfile.setProfileImageDataUrl(defaultProfileImage);
//                }
//            }

            currentProfile.setBio(me.getBio());
            currentProfile.setFirstName(me.getFirstName());
            currentProfile.setLastName(me.getLastName());
//            model.addAttribute("me",currentProfile);

            userRepository.save(currentProfile);

//            model.addAttribute("profile_image",currentProfile.getProfileImageDataUrl());
            return "redirect:profile/settings";
        }catch(Exception e){
            e.printStackTrace();
        }

        //Redirect back to the settings page
        return "users/settings";
//        return "/landing";
    }
}
