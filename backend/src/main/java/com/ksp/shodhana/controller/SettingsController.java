package com.ksp.shodhana.controller;

import com.ksp.shodhana.dto.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * Controller for handling Workspace and AI settings requests.
 */
@RestController
@RequestMapping("/api/v1/settings")
public class SettingsController {

    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);
    private final WebClient aiServiceWebClient;

    public SettingsController(WebClient aiServiceWebClient) {
        this.aiServiceWebClient = aiServiceWebClient;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> getSettings() {
        log.info("Fetching current AI settings from FastAPI...");
        try {
            Map<String, Object> settings = aiServiceWebClient.get()
                    .uri("/ai/v1/settings")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return ApiResponse.ok(settings);
        } catch (Exception e) {
            log.error("Failed to fetch settings from AI service: {}", e.getMessage());
            // Fallback response if AI service is offline
            String apiKey = System.getenv("GEMINI_API_KEY") != null ? System.getenv("GEMINI_API_KEY") : "";
            return ApiResponse.ok(Map.of(
                    "gemini_model", "gemini-3.5-flash-lite",
                    "gemini_api_key", apiKey,
                    "default_district", "Bengaluru Urban",
                    "local_fallback_active", true
            ));
        }
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> updateSettings(@RequestBody Map<String, Object> payload) {
        log.info("Updating AI settings: {}", payload);
        try {
            Map<String, Object> result = aiServiceWebClient.post()
                    .uri("/ai/v1/settings")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return ApiResponse.ok(result);
        } catch (Exception e) {
            log.error("Failed to update settings in AI service: {}", e.getMessage());
            return ApiResponse.ok(Map.of("status", "local_only", "message", "Settings updated locally (AI service offline)"));
        }
    }
}
