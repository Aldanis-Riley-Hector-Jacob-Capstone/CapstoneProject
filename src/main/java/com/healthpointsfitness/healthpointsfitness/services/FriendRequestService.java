package com.healthpointsfitness.healthpointsfitness.services;

import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendRequestService {

    private final UserRepository userDao;
    public FriendRequestService(UserRepository userDao) {
        this.userDao =userDao;
    }

    public List<User> getAllUsers() {
        try {
            return userDao.findAll();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
