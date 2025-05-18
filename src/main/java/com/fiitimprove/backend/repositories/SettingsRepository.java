package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for {@link Settings} that allows to make operations over db
 */
public interface SettingsRepository extends JpaRepository<Settings, Long> {
    Optional<Settings> findByUserId(Long userId);
}