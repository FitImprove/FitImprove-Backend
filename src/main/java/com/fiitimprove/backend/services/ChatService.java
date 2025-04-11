package com.fiitimprove.backend.services;

import com.fiitimprove.backend.exceptions.ResourceNotFoundException;
import com.fiitimprove.backend.models.Chat;
import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.models.RegularUser;
import com.fiitimprove.backend.repositories.ChatRepository;
import com.fiitimprove.backend.repositories.CoachRepository;
import com.fiitimprove.backend.repositories.RegularUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private RegularUserRepository regularUserRepository;

    public Chat createChat(Long coachId, Long regularUserId) {
        // Перевіряємо, чи існує чат між цими користувачами
        Optional<Chat> existingChat = chatRepository.findByCoachIdAndRegularUserId(coachId, regularUserId);
        if (existingChat.isPresent()) {
            return existingChat.get();
        }
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach not found with id: " + coachId));
        RegularUser regularUser = regularUserRepository.findById(regularUserId)
                .orElseThrow(() -> new RuntimeException("RegularUser not found with id: " + regularUserId));
        Chat chat = new Chat();
        chat.setCoach(coach);
        chat.setRegularUser(regularUser);
        return chatRepository.save(chat);
    }

    public List<Chat> findChatsByCoachId(Long coachId) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found with id: " + coachId));
        return chatRepository.findByCoachId(coachId);
    }

    public List<Chat> findChatsByRegularUserId(Long regularUserId) {
        RegularUser regularUser = regularUserRepository.findById(regularUserId)
                .orElseThrow(() -> new ResourceNotFoundException("RegularUser not found with id: " + regularUserId));
        return chatRepository.findByRegularUserId(regularUserId);
    }

    public Chat findChatById(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found with id: " + chatId));
    }

}