package com.ksp.shodhana.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cryptographic Immutable WORM Audit Ledger Service.
 * Implements SHA-256 Hash Chaining across all audit log entries:
 * Hash_N = SHA-256(Hash_{N-1} + Timestamp + OfficerID + Action)
 */
@Service
public class AuditLedgerService {

    private static final Logger log = LoggerFactory.getLogger(AuditLedgerService.class);
    private final List<AuditEntry> ledger = Collections.synchronizedList(new ArrayList<>());
    private String lastHash = "GENESIS_HASH_KSP_SHODHANA_2026_VAULT";

    public synchronized AuditEntry recordEvent(String officerId, String badgeNumber, String action, String resourceId, String clientIp) {
        String timestamp = Instant.now().toString();
        String rawData = lastHash + "|" + timestamp + "|" + officerId + "|" + badgeNumber + "|" + action + "|" + resourceId + "|" + clientIp;
        String currentHash = calculateSha256(rawData);

        AuditEntry entry = new AuditEntry(
                ledger.size() + 1,
                timestamp,
                officerId,
                badgeNumber,
                action,
                resourceId,
                clientIp,
                lastHash,
                currentHash
        );

        ledger.add(entry);
        lastHash = currentHash;

        log.info("WORM Audit Event Record #{} [{}] Hash: {}", entry.getSequenceNumber(), action, currentHash);
        return entry;
    }

    public List<AuditEntry> getLedger() {
        return Collections.unmodifiableList(new ArrayList<>(ledger));
    }

    public synchronized boolean verifyLedgerIntegrity() {
        String expectedPreviousHash = "GENESIS_HASH_KSP_SHODHANA_2026_VAULT";

        for (AuditEntry entry : ledger) {
            if (!entry.getPreviousHash().equals(expectedPreviousHash)) {
                log.error("WORM Ledger Tampering Detected at Sequence #{}", entry.getSequenceNumber());
                return false;
            }

            String rawData = entry.getPreviousHash() + "|" + entry.getTimestamp() + "|" + entry.getOfficerId() + "|" +
                    entry.getBadgeNumber() + "|" + entry.getAction() + "|" + entry.getResourceId() + "|" + entry.getClientIp();

            String computedHash = calculateSha256(rawData);
            if (!computedHash.equals(entry.getCurrentHash())) {
                log.error("WORM Ledger Hash Mismatch at Sequence #{}", entry.getSequenceNumber());
                return false;
            }

            expectedPreviousHash = entry.getCurrentHash();
        }

        log.info("WORM Audit Ledger Integrity Verified: %d entries valid", ledger.size());
        return true;
    }

    private String calculateSha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 Digest Error", e);
        }
    }

    public static class AuditEntry {
        private final long sequenceNumber;
        private final String timestamp;
        private final String officerId;
        private final String badgeNumber;
        private final String action;
        private final String resourceId;
        private final String clientIp;
        private final String previousHash;
        private final String currentHash;

        public AuditEntry(long sequenceNumber, String timestamp, String officerId, String badgeNumber, String action, String resourceId, String clientIp, String previousHash, String currentHash) {
            this.sequenceNumber = sequenceNumber;
            this.timestamp = timestamp;
            this.officerId = officerId;
            this.badgeNumber = badgeNumber;
            this.action = action;
            this.resourceId = resourceId;
            this.clientIp = clientIp;
            this.previousHash = previousHash;
            this.currentHash = currentHash;
        }

        public long getSequenceNumber() { return sequenceNumber; }
        public String getTimestamp() { return timestamp; }
        public String getOfficerId() { return officerId; }
        public String getBadgeNumber() { return badgeNumber; }
        public String getAction() { return action; }
        public String getResourceId() { return resourceId; }
        public String getClientIp() { return clientIp; }
        public String getPreviousHash() { return previousHash; }
        public String getCurrentHash() { return currentHash; }
    }
}
