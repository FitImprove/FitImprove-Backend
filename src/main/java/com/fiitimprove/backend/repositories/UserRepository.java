package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link User} that allows to make operations over db
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}