package com.fiitimprove.backend.repositories;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.models.User;
public interface CoachRepository extends JpaRepository<Coach, Long> {
    public Optional<Coach> findById(Long coachId);

    @Query(value = "SELECT c FROM coach c " +
        "WHERE data->>'role' = 'COACH' " + 
        "AND (:name is null OR levenshtein(LOWER(c.surname), LOWER(:name)) < 3) OR levenshtein(LOWER(c.name), LOWER(:name)) < 3) " + 
        "AND (:gender is null OR c.gender = :gender) " + 
        "AND (:field is null OR LOWER(:field) = ANY(LOWER(c.fields)) )", nativeQuery = true)
    public List<Coach> findCoachByNameOrSurname(
        @Param("name") String name,
        @Param("gender") User.Gender gender,
        @Param("field") String field
    );
}