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

/**
 * WebSocket handler that manages real-time chat messaging between clients' applications
 */
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<Long, Map<String, WebSocketSession>> chatSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final MessageService messageService;

    /**
     * Constructs a new ChatWebSocketHandler with the provided {@link MessageService}.
     * Initializes the {@link ObjectMapper} with support for Java 8 date/time types.
     *
     * @param messageService the service used to save chat messages
     */
    public ChatWebSocketHandler(MessageService messageService) {
        this.objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        this.objectMapper.registerModule(module);
        this.messageService = messageService;
    }

    /**
     * Called when a new WebSocket connection is established.
     * Extracts the chat ID from the session URI and adds the session to the chatSessions map.
     *
     * @param session the new WebSocket session
     * @throws Exception if an error occurs while processing the connection
     */
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

    /**
     * Handles incoming text messages from clients.
     * Deserializes the message JSON into {@link MessageDTO}, sets timestamps and read status,
     * saves it via {@link MessageService}, and broadcasts the saved message to all sessions
     * connected to the same chat.
     *
     * @param session the WebSocket session that sent the message
     * @param message the incoming text message
     * @throws Exception if an error occurs during message handling
     */
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

    /**
     * Called when a WebSocket connection is closed.
     * Removes the session from the chatSessions map and cleans up if no sessions remain for the chat.
     *
     * @param session the WebSocket session that was closed
     * @param status the status of the connection close
     * @throws Exception if an error occurs during disconnection handling
     */
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
    
    /**
     * Extracts the chat ID from the WebSocket session URI.
     * Assumes the URI contains "/ws/chat/{chatId}".
     *
     * @param session the WebSocket session
     * @return the chat ID string if found, or null otherwise
     */
    private String extractChatId(WebSocketSession session) {
        String uri = session.getUri().toString();
        String[] parts = uri.split("/ws/chat/");
        return parts.length > 1 ? parts[1] : null;
    }
}