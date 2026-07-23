package com.ksp.shodhana.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuditLedgerServiceTest {

    @Autowired
    private AuditLedgerService auditLedgerService;

    @Test
    @DisplayName("Verify Cryptographic WORM Audit Hash Chaining")
    public void testAuditLedgerHashChainingIntegrity() {
        AuditLedgerService.AuditEntry entry1 = auditLedgerService.recordEvent(
                "OFFICER-101", "KSP-BADGE-101", "QUERY", "KA/2026/00101", "127.0.0.1"
        );

        AuditLedgerService.AuditEntry entry2 = auditLedgerService.recordEvent(
                "OFFICER-102", "KSP-BADGE-102", "EXPORT", "REPORT-101", "127.0.0.1"
        );

        assertNotNull(entry1.getCurrentHash());
        assertNotNull(entry2.getCurrentHash());
        assertEquals(entry1.getCurrentHash(), entry2.getPreviousHash());

        assertTrue(auditLedgerService.verifyLedgerIntegrity());
    }
}
