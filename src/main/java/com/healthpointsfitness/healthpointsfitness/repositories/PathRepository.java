package com.healthpointsfitness.healthpointsfitness.repositories;

import com.healthpointsfitness.healthpointsfitness.models.Path;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PathRepository extends JpaRepository<Path, Long> {

}
