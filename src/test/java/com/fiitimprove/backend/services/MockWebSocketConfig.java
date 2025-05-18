package com.fiitimprove.backend.services;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import jakarta.websocket.server.ServerContainer;

/**
 * That class ensures that WebSockets wont be activated/create during service testing, because of conflicts and
 * sockets beeing not setted up for test build 
 */
@TestConfiguration
public class MockWebSocketConfig {
    @Bean
    public ServerContainer createWebSocketContainer() {
        return Mockito.mock(ServerContainer.class);
    }
}