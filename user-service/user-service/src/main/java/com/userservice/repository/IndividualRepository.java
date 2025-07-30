package com.userservice.repository;

import com.userservice.entity.Individual;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IndividualRepository extends JpaRepository<Individual, UUID> {
}
