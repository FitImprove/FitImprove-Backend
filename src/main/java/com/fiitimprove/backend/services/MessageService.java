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

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private  ChatRepository chatRepository;
    @Autowired
    private ChatService chatService;



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

    public List<Message> findMessagesByChatId(Long chatId) {
        return messageRepository.findByChatId(chatId);
    }

}