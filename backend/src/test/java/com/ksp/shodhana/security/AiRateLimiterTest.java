package com.ksp.shodhana.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AiRateLimiterTest {

    private AiRateLimiter aiRateLimiter;

    @BeforeEach
    void setUp() {
        aiRateLimiter = new AiRateLimiter();
    }

    @Test
    void testAllowsUpToTenRequestsPerMinute() {
        String clientIp = "192.168.1.100";
        for (int i = 1; i <= 10; i++) {
            assertTrue(aiRateLimiter.tryAcquire(clientIp), "Request " + i + " should be allowed");
        }
    }

    @Test
    void testBlocksEleventhRequestInSameWindow() {
        String clientIp = "192.168.1.101";
        for (int i = 1; i <= 10; i++) {
            aiRateLimiter.tryAcquire(clientIp);
        }

        assertFalse(aiRateLimiter.tryAcquire(clientIp), "11th request should be rate limited");
    }

    @Test
    void testIsolatesDifferentClientIPs() {
        String clientIpA = "10.0.0.1";
        String clientIpB = "10.0.0.2";

        for (int i = 1; i <= 10; i++) {
            aiRateLimiter.tryAcquire(clientIpA);
        }

        assertFalse(aiRateLimiter.tryAcquire(clientIpA), "Client A should be blocked");
        assertTrue(aiRateLimiter.tryAcquire(clientIpB), "Client B should still be allowed");
    }

    @Test
    void testResetClearsRateLimitState() {
        String clientIp = "192.168.1.102";
        for (int i = 1; i <= 10; i++) {
            aiRateLimiter.tryAcquire(clientIp);
        }
        assertFalse(aiRateLimiter.tryAcquire(clientIp));

        aiRateLimiter.reset(clientIp);
        assertTrue(aiRateLimiter.tryAcquire(clientIp), "Request after reset should be allowed");
    }
}
