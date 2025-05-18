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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class provides a functionality to
 * <ul>
 *  <li>create new message</li>
 *  <li>get chat history</li>
 *  <li>create chat</li>
 *  <li>get list of chats</li>
 * </ul>
 */
@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private CoachRepository coachRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private RegularUserRepository regularUserRepository;

    /**
     * Sends a message in a chat.
     *
     * @param chatId     ID of the chat to send the message in
     * @param senderId   ID of the sender (coach or regular user)
     * @param senderRole Role of the sender ("COACH" or "REGULAR_USER")
     * @param content    Text content of the message
     * @return The saved {@link MessageDTO} representing the message
     * @throws RuntimeException if the chat does not exist
     */
    public MessageDTO sendMessage(Long chatId, Long senderId, String senderRole, String content) {
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

    /**
     * Retrieves all messages for a specific chat.
     *
     * @param chatId ID of the chat
     * @return List of {@link MessageDTO} containing all chat messages
     * @throws RuntimeException if the chat does not exist
     */
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

    /**
     * Marks a message as read.
     *
     * @param messageId ID of the message to mark as read
     * @throws RuntimeException if the message does not exist
     */
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

    /**
     * Creates a new chat between a coach and a regular user, if it doesn't already exist.
     *
     * @param coachId       ID of the coach
     * @param regularUserId ID of the regular user
     * @return The created or existing {@link Chat} object
     * @throws RuntimeException if the coach or regular user is not found
     */
    public Chat createChat(Long coachId, Long regularUserId) {

        Optional<Chat> existingChat = chatRepository.findByCoachIdAndRegularUserId(coachId, regularUserId);
        if (existingChat.isPresent()) {
            System.out.println("chat already exist");
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

    /**
     * Retrieves all chats for a given coach.
     *
     * @param coachId ID of the coach
     * @return List of {@link Chat} associated with the coach
     * @throws ResourceNotFoundException if the coach does not exist
     */
    public List<Chat> findChatsByCoachId(Long coachId) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found with id: " + coachId));
        return chatRepository.findByCoachId(coachId);
    }

    /**
     * Retrieves all chats for a given regular user.
     *
     * @param regularUserId ID of the regular user
     * @return List of {@link Chat} associated with the regular user
     * @throws ResourceNotFoundException if the regular user does not exist
     */
    public List<Chat> findChatsByRegularUserId(Long regularUserId) {
        RegularUser regularUser = regularUserRepository.findById(regularUserId)
                .orElseThrow(() -> new ResourceNotFoundException("RegularUser not found with id: " + regularUserId));
        return chatRepository.findByRegularUserId(regularUserId);
    }

    /**
    * Retrieves a chat by its ID.
    *
    * @param chatId ID of the chat
    * @return The {@link Chat} object
    * @throws RuntimeException if the chat is not found
    */
    public Chat findChatById(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found with id: " + chatId));
    }

    /**
     * Checks whether a chat exists between the current user and a coach or regular user.
     *
     * @param coachId       ID of the coach (nullable)
     * @param userId        ID of the regular user (nullable)
     * @param currentUserId ID of the current user performing the check
     * @return {@code true} if a chat exists, {@code false} otherwise
     */
    public boolean checkChatExists(Long coachId, Long userId, Long currentUserId) {
        List<Chat> chats;
        if (coachId != null && userId == null) {

            chats = findChatsByRegularUserId(currentUserId);
            return chats.stream().anyMatch(chat -> chat.getCoach().getId().equals(coachId));
        } else if (userId != null && coachId == null) {

            chats = findChatsByCoachId(currentUserId);
            return chats.stream().anyMatch(chat -> chat.getRegularUser().getId().equals(userId));
        }
        return false;
    }
}