package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Chat} chats that allows to make operations over db
 */
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByCoachIdAndRegularUserId(Long coachId, Long regularUserId);
    List<Chat> findByCoachId(Long coachId);
    List<Chat> findByRegularUserId(Long regularUserId);
}