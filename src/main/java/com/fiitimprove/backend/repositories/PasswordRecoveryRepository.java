package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.PasswordRecovery;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRecoveryRepository extends JpaRepository<PasswordRecovery, Long> {
    public Optional<PasswordRecovery> findByToken(String token);
}