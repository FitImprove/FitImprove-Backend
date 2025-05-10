package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.dto.MessageDTO;
import com.fiitimprove.backend.exceptions.AccessDeniedException;
import com.fiitimprove.backend.models.Chat;
import com.fiitimprove.backend.security.SecurityUtil;
import com.fiitimprove.backend.services.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }



    @MessageMapping("/chat/{chatId}/send")
    public void sendMessage(@DestinationVariable Long chatId, MessageDTO messageDTO) {
//        chatService.sendMessage(
//                chatId,
//                messageDTO.getSenderId(),
//                messageDTO.getSenderRole(),
//                messageDTO.getContent()
//        );
    }

    @MessageMapping("/chat/{chatId}/read/{messageId}")
    public void markAsRead(@DestinationVariable Long chatId, @DestinationVariable Long messageId) {
        chatService.markMessageAsRead(messageId);
    }
}