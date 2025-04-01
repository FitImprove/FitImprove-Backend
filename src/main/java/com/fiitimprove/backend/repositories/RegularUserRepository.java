package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.RegularUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegularUserRepository extends JpaRepository<RegularUser, Long> {
}