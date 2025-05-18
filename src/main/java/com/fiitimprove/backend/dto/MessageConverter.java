package com.fiitimprove.backend.dto;

import com.fiitimprove.backend.models.Message;

/**
 * Convertor of Message to MessageDTO
 */
public class MessageConverter {
    public static MessageDTO toDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setChatId(message.getChat().getId());
        dto.setSenderId(message.getSenderId());
        dto.setSenderRole(message.getSenderRole().name());
        dto.setContent(message.getContent());
        dto.setSentAt(message.getSentAt());
        dto.setDeliveredAt(message.getDeliveredAt());
        dto.setIsRead(message.isRead());
        return dto;
    }
}