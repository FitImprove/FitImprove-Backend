package com.fiitimprove.backend.services;

import com.fiitimprove.backend.dto.MessageConverter;
import com.fiitimprove.backend.dto.MessageDTO;
import com.fiitimprove.backend.models.Chat;
import com.fiitimprove.backend.models.Message;
import com.fiitimprove.backend.repositories.ChatRepository;
import com.fiitimprove.backend.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing chat messages, including saving, sending,
 * and retrieving messages within chats.
 */
@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private  ChatRepository chatRepository;
    @Autowired
    private ChatService chatService;

    /**
     * Saves a new message based on the provided DTO, associating it with an existing chat.
     *
     * @param dto Data transfer object containing message details.
     * @return The saved message converted back to a DTO.
     * @throws RuntimeException if the chat with the provided ID does not exist.
     */
    public MessageDTO saveMessage(MessageDTO dto) {
        Chat chat = chatRepository.findById(dto.getChatId())
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        Message message = new Message();
        message.setChat(chat);
        message.setSenderId(dto.getSenderId());
        message.setSenderRole(Message.SenderRole.valueOf(dto.getSenderRole()));
        message.setContent(dto.getContent());
        message.setSentAt(dto.getSentAt());
        message.setDeliveredAt(dto.getDeliveredAt());
        message.setRead(dto.getIsRead());
        Message saved = messageRepository.save(message);
        return MessageConverter.toDTO(saved);
    }

    /**
     * Sends a message in a specific chat by a given sender with the specified role and content.
     * Validates sender participation and role consistency in the chat.
     *
     * @param chatId     ID of the chat where the message is sent.
     * @param senderId   ID of the sender.
     * @param senderRole Role of the sender (COACH or USER).
     * @param content    Content of the message.
     * @return The saved Message entity.
     * @throws RuntimeException if the sender is not a participant in the chat or
     *                          if the sender's role does not match their identity in the chat.
     */
    public Message sendMessage(Long chatId, Long senderId, Message.SenderRole senderRole, String content) {
        Chat chat = chatService.findChatById(chatId);

        if (!senderId.equals(chat.getCoach().getId()) && !senderId.equals(chat.getRegularUser().getId())) {
            throw new RuntimeException("Sender is not a participant of this chat");
        }
        if (senderId.equals(chat.getCoach().getId()) && senderRole != Message.SenderRole.COACH) {
            throw new RuntimeException("Sender role does not match: expected COACH");
        }
        if (senderId.equals(chat.getRegularUser().getId()) && senderRole != Message.SenderRole.USER) {
            throw new RuntimeException("Sender role does not match: expected USER");
        }

        Message message = new Message();
        message.setChat(chat);
        message.setSenderId(senderId);
        message.setSenderRole(senderRole);
        message.setContent(content);
        message.setRead(false);

        chat.setUpdatedAt(java.time.LocalDateTime.now());
        return messageRepository.save(message);
    }

    /**
     * Retrieves all messages belonging to a specific chat.
     *
     * @param chatId ID of the chat.
     * @return List of messages associated with the chat.
     */
    public List<Message> findMessagesByChatId(Long chatId) {
        return messageRepository.findByChatId(chatId);
    }
}