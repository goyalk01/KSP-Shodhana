package com.ksp.shodhana.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * The "super response" from POST /api/v1/ai/query.
 * This single payload drives the entire investigation workspace UI.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkspacePayload {

    /** AI-generated natural language response */
    private String message;

    /** Which visualization panels to activate: "network_graph", "heatmap", "timeline", "evidence" */
    private List<String> visualizations;

    /** Network graph data for React Force Graph */
    private NetworkGraphData networkGraph;

    /** Heatmap points for React Leaflet */
    private HeatmapData heatmap;

    /** Timeline events */
    private List<TimelineEventData> timeline;

    /** Explainable evidence items */
    private List<EvidenceItem> evidence;

    /** Suggested follow-up questions */
    private List<String> suggestedFollowups;

    // ===== Nested Data Structures =====

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NetworkGraphData {
        private List<GraphNode> nodes;
        private List<GraphLink> links;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GraphNode {
        private String id;
        private String name;
        private String type;       // "criminal", "crime", "location"
        private String riskLevel;  // "high", "medium", "low"
        private String status;     // "wanted", "arrested", etc.
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GraphLink {
        private String source;
        private String target;
        private String type;      // "associate", "gang_member", "family", "co_accused"
        private int strength;     // 1-10
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HeatmapData {
        private List<HeatmapPoint> points;
        private GeoCenter center;
        private int zoom;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HeatmapPoint {
        private double lat;
        private double lng;
        private double intensity;  // 0.0 - 1.0
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeoCenter {
        private double lat;
        private double lng;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimelineEventData {
        private String id;
        private String eventType;
        private String eventDate;
        private String title;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvidenceItem {
        private String id;
        private String claim;
        private List<String> sources;
        private double confidence; // 0.0 - 1.0
        private String type;      // "criminal_link", "pattern", "location", "modus_operandi"
    }
}
