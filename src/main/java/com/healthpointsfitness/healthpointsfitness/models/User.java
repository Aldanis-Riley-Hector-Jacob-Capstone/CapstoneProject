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

    @Column(name = "bio", nullable = true, unique = false, length = 255)
    private String bio = "You can write your bio in the Profile Settings.";

    @Lob
    @Column(name = "profile_image", length = Integer.MAX_VALUE, nullable = true)
    private byte[] profileImage;

    @Transient
    private String profileImageDataUrl = "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";

    @Column(name = "is_admin", nullable = true)
    private Boolean isAdmin = false;

    @Column(nullable = false, unique = false, name = "roles", length = 255)
    private String roles = "ROLE_CLIENT";

    @Column(nullable = false, unique = false, name = "total_points", length = 255)
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
        this.bio = bio = "";
        this.profileImage = null;
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

    public User(User copy) {
        id = copy.id; // This line is SUPER important! Many things won't work if it's absent
        email = copy.email;
        username = copy.username;
        password = copy.password;
        firstName = copy.firstName;
        lastName = copy.lastName;
        roles = copy.roles;
        totalPoints = copy.totalPoints;
        bio = copy.bio;
        profileImage = copy.profileImage;
        goals = copy.goals;
        created_paths = copy.created_paths;
        followed_paths = copy.followed_paths;
    }
}
