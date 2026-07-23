package com.ksp.shodhana.controller;

import com.ksp.shodhana.dto.request.AiQueryRequest;
import com.ksp.shodhana.dto.response.ApiResponse;
import com.ksp.shodhana.dto.response.WorkspacePayload;
import com.ksp.shodhana.service.AiGatewayService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.Executors;

/**
 * Controller for AI-powered query processing & SSE Streaming.
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

    /**
     * Real-time Server-Sent Events (SSE) streaming endpoint for token-by-token response streaming.
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamQueryResponse(@RequestParam("query") String query) {
        log.info("SSE Stream requested for query: {}", query);
        SseEmitter emitter = new SseEmitter(60000L); // 60-second timeout

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                WorkspacePayload payload = aiGatewayService.processQuery(AiQueryRequest.builder().text(query).build());
                String message = payload.getMessage() != null ? payload.getMessage() : "Analysis complete.";
                
                String[] words = message.split(" ");
                for (String word : words) {
                    emitter.send(word + " ");
                    Thread.sleep(50); // 50ms token delay for smooth typing animation
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}
