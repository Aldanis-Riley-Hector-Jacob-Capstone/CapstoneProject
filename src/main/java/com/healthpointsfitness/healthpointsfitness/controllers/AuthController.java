package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.Exercise;
import com.healthpointsfitness.healthpointsfitness.models.Path;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.models.UserWithRoles;
import com.healthpointsfitness.healthpointsfitness.repositories.PathRepository;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import com.healthpointsfitness.healthpointsfitness.services.PathsService;
import com.healthpointsfitness.healthpointsfitness.services.UserDetailsLoader;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class AuthController {
    @Autowired
    private UserRepository userDao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private PathRepository pathRepository;
    @Autowired
    private UserDetailsLoader userDetailsLoader;
    @Autowired
    PathsService pathServ;


    @GetMapping("/login")
    private String login() {
        if(!SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass().equals(String.class)) {

            if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                return "redirect:/profile/" + user.getUsername();
            }
        }
        return "login"; //Return the login view
    }

    @GetMapping("/register")
    private String registerGet(Model model){
        model.addAttribute("user",new User()); //Bind an empty user entity
        return "register"; //Return the registration view
    }

    @PostMapping("/register")
    private String registerPost(@ModelAttribute("user") User user, HttpServletRequest request, Model model) {
        try { //Try to
            String clearPass = user.getPassword();
            user.setPassword(passwordEncoder.encode(user.getPassword())); //Encode the password
            user.setTotalPoints(0L);
            userDao.save(user); //Save the user to the database
            request.login(user.getUsername(),clearPass);
            if(request.isUserInRole("ROLE_ADMIN")){
                return "redirect:/admin/landing";
            }else if(request.isUserInRole("ROLE_CLIENT")){
                model = userDetailsLoader.getUserData(model);
                //TODO: Update the user model if they completed a path based on the exercises and give them the badge as well
                List<Path> allPaths = pathRepository.findAll();
                List<Path> completedPaths = allPaths.stream().filter(path->{
                    List<Exercise> allExercisesForPath = new ArrayList<>();
                    path.getChallenges().forEach(challenge -> {
                        allExercisesForPath.addAll(challenge.getExercises());
                    });
                    List<Exercise> completed = user.getCompletedExercises().stream().filter(allExercisesForPath::contains).toList();
                    return completed.containsAll(allExercisesForPath);
                }).toList();

                //Generate image for the completed paths
                completedPaths.forEach(path->{
                    byte[] encodeBase64 = Base64.getEncoder().encode(path.getImageBlob());
                    String base64Encoded = new String(encodeBase64, StandardCharsets.UTF_8);
                    path.setImageDataUrl(base64Encoded);
                });

                //Set transient values for the path
                user.getFollowed_paths().forEach(path->{
                    //Set the image
                    byte[] encodeBase64 = Base64.getEncoder().encode(path.getImageBlob());
                    String base64Encoded = new String(encodeBase64, StandardCharsets.UTF_8);
                    path.setImageDataUrl(base64Encoded);

                    //Setting path percent done
                    List<Exercise> allExercisesForPath = new ArrayList<>();
                    path.getChallenges().forEach(challenge -> {
                        allExercisesForPath.addAll(challenge.getExercises());
                    });

                    List<Exercise> completed = user.getCompletedExercises().stream().filter(allExercisesForPath::contains).toList();
                    Double percent = Double.valueOf(completed.size()) / Double.valueOf(allExercisesForPath.size());
                    path.setPercentDone(percent * 100);
                });

                model.addAttribute("user", user);
                model.addAttribute("completed_paths", completedPaths);
                return "users/landing";
            }
        }catch(DataIntegrityViolationException e) { //Catch any exceptions
            e.printStackTrace();
            return "redirect:register?exists=true";
        }catch(Exception e){
            e.printStackTrace();
        }

        //Return the index view
        return "landing";
    }

    @GetMapping("/admin/landing")
    private String adminIndexGet(
            Model model,
            @PageableDefault(value = 2) Pageable pageable
    ){
        try {
            if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
                User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                Long id = loggedInUser.getId();
                System.out.println(id);

                model.addAttribute("userid", id);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        Page<Path> currentPage = pathRepository.findAll(pageable);
        Integer pageCount = currentPage.getTotalPages();
        currentPage.forEach(path->{
            path.setImageDataUrl(pathServ.getPathImage(path));
        });
        model.addAttribute("paths",currentPage);
        model.addAttribute("totalPages",currentPage.getTotalPages());
        model.addAttribute("offset",pageable.getOffset());
        model.addAttribute("pageSize",pageable.getPageSize());
        model.addAttribute("pageNumber",pageable.getPageNumber());
        model.addAttribute("page_count",pageCount);
        List<Integer> pageNumbers = IntStream.rangeClosed(1, currentPage.getTotalPages())
                .boxed()
                .collect(Collectors.toList());
        model.addAttribute("pageNumbers",pageNumbers);
        return "admin/landing";
    }

    @GetMapping("/")
    private String rootMapping(Model model) {
        try {
            if(!SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass().equals(String.class)) {
                UserWithRoles principal = (UserWithRoles) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                var user = userDao.findById(principal.getId());
                if (user.isPresent()) {
                    var roles = AuthorityUtils.commaSeparatedStringToAuthorityList(user.get().getRoles());
                    principal.getAuthorities().forEach(auth -> System.out.println(auth.getAuthority()));
                    if (roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                        return "redirect:/admin/landing";
                    } else if (roles.contains(new SimpleGrantedAuthority("ROLE_CLIENT"))) {
                        model.addAttribute("user", user);
                        return "redirect:/profile/" + user.get().getUsername();
                    } else {
                        return "landing";
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return "landing";
    }
}
