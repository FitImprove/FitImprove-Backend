package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.RegularUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link RegularUser} that allows to make operations over db
 */
public interface RegularUserRepository extends JpaRepository<RegularUser, Long> {
}