package com.ksp.shodhana.controller;

import com.ksp.shodhana.dto.response.ApiResponse;
import com.ksp.shodhana.service.ReportService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for report generation and preview.
 */
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Generate an intelligence report for an investigation.
     * Returns a report ID that can be used for download or preview.
     */
    @PostMapping("/generate")
    public ApiResponse<Map<String, String>> generateReport(
            @RequestParam Long investigationId) {
        // Use investigationId directly as the report identifier for the MVP
        reportService.generateReport(investigationId);
        String reportId = String.valueOf(investigationId);
        return ApiResponse.ok(Map.of("reportId", reportId, "status", "generated"));
    }

    /** Download link mapper */
    @GetMapping("/{reportId}/download")
    public ApiResponse<Map<String, String>> downloadReport(@PathVariable String reportId) {
        String downloadUrl = reportService.getDownloadUrl(reportId);
        return ApiResponse.ok(Map.of("url", downloadUrl));
    }

    /** Serve print-ready case dossier HTML directly to browser / iframe */
    @GetMapping(value = "/{reportId}/preview", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String previewReport(@PathVariable String reportId) {
        try {
            Long investigationId = Long.parseLong(reportId);
            return reportService.generateReport(investigationId);
        } catch (NumberFormatException e) {
            // Default to investigation 1
            return reportService.generateReport(1L);
        }
    }
}
