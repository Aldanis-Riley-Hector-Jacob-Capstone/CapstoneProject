package com.healthpointsfitness.healthpointsfitness.services;

import com.healthpointsfitness.healthpointsfitness.models.Path;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.models.UserWithRoles;
import com.healthpointsfitness.healthpointsfitness.repositories.PathRepository;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;

@Service
public class UserDetailsLoader implements UserDetailsService {
    private final UserRepository users;

    @Autowired
    private UserRepository userDao;
    @Autowired
    private PathRepository pathRepository;


    public UserDetailsLoader(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = users.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("No user found for " + username);
        }

        return new UserWithRoles(user);
    }


    public Long getUserPoints(int userid) {
        User user = users.findUserById(userid);
        if (user == null) {
            throw new UsernameNotFoundException("A user could not be found matching the id of  " + userid);
        }
        return user.getTotalPoints();
    }

//    Created here to be used in both UserController and AuthController instead of having to repeat the same code over and over
    public Model getUserData(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User curUser = userDao.findUserByUsername(auth.getName());

//        List<Path> paths = pathRepository.findAll();
        List<Path> paths = curUser.getFollowed_paths();

//        Dynamic Path icons / badges
        for (Path path : paths){
            byte[] encodeBase64 = Base64.getEncoder().encode(path.getImageBlob());
            String base64Encoded;
            try {
                base64Encoded = new String(encodeBase64, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            path.setImageDataUrl(base64Encoded);
        }

//        Gets total points
        Long pointsTotal = curUser.getTotalPoints();


        model.addAttribute("paths", paths);
        model.addAttribute("points", pointsTotal);

        return model;
    }
}