package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}