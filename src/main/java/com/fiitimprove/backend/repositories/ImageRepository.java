package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.Image;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByUserId(Long userId);
    Optional<Image> findTopByUserIdOrderByIdDesc(Long userId);
}