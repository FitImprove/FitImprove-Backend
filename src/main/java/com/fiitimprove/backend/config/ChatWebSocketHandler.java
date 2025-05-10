package com.fiitimprove.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fiitimprove.backend.dto.MessageDTO;
import com.fiitimprove.backend.services.MessageService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<Long, Map<String, WebSocketSession>> chatSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final MessageService messageService;
    public ChatWebSocketHandler(MessageService messageService) {
        this.objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        this.objectMapper.registerModule(module);
        this.messageService = messageService;
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String chatId = extractChatId(session);
        if (chatId != null) {
            Long parsedChatId = Long.parseLong(chatId);
            chatSessions.computeIfAbsent(parsedChatId, k -> new ConcurrentHashMap<>())
                    .put(session.getId(), session);
            System.out.println("Connected to chat " + parsedChatId + ", session: " + session.getId());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String chatId = extractChatId(session);
        if (chatId == null) return;

        Long parsedChatId = Long.parseLong(chatId);
        String payload = message.getPayload();
        System.out.println("Received message for chat " + parsedChatId + ": " + payload);

        MessageDTO messageDTO = objectMapper.readValue(payload, MessageDTO.class);
        messageDTO.setSentAt(LocalDateTime.now());
        messageDTO.setDeliveredAt(LocalDateTime.now());
        messageDTO.setIsRead(false);
        MessageDTO savedMessage = messageService.saveMessage(messageDTO);

        Map<String, WebSocketSession> sessions = chatSessions.get(parsedChatId);
        if (sessions != null) {
            String response = objectMapper.writeValueAsString(savedMessage);
            for (WebSocketSession s : sessions.values()) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(response));
                }
            }
        }
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String chatId = extractChatId(session);
        if (chatId != null) {
            Long parsedChatId = Long.parseLong(chatId);
            Map<String, WebSocketSession> sessions = chatSessions.get(parsedChatId);
            if (sessions != null) {
                sessions.remove(session.getId());
                if (sessions.isEmpty()) {
                    chatSessions.remove(parsedChatId);
                }
            }
            System.out.println("Disconnected from chat " + parsedChatId + ", session: " + session.getId());
        }
    }
    private String extractChatId(WebSocketSession session) {
        String uri = session.getUri().toString();
        String[] parts = uri.split("/ws/chat/");
        return parts.length > 1 ? parts[1] : null;
    }
}