package com.flowai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class FlowAiApplicationTests {

    @Test
    void contextLoads() {
        // If the application context starts without errors, this test passes.
        // This is your first line of defense against misconfiguration.
    }
}