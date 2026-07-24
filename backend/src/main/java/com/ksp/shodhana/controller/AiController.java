package com.ksp.shodhana.controller;

import com.ksp.shodhana.dto.request.AiQueryRequest;
import com.ksp.shodhana.dto.response.ApiResponse;
import com.ksp.shodhana.dto.response.WorkspacePayload;
import com.ksp.shodhana.exception.ShodhanaException;
import com.ksp.shodhana.security.AiRateLimiter;
import com.ksp.shodhana.service.AiGatewayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.Executors;

/**
 * Controller for AI-powered query processing & SSE Streaming.
 * Implements strict input validation and zero-dependency sliding window rate limiting.
 */
@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private static final Logger log = LoggerFactory.getLogger(AiController.class);
    private final AiGatewayService aiGatewayService;
    private final AiRateLimiter aiRateLimiter;

    public AiController(AiGatewayService aiGatewayService, AiRateLimiter aiRateLimiter) {
        this.aiGatewayService = aiGatewayService;
        this.aiRateLimiter = aiRateLimiter;
    }

    /**
     * Process a natural language query from the investigator.
     * Enforces rate limits (10 req/min/IP) and input validation via @Valid.
     */
    @PostMapping("/query")
    public ApiResponse<WorkspacePayload> processQuery(
            @Valid @RequestBody AiQueryRequest request,
            HttpServletRequest httpRequest) {

        String clientIp = getClientIp(httpRequest);
        if (!aiRateLimiter.tryAcquire(clientIp)) {
            throw new ShodhanaException("RATE_LIMIT_EXCEEDED", "Rate limit exceeded. Maximum 10 AI queries per minute allowed.");
        }

        log.info("AI query received from IP {}: {}", clientIp, request.getText());
        WorkspacePayload payload = aiGatewayService.processQuery(request);
        return ApiResponse.ok(payload);
    }

    /**
     * Real-time Server-Sent Events (SSE) streaming endpoint for token-by-token response streaming.
     * Enforces explicit query validation (non-blank, max 2000 chars) and IP rate limiting (10 req/min/IP).
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamQueryResponse(
            @RequestParam(value = "query", required = false) String query,
            HttpServletRequest httpRequest) {

        String clientIp = getClientIp(httpRequest);

        // 1. Input Validation Guard: Null or blank check
        if (query == null || query.isBlank()) {
            log.warn("Rejected SSE stream request from IP {}: query parameter is null or blank", clientIp);
            SseEmitter emitter = new SseEmitter(5000L);
            try {
                emitter.send(SseEmitter.event().name("error").data("Query text must not be blank."));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
            return emitter;
        }

        // 2. Input Validation Guard: Length cap check
        if (query.length() > 2000) {
            log.warn("Rejected SSE stream request from IP {}: query length {} exceeds 2000 character cap", clientIp, query.length());
            SseEmitter emitter = new SseEmitter(5000L);
            try {
                emitter.send(SseEmitter.event().name("error").data("Query text cannot exceed 2000 characters."));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
            return emitter;
        }

        // 3. Rate Limiting Guard: IP rate limit check
        if (!aiRateLimiter.tryAcquire(clientIp)) {
            log.warn("Rejected SSE stream request from IP {}: rate limit exceeded (10 req/min)", clientIp);
            SseEmitter emitter = new SseEmitter(5000L);
            try {
                emitter.send(SseEmitter.event().name("error").data("Rate limit exceeded. Maximum 10 AI queries per minute allowed."));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
            return emitter;
        }

        log.info("SSE Stream requested from IP {} for query: {}", clientIp, query);
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

    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "127.0.0.1";
        }
        String clientIp = "127.0.0.1";
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            clientIp = xForwardedFor.split(",")[0].trim();
        } else if (request.getRemoteAddr() != null) {
            clientIp = request.getRemoteAddr();
        }

        if ("0:0:0:0:0:0:0:1".equals(clientIp) || "::1".equals(clientIp)) {
            clientIp = "127.0.0.1";
        }
        return clientIp;
    }
}
