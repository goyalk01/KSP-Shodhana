package com.ksp.shodhana.security;

import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public void initSeedLedger() {
        log.info("Initializing WORM Cryptographic Audit Ledger with seed logs...");
        recordEvent("KSP-OFF-402", "KA-POL-8821", "SYSTEM_STARTUP", "VAULT_BOOT", "127.0.0.1");
        recordEvent("KSP-OFF-109", "KA-POL-1092", "DOSSIER_ACCESS", "KA-CR-2024-001", "10.0.4.12");
        recordEvent("KSP-SUP-001", "KA-POL-0001", "SPATIAL_RADIUS_QUERY", "BANGALORE_CENTRAL", "10.0.1.5");
    }

    public synchronized AuditEntry recordEvent(String officerId, String badgeNumber, String action, String resourceId, String clientIp) {
        String timestamp = Instant.now().toString();
        String rawData = lastHash + "|" + timestamp + "|" + officerId + "|" + badgeNumber + "|" + action + "|" + resourceId + "|" + clientIp;
        String currentHash = calculateSha256(rawData);

        AuditEntry entry = new AuditEntry();
        entry.setSequenceNumber(ledger.size() + 1);
        entry.setTimestamp(timestamp);
        entry.setOfficerId(officerId);
        entry.setBadgeNumber(badgeNumber);
        entry.setAction(action);
        entry.setResourceId(resourceId);
        entry.setClientIp(clientIp);
        entry.setPreviousHash(lastHash);
        entry.setCurrentHash(currentHash);

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

        log.info("WORM Audit Ledger Integrity Verified: {} entries valid", ledger.size());
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
        private long sequenceNumber;
        private String timestamp;
        private String officerId;
        private String badgeNumber;
        private String action;
        private String resourceId;
        private String clientIp;
        private String previousHash;
        private String currentHash;

        public AuditEntry() {}

        public long getSequenceNumber() { return sequenceNumber; }
        public void setSequenceNumber(long sequenceNumber) { this.sequenceNumber = sequenceNumber; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

        public String getOfficerId() { return officerId; }
        public void setOfficerId(String officerId) { this.officerId = officerId; }

        public String getBadgeNumber() { return badgeNumber; }
        public void setBadgeNumber(String badgeNumber) { this.badgeNumber = badgeNumber; }

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }

        public String getResourceId() { return resourceId; }
        public void setResourceId(String resourceId) { this.resourceId = resourceId; }

        public String getClientIp() { return clientIp; }
        public void setClientIp(String clientIp) { this.clientIp = clientIp; }

        public String getPreviousHash() { return previousHash; }
        public void setPreviousHash(String previousHash) { this.previousHash = previousHash; }

        public String getCurrentHash() { return currentHash; }
        public void setCurrentHash(String currentHash) { this.currentHash = currentHash; }
    }
}
