package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.models.Message;
import com.fiitimprove.backend.services.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(
            @RequestParam Long chatId,
            @RequestParam Long senderId,
            @RequestParam Message.SenderRole senderRole,
            @RequestBody @Valid Message message) {
        return ResponseEntity.ok(messageService.sendMessage(chatId, senderId, senderRole, message.getContent()));
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<Message>> getMessagesByChatId(@PathVariable Long chatId) {
        return ResponseEntity.ok(messageService.findMessagesByChatId(chatId));
    }

}