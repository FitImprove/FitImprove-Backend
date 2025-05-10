package com.fiitimprove.backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter

public class MessageDTO {
    private Long id;
    private Long chatId;
    private Long senderId;
    private String senderRole;
    private String content;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private Boolean isRead;
}