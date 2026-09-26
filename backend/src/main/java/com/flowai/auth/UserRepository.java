package com.flowai.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Used by AuthService during login and by JwtAuthFilter
     * to load a user from a token's subject claim.
     */
    Optional<User> findByEmail(String email);

    /**
     * Used during registration to prevent duplicate accounts.
     */
    boolean existsByEmail(String email);
}