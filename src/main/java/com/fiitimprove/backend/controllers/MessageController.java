
package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.models.Message;
import com.fiitimprove.backend.security.SecurityUtil;
import com.fiitimprove.backend.services.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles endpoints related to messages
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    private SecurityUtil securityUtil;
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    @Operation(summary = "Send a message", description = "Sends a message in a specified chat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid message data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Chat or sender not found")
    })
    public ResponseEntity<Message> sendMessage(
            @RequestParam Long chatId,
            @RequestParam Long senderId,
            @RequestParam Message.SenderRole senderRole,
            @RequestBody @Valid Message message) {
        return ResponseEntity.ok(messageService.sendMessage(chatId, senderId, senderRole, message.getContent()));
    }

    @GetMapping("/chat/{chatId}")
    @Operation(summary = "Get messages by chat ID", description = "Retrieves all messages in a specified chat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Chat not found")
    })
    public ResponseEntity<List<Message>> getMessagesByChatId(@PathVariable Long chatId) {
        return ResponseEntity.ok(messageService.findMessagesByChatId(chatId));
    }
}