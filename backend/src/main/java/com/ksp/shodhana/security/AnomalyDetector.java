package com.ksp.shodhana.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bulk Export Anomaly Detector & Anti-Exfiltration Rate Limiter.
 * Monitors query frequencies per officer account and locks sessions if >20 export/query requests occur within 5 minutes.
 */
@Component
public class AnomalyDetector {

    private static final Logger log = LoggerFactory.getLogger(AnomalyDetector.class);

    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> lastResetTime = new ConcurrentHashMap<>();
    private final Map<String, Boolean> lockedAccounts = new ConcurrentHashMap<>();

    private static final int MAX_REQUESTS_PER_WINDOW = 20;
    private static final long WINDOW_MS = 5 * 60 * 1000L; // 5 minutes

    public boolean isAllowed(String badgeNumber) {
        if (lockedAccounts.getOrDefault(badgeNumber, false)) {
            log.warn("Blocked request from LOCKED account: {}", badgeNumber);
            return false;
        }

        long now = System.currentTimeMillis();
        long resetTime = lastResetTime.getOrDefault(badgeNumber, now);

        if (now - resetTime > WINDOW_MS) {
            requestCounts.put(badgeNumber, new AtomicInteger(1));
            lastResetTime.put(badgeNumber, now);
            return true;
        }

        int count = requestCounts.computeIfAbsent(badgeNumber, k -> new AtomicInteger(0)).incrementAndGet();
        if (count > MAX_REQUESTS_PER_WINDOW) {
            lockedAccounts.put(badgeNumber, true);
            log.error("ANOMALY DETECTED: Account {} locked due to excessive query/export frequency ({} requests in 5m)", badgeNumber, count);
            return false;
        }

        return true;
    }

    public void unlockAccount(String badgeNumber) {
        lockedAccounts.remove(badgeNumber);
        requestCounts.remove(badgeNumber);
        log.info("Account {} unlocked by SSO admin override", badgeNumber);
    }
}
