package com.fiitimprove.backend.controllers;

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
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    @Autowired
    private SecurityUtil securityUtil;
    @PostMapping("/create")
    @Operation(summary = "Create a chat", description = "Creates a chat between a coach and a regular user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid coach or user ID"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach or user not found")
    })
    public ResponseEntity<Chat> createChat(@RequestParam Long coachId, @RequestParam Long regularUserId) {
        Long currentUserId = securityUtil.getCurrentUserId();
        if (!currentUserId.equals(regularUserId)) {
            throw new AccessDeniedException("Only the regular user can create the chat");
        }
        return ResponseEntity.ok(chatService.createChat(coachId, regularUserId));
    }

    @GetMapping("/coach/{coachId}")
    @Operation(summary = "Get chats by coach ID", description = "Retrieves all chats for a specified coach")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chats retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach not found")
    })
    public ResponseEntity<List<Chat>> getChatsByCoachId(@PathVariable Long coachId) {
        Long currentUserId = securityUtil.getCurrentUserId();
        if (!currentUserId.equals(coachId)) {
            throw new AccessDeniedException("You can only access your own chats");
        }
        return ResponseEntity.ok(chatService.findChatsByCoachId(coachId));
    }

    @GetMapping("/user/{regularUserId}")
    @Operation(summary = "Get chats by regular user ID", description = "Retrieves all chats for a specified regular user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chats retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<Chat>> getChatsByRegularUserId(@PathVariable Long regularUserId) {
        Long currentUserId = securityUtil.getCurrentUserId();
        if (!currentUserId.equals(regularUserId)) {
            throw new AccessDeniedException("You can only access your own chats");
        }
        return ResponseEntity.ok(chatService.findChatsByRegularUserId(regularUserId));
    }
}