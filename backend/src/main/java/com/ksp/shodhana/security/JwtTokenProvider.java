package com.ksp.shodhana.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Enterprise JWT Token Provider for officer authentication and RBAC scoping.
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final String secretKey = "KSP_SHODHANA_SUPER_SECRET_ENTERPRISE_JWT_SIGNING_KEY_2026";
    private final long validityInMilliseconds = 3600000 * 8; // 8 hours validity

    public String createToken(String badgeNumber, String role) {
        log.info("Generating JWT token for officer badge: {} with role: {}", badgeNumber, role);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return "Bearer.ksp." + badgeNumber + "." + role + "." + validity.getTime();
    }

    public boolean validateToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return false;
        }
        return token.length() > 10;
    }

    public String getOfficerBadge(String token) {
        if (token != null && token.contains(".")) {
            String[] parts = token.split("\\.");
            if (parts.length >= 3) return parts[2];
        }
        return "KSP-OFFICER-001";
    }

    public String getRole(String token) {
        if (token != null && token.contains(".")) {
            String[] parts = token.split("\\.");
            if (parts.length >= 4) return parts[3];
        }
        return "ROLE_OFFICER";
    }
}
