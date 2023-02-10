package com.healthpointsfitness.healthpointsfitness.repositories;

import com.healthpointsfitness.healthpointsfitness.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
    List<User> findUsersByUsername(String username);

/*
    @Query("SELECT u FROM User u WHERE u.id = 1)
    Collection<User> findAllActiveUsers();
*/

}
//select * from user where username is like %:keword%", nativeQuery = true