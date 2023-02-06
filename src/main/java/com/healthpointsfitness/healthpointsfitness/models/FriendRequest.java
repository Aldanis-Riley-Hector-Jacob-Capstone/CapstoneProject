package com.healthpointsfitness.healthpointsfitness.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="friend_request")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="approved", nullable = false)
    private Boolean requestApproved = false;

    @Column(name="date_sent", nullable = false)
    private Timestamp date_sent;

    @Column(name="date_approved_or_denied", nullable = false)
    private Timestamp date_approved_or_denied;

    @ManyToOne
    private User from;

    @ManyToOne
    private User to;
}
