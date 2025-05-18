package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
/**
 * Repository for {@link Message} that allows to make operations over db
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatId(Long chatId);
}