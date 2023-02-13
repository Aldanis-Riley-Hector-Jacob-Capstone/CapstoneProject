package com.healthpointsfitness.healthpointsfitness.repositories;

import com.healthpointsfitness.healthpointsfitness.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);

    User findUserById(Long id);

    User findUserByEmail(String email);

    List<User> findUserByUsernameContaining(String string);

    List<User> findUserByTotalPointsLessThanEqual(Integer points);
}
