package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.Path;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.models.UserWithRoles;
import com.healthpointsfitness.healthpointsfitness.repositories.PathRepository;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import com.healthpointsfitness.healthpointsfitness.services.UserDetailsLoader;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.io.UnsupportedEncodingException;
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
    private String registerPost(@ModelAttribute("user") User user, HttpServletRequest request, Model model) {
        try { //Try to
            String clearPass = user.getPassword();
            user.setPassword(passwordEncoder.encode(user.getPassword())); //Encode the password
            user.setTotalPoints(0L);
            userDao.save(user); //Save the user to the database
            request.login(user.getUsername(),clearPass);
            if(request.isUserInRole("ROLE_ADMIN")){
                return "redirect:/admin/index";
            }else if(request.isUserInRole("ROLE_CLIENT")){
                model = userDetailsLoader.getUserData(model);
                return "/users/index";
            }
        }catch(Exception e) { //Catch any exceptions
            System.out.println(e.getMessage());  //Print the exception to the terminal
            e.printStackTrace();
        }

        //Return the index view
        return "index";
    }

    @GetMapping("/admin/index")
    private String adminIndexGet(
            Model model,
            @PageableDefault(value = 2) Pageable pageable
    ){
        Page<Path> currentPage = pathRepository.findAll(pageable);
        Integer pageCount = currentPage.getTotalPages();
        currentPage.forEach(path->{
            byte[] encodeBase64 = Base64.getEncoder().encode(path.getImageBlob());
            String base64Encoded;
            try {
                base64Encoded = new String(encodeBase64, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            path.setImageDataUrl(base64Encoded);
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
        return "/admin/index";
    }

    @GetMapping("/")
    private String rootMapping(Model model) {
        try {
            var principal = (UserWithRoles) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            var user = userDao.findUserByUsername(principal.getUsername());
//            System.out.println(user.getId());
            var roles = AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRoles());
//            System.out.println("Contains Admin Authority: " + roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
            principal.getAuthorities().forEach(auth->System.out.println(auth.getAuthority()));
            if (roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
               return "redirect:/admin/index";
            } else if (roles.contains(new SimpleGrantedAuthority("ROLE_CLIENT"))) {
                model = userDetailsLoader.getUserData(model);
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
