package com.ksp.shodhana.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request body for POST /api/v1/ai/query.
 * Sent by the frontend when the investigator submits a natural language query.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiQueryRequest {

    /** The natural language query from the investigator */
    @NotBlank(message = "Query text is required")
    @Size(max = 2000, message = "Query must not exceed 2000 characters")
    private String text;

    /** Session ID for conversation context */
    private String sessionId;

    /** Previous messages for multi-turn context (last 5 max) */
    private List<ConversationMessage> conversationHistory;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationMessage {
        private String role;  // "user" or "assistant"
        private String content;
    }
}
