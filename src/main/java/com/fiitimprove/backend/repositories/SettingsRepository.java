package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<Settings, Long> {
    Settings findByUserId(Long userId);
}