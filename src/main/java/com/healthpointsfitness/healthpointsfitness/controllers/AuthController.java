package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.models.UserWithRoles;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Controller
public class AuthController {
    @Autowired
    private UserRepository userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;


    @GetMapping("/login")
    private String login(){
        return "login"; //Return the login view
    }

    @GetMapping("/register")
    private String registerGet(Model model){
        model.addAttribute("user",new User()); //Bind an empty user entity
        return "register"; //Return the registration view
    }

    @PostMapping("/register")
    private String registerPost(@ModelAttribute("user") User user, HttpServletRequest request) {
        try { //Try to
            String clearPass = user.getPassword();
            user.setPassword(passwordEncoder.encode(user.getPassword())); //Encode the password
            userDao.save(user); //Save the user to the database
            request.login(user.getUsername(),clearPass);
            if(request.isUserInRole("ROLE_ADMIN")){
                return "/admin/index";
            }else if(request.isUserInRole("ROLE_USER")){
                return "/users/index";
            }
        }catch(Exception e) { //Catch any exceptions
            System.out.println(e.getMessage());  //Print the exception to the terminal
            e.printStackTrace();
        }

        //Return the index view
        return "index";
    }

    @GetMapping("/")
    private String rootMapping() {
        try {
            var principal = (UserWithRoles) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            var user = userDao.findUserByUsername(principal.getUsername());
//            System.out.println(user.getId());
            var roles = AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRoles());
//            System.out.println("Contains Admin Authority: " + roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
            principal.getAuthorities().forEach(auth->System.out.println(auth.getAuthority()));
            if (roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return "/admin/index";
            } else if (roles.contains(new SimpleGrantedAuthority("ROLE_USER"))) {
                return "/users/index";
            } else {
                return "/login";
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return "/login";
    }
}
