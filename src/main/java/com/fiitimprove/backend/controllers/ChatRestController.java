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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatRestController {

    @Autowired
    private ChatService chatService;
    @Autowired
    private SecurityUtil securityUtil;
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<MessageDTO>> getChatMessages(@PathVariable Long chatId) {
        List<MessageDTO> messages = chatService.getChatMessages(chatId);
        return ResponseEntity.ok(messages);
    }
    @GetMapping("/{chatId}")
    public ResponseEntity<Chat> getChatInfo(@PathVariable Long chatId) {
        Chat chat = chatService.findChatById(chatId);
        return ResponseEntity.ok(chat);
    }
    @PostMapping("/create")
    @Operation(summary = "Create a chat", description = "Creates a chat between a coach and a regular user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid coach or user ID"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach or user not found")
    })
    public ResponseEntity<Chat> createChat(@RequestParam Long coachId) {
        System.out.println("si=om tu");
        Long currentUserId = securityUtil.getCurrentUserId();
        return ResponseEntity.ok(chatService.createChat(coachId, currentUserId));
    }

    @GetMapping("/coach")
    @Operation(summary = "Get chats by coach ID", description = "Retrieves all chats for a specified coach")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chats retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach not found")
    })
    public ResponseEntity<List<Chat>> getChatsByCoachId() {
        Long currentUserId = securityUtil.getCurrentUserId();

        return ResponseEntity.ok(chatService.findChatsByCoachId(currentUserId));
    }

    @GetMapping("/user")
    @Operation(summary = "Get chats by regular user ID", description = "Retrieves all chats for a specified regular user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chats retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<Chat>> getChatsByRegularUserId() {
        Long currentUserId = securityUtil.getCurrentUserId();

        return ResponseEntity.ok(chatService.findChatsByRegularUserId(currentUserId));
    }
    @GetMapping("/exists")
    @Operation(summary = "Check if chat exists", description = "Checks if a chat exists between the specified coach and user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat existence checked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters: at least one of coachId or userId must be provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach or user not found")
    })
    public ResponseEntity<Boolean> checkChatExists(
            @RequestParam(required = false) Long coachId,
            @RequestParam(required = false) Long userId
    ) {
        if (coachId == null && userId == null) {
            return ResponseEntity.badRequest().body(false);
        }
        Long currentUserId = securityUtil.getCurrentUserId();
        boolean exists = chatService.checkChatExists(coachId, userId, currentUserId);
        return ResponseEntity.ok(exists);
    }
}