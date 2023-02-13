package com.healthpointsfitness.healthpointsfitness.repositories;

import com.healthpointsfitness.healthpointsfitness.models.RecoveryRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecoveryRequestRepository extends JpaRepository<RecoveryRequest,Long> {
    RecoveryRequest findRecoveryRequestByCode(String code);
}
