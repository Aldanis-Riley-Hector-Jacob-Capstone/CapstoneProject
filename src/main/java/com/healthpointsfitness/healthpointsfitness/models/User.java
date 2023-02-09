package com.healthpointsfitness.healthpointsfitness.models;

import com.healthpointsfitness.healthpointsfitness.models.Goal;
import com.healthpointsfitness.healthpointsfitness.models.Path;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.util.buf.StringUtils;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email",nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, unique = false, length = 255)
    private String password;

    @Column(name = "username", nullable = false, unique = true, length = 255)
    private String username;

    @Column(name = "first_name", nullable = true, unique = false, length = 255)
    private String firstName;

    @Column(name = "last_name", nullable = true, unique = false, length = 255)
    private String lastName;

    @Column(name = "is_admin", nullable = true)
    private Boolean isAdmin = false;

    @Column(nullable = false, unique = false, name = "roles", length = 255)
    private String roles = "ROLE_USER";

    @Column(nullable = false, unique = false, name = "totalPoints", length = 255)
    private Long totalPoints;

    public User(String username, String password, List<Role> roles){
        this.username = username;
        this.password = password;
        List<String> rolesStrList = new ArrayList<>();
        roles.forEach(role -> {
            rolesStrList.add(role.getRoleName());
        });
        this.roles = StringUtils.join(rolesStrList,',');
        this.totalPoints = 0L;
    }

    public User(User copy) {
        id = copy.id; // This line is SUPER important! Many things won't work if it's absent
        email = copy.email;
        username = copy.username;
        password = copy.password;
        roles = copy.roles;
        totalPoints = copy.totalPoints;
    }


    ///////// RELATIONSHIPS ////////////

    //User <> Goal
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Goal> goals;

    //User(Admin) <> Path
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Path> created_paths;


    //User(Regular User) <> Path
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Path> followed_paths;

    //User <> User
    @ManyToMany(cascade = CascadeType.ALL)
    private List<User> friends;


    @OneToMany(cascade = CascadeType.ALL)
    private List<FriendRequest> friend_requests;
}
