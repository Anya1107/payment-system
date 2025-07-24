package com.userservice.repository;

import com.userservice.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = {"address", "individual"})
    Optional<User> findById(UUID id);

    @EntityGraph(attributePaths = {"address", "individual"})
    Optional<User> findByEmail(String email);
}
