package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.models.Chat;
import com.fiitimprove.backend.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/create")
    public ResponseEntity<Chat> createChat(@RequestParam Long coachId, @RequestParam Long regularUserId) {
        return ResponseEntity.ok(chatService.createChat(coachId, regularUserId));
    }

    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<Chat>> getChatsByCoachId(@PathVariable Long coachId) {
        return ResponseEntity.ok(chatService.findChatsByCoachId(coachId));
    }

    @GetMapping("/user/{regularUserId}")
    public ResponseEntity<List<Chat>> getChatsByRegularUserId(@PathVariable Long regularUserId) {
        return ResponseEntity.ok(chatService.findChatsByRegularUserId(regularUserId));
    }
}