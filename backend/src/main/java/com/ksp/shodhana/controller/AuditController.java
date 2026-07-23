package com.ksp.shodhana.controller;

import com.ksp.shodhana.security.AuditLedgerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Enterprise Audit Controller.
 * Exposes read-only access to the WORM Immutable Cryptographic Audit Ledger.
 */
@RestController
@RequestMapping("/api/v1/audit")
@CrossOrigin(origins = "*")
public class AuditController {

    private final AuditLedgerService auditLedgerService;

    public AuditController(AuditLedgerService auditLedgerService) {
        this.auditLedgerService = auditLedgerService;
    }

    @GetMapping("/ledger")
    public ResponseEntity<Map<String, Object>> getAuditLedger() {
        Map<String, Object> response = new HashMap<>();
        response.put("ledger", auditLedgerService.getLedger());
        response.put("integrityVerified", auditLedgerService.verifyLedgerIntegrity());
        response.put("count", auditLedgerService.getLedger().size());
        return ResponseEntity.ok(response);
    }
}
