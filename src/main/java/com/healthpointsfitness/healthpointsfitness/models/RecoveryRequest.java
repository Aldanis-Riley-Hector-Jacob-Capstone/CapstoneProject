package com.healthpointsfitness.healthpointsfitness.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name="recovery_request")
public class RecoveryRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    User user;

    @Column(name = "recovery_code",nullable = false,length = 255)
    String code;

    public RecoveryRequest(RecoveryRequest copy){
        id = copy.id;
        user = copy.user;
        code = copy.code;
    }

}
