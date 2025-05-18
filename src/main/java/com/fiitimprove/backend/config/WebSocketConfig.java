package com.fiitimprove.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.util.Collections;

/**
 * Configuration class for setting up WebSocket support in the application.
 *
 * <p>This configuration enables WebSocket handling with Spring's
 * {@link EnableWebSocket} annotation and implements {@link WebSocketConfigurer}
 * to register WebSocket handlers.</p>
 *
 * <p>The {@code WebSocketConfig} registers the {@link ChatWebSocketHandler} at
 * the endpoint "/ws/chat/{chatId}", allowing clients to connect to chat-specific
 * WebSocket channels.</p>
 *
 * <p>Additionally, it defines beans to customize the WebSocket container's
 * buffer sizes and a handler mapping to route WebSocket requests.</p>
 * 
 * @see ChatWebSocketHandler
 * @see WebSocketConfigurer
 * @see ServletServerContainerFactoryBean
 */
@Configuration
@EnableWebSocket
@Profile("!test")
public class WebSocketConfig implements WebSocketConfigurer {
    private final ChatWebSocketHandler chatWebSocketHandler;

    /**
     * Constructs the WebSocket configuration with the given {@link ChatWebSocketHandler}.
     *
     * @param chatWebSocketHandler the WebSocket handler for chat messages
     */
    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler) {
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    /**
     * Registers WebSocket handlers with their corresponding URL endpoints.
     *
     * @param registry the registry to add WebSocket handlers to
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat/{chatId}")
                .setAllowedOrigins("*");
    }

    /**
     * Configures the WebSocket container with customized buffer sizes for
     * text and binary messages.
     *
     * @return a configured {@link ServletServerContainerFactoryBean}
     */
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }

    /**
     * Creates a {@link HandlerMapping} bean that maps WebSocket URL patterns
     * to the chat WebSocket handler with a specified order of precedence.
     *
     * @return the handler mapping for WebSocket requests
     */
    @Bean
    public HandlerMapping customWebSocketHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(1);
        mapping.setUrlMap(Collections.singletonMap("/ws/chat/**", chatWebSocketHandler));
        return mapping;
    }
}