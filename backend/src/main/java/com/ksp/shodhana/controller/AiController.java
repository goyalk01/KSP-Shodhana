package com.ksp.shodhana.controller;

import com.ksp.shodhana.dto.request.AiQueryRequest;
import com.ksp.shodhana.dto.response.ApiResponse;
import com.ksp.shodhana.dto.response.WorkspacePayload;
import com.ksp.shodhana.service.AiGatewayService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for AI-powered query processing.
 * This is the primary endpoint investigators interact with.
 */
@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private static final Logger log = LoggerFactory.getLogger(AiController.class);
    private final AiGatewayService aiGatewayService;

    public AiController(AiGatewayService aiGatewayService) {
        this.aiGatewayService = aiGatewayService;
    }

    /**
     * Process a natural language query from the investigator.
     * Returns a workspace payload that drives all UI panels.
     */
    @PostMapping("/query")
    public ApiResponse<WorkspacePayload> processQuery(@Valid @RequestBody AiQueryRequest request) {
        log.info("AI query received: {}", request.getText());
        WorkspacePayload payload = aiGatewayService.processQuery(request);
        return ApiResponse.ok(payload);
    }
}
