package com.healthpointsfitness.healthpointsfitness.repositories;

import com.healthpointsfitness.healthpointsfitness.models.FriendRequest;
import com.healthpointsfitness.healthpointsfitness.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRequestsRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findFriendRequestByFromAndTo(User from, User to);
}
