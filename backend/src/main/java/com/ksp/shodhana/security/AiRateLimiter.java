package com.ksp.shodhana.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Zero-dependency In-Memory Rate Limiter for AI Query & SSE Streaming Endpoints.
 * Enforces a sliding window limit (10 requests per minute per IP) to protect Gemini AI gateway from abuse and cost overruns.
 */
@Component
public class AiRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(AiRateLimiter.class);

    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> windowStartTimes = new ConcurrentHashMap<>();

    private static final int MAX_REQUESTS_PER_MINUTE = 10;
    private static final long WINDOW_MS = 60 * 1000L; // 1 minute window

    public boolean tryAcquire(String clientIp) {
        long now = System.currentTimeMillis();
        long windowStart = windowStartTimes.computeIfAbsent(clientIp, k -> now);

        if (now - windowStart > WINDOW_MS) {
            windowStartTimes.put(clientIp, now);
            requestCounts.put(clientIp, new AtomicInteger(1));
            return true;
        }

        int count = requestCounts.computeIfAbsent(clientIp, k -> new AtomicInteger(0)).incrementAndGet();
        if (count > MAX_REQUESTS_PER_MINUTE) {
            log.warn("AI Rate limit exceeded for IP {}: {} requests in current 1m window (max: {})", clientIp, count, MAX_REQUESTS_PER_MINUTE);
            return false;
        }

        return true;
    }

    public void reset(String clientIp) {
        requestCounts.remove(clientIp);
        windowStartTimes.remove(clientIp);
    }
}
