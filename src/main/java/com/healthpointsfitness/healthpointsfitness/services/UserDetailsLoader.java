package com.healthpointsfitness.healthpointsfitness.services;

import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.models.UserWithRoles;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsLoader implements UserDetailsService {
    private final UserRepository users;

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
}