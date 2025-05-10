package com.fiitimprove.backend.services;

import com.fiitimprove.backend.dto.MessageDTO;
import com.fiitimprove.backend.exceptions.ResourceNotFoundException;
import com.fiitimprove.backend.models.Chat;
import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.models.Message;
import com.fiitimprove.backend.models.RegularUser;
import com.fiitimprove.backend.repositories.ChatRepository;
import com.fiitimprove.backend.repositories.CoachRepository;
import com.fiitimprove.backend.repositories.MessageRepository;
import com.fiitimprove.backend.repositories.RegularUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private CoachRepository coachRepository;
    @Autowired
    private MessageRepository messageRepository;

//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RegularUserRepository regularUserRepository;
    public MessageDTO sendMessage(Long chatId, Long senderId, String senderRole, String content) {
        // Знаходимо чат
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        Message message = new Message();
        message.setChat(chat);
        message.setSenderId(senderId);
        message.setSenderRole(Message.SenderRole.valueOf(senderRole));
        message.setContent(content);
//        message.setSentAt(LocalDateTime.now());
//        message.setDeliveredAt(LocalDateTime.now());
//        message.setRead(false);

        message = messageRepository.save(message);

        chat.setUpdatedAt(LocalDateTime.now());
        chatRepository.save(chat);

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(message.getId());
        messageDTO.setChatId(chatId);
        messageDTO.setSenderId(senderId);
        messageDTO.setSenderRole(senderRole);
        messageDTO.setContent(content);
//        messageDTO.setSentAt(message.getSentAt());
//        messageDTO.setDeliveredAt(message.getDeliveredAt());
//        messageDTO.setRead(message.isRead());

       // messagingTemplate.convertAndSend("/topic/chat/" + chatId, messageDTO);

        return messageDTO;
    }
    public List<MessageDTO> getChatMessages(Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        return chat.getMessages().stream().map(message -> {
            MessageDTO dto = new MessageDTO();
            dto.setId(message.getId());
            dto.setChatId(chatId);
            dto.setSenderId(message.getSenderId());
            dto.setSenderRole(message.getSenderRole().toString());
            dto.setContent(message.getContent());
            dto.setSentAt(message.getSentAt());
            dto.setDeliveredAt(message.getDeliveredAt());
            dto.setIsRead(message.isRead());
            return dto;
        }).collect(Collectors.toList());
    }
    public void markMessageAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setRead(true);
        messageRepository.save(message);

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(message.getId());
        messageDTO.setChatId(message.getChat().getId());
        messageDTO.setSenderId(message.getSenderId());
        messageDTO.setSenderRole(message.getSenderRole().toString());
        messageDTO.setContent(message.getContent());
//        messageDTO.setSentAt(message.getSentAt());
//        messageDTO.setDeliveredAt(message.getDeliveredAt());
//        messageDTO.setRead(true);

        //messagingTemplate.convertAndSend("/topic/chat/" + message.getChat().getId(), messageDTO);
    }
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