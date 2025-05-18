package com.fiitimprove.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.fiitimprove.backend.services.MockWebSocketConfig;

/**
 * Starting point for tests of backend
 */
@ActiveProfiles("test")
@SpringBootTest(
    classes = {FitImproveApplication.class},
    webEnvironment = WebEnvironment.RANDOM_PORT
)
@Import(MockWebSocketConfig.class)
class FitImproveApplicationTests {
    @Test
    void contextLoads() {
    }
}
