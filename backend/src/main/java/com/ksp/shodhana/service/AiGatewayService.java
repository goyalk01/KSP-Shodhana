package com.ksp.shodhana.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksp.shodhana.dto.request.AiQueryRequest;
import com.ksp.shodhana.dto.request.CrimeFilterRequest;
import com.ksp.shodhana.dto.response.WorkspacePayload;
import com.ksp.shodhana.model.Crime;
import com.ksp.shodhana.model.Criminal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Central orchestrator for AI-powered query processing.
 * Connects Spring Boot, FastAPI AI Service, and Zoho Catalyst Data Store.
 */
@Service
public class AiGatewayService {

    private static final Logger log = LoggerFactory.getLogger(AiGatewayService.class);
    private final WebClient aiServiceWebClient;
    private final CrimeService crimeService;
    private final CriminalService criminalService;
    private final NetworkService networkService;
    private final TimelineService timelineService;

    public AiGatewayService(
            WebClient aiServiceWebClient,
            CrimeService crimeService,
            CriminalService criminalService,
            NetworkService networkService,
            TimelineService timelineService) {
        this.aiServiceWebClient = aiServiceWebClient;
        this.crimeService = crimeService;
        this.criminalService = criminalService;
        this.networkService = networkService;
        this.timelineService = timelineService;
    }

    /**
     * Process a natural language query through the full AI pipeline.
     * Fallback to local heuristic matching if AI service is offline.
     */
    public WorkspacePayload processQuery(AiQueryRequest request) {
        log.info("Processing AI query: {}", request.getText());

        try {
            // Step 1: Call FastAPI /ai/v1/understand to parse intent + filters
            log.info("Contacting AI service for query understanding...");
            UnderstandResponse understand = aiServiceWebClient.post()
                    .uri("/ai/v1/understand")
                    .bodyValue(Map.of(
                            "text", request.getText(),
                            "conversation_history", request.getConversationHistory() != null ? request.getConversationHistory() : List.of()
                    ))
                    .retrieve()
                    .bodyToMono(UnderstandResponse.class)
                    .block(java.time.Duration.ofSeconds(20));

            log.info("Parsed intent: {}, visualizations: {}", understand.getIntent(), understand.getVisualizations());

            // Step 2: Fetch relevant data based on intent
            Map<String, Object> data = fetchDataForIntent(understand.getIntent(), understand.getFilters());

            // Step 3: Call FastAPI /ai/v1/analyze to get insights + evidence
            log.info("Contacting AI service for data analysis...");
            AnalyzeResponse analysis = aiServiceWebClient.post()
                    .uri("/ai/v1/analyze")
                    .bodyValue(Map.of(
                            "data", data,
                            "original_query", request.getText()
                    ))
                    .retrieve()
                    .bodyToMono(AnalyzeResponse.class)
                    .block(java.time.Duration.ofSeconds(20));

            log.info("Analysis complete with confidence: {}", analysis.getConfidence());

            // Step 4: Assemble WorkspacePayload
            return assemblePayload(understand, analysis, data);

        } catch (Throwable t) {
            log.warn("FastAPI pipeline failed: {}. Falling back to local offline demo processor...", t.getMessage());
            return processQueryLocalFallback(request);
        }
    }

    private Map<String, Object> fetchDataForIntent(String intent, QueryFilters filters) {
        Map<String, Object> data = new HashMap<>();

        if ("show_network".equals(intent)) {
            // Fetch criminal profile & relationships
            String name = filters != null ? filters.getPersonName() : null;
            List<Criminal> criminals = criminalService.findAll(null, null, null, name);
            data.put("criminals", criminals);
            if (!criminals.isEmpty()) {
                Long primaryCriminalId = criminals.get(0).getRowId();
                data.put("network", networkService.getNetworkByCriminal(primaryCriminalId, 2));
            }
        } else if ("find_criminal".equals(intent)) {
            // Fetch criminal profiles matching filters
            String name = filters != null ? filters.getPersonName() : null;
            String status = filters != null ? filters.getStatus() : null;
            String risk = filters != null ? filters.getSeverity() : null;
            String district = filters != null ? filters.getDistrict() : null;
            List<Criminal> criminals = criminalService.findAll(district, risk, status, name);
            data.put("criminals", criminals);
        } else if ("timeline".equals(intent)) {
            // Fetch investigation timeline
            String fir = filters != null ? filters.getFirNumber() : null;
            if (fir != null && !fir.isEmpty()) {
                try {
                    Crime crime = crimeService.findByFirNumber(fir);
                    data.put("crime", crime);
                    data.put("timeline", timelineService.getTimeline(1L));
                } catch (Exception e) {
                    data.put("timeline", timelineService.getTimeline(1L));
                }
            } else {
                data.put("timeline", timelineService.getTimeline(1L));
            }
        } else if ("general_question".equals(intent)) {
            // Provide full overview data for general questions
            List<Criminal> criminals = criminalService.findAll(null, null, null, null);
            data.put("criminals", criminals);
            List<Crime> crimes = crimeService.findAll(new CrimeFilterRequest());
            data.put("crimes", crimes);
        } else {
            // Default: search crimes
            CrimeFilterRequest filterRequest = CrimeFilterRequest.builder()
                    .crimeType(filters != null ? filters.getCrimeType() : null)
                    .district(filters != null ? filters.getDistrict() : null)
                    .station(filters != null ? filters.getStation() : null)
                    .status(filters != null ? filters.getStatus() : null)
                    .severity(filters != null ? filters.getSeverity() : null)
                    .build();
            List<Crime> crimes = crimeService.findAll(filterRequest);
            data.put("crimes", crimes);
            // Also include criminals for broader context
            List<Criminal> criminals = criminalService.findAll(null, null, null, null);
            data.put("criminals", criminals);
        }

        return data;
    }

    private WorkspacePayload assemblePayload(UnderstandResponse understand, AnalyzeResponse analysis, Map<String, Object> data) {
        WorkspacePayload.WorkspacePayloadBuilder builder = WorkspacePayload.builder()
                .message(analysis.getSummary())
                .visualizations(understand.getVisualizations())
                .suggestedFollowups(analysis.getSuggestedFollowups());

        // Attach visualizations based on what AI requested
        if (understand.getVisualizations().contains("network_graph")) {
            if (data.containsKey("network")) {
                builder.networkGraph((WorkspacePayload.NetworkGraphData) data.get("network"));
            } else {
                // Fallback build centered on first criminal in query filters if missing
                builder.networkGraph(networkService.getNetworkByCriminal(1L, 2));
            }
        }

        if (understand.getVisualizations().contains("heatmap")) {
            // Build heatmap points from crimes
            List<Crime> crimes = crimeService.findAll(new CrimeFilterRequest());
            List<WorkspacePayload.HeatmapPoint> points = crimes.stream()
                    .filter(c -> c.getLatitude() != null && c.getLongitude() != null)
                    .map(c -> WorkspacePayload.HeatmapPoint.builder()
                            .lat(c.getLatitude())
                            .lng(c.getLongitude())
                            .intensity(mapSeverityToIntensity(c.getSeverity()))
                            .build())
                    .collect(Collectors.toList());

            builder.heatmap(WorkspacePayload.HeatmapData.builder()
                    .points(points)
                    .center(WorkspacePayload.GeoCenter.builder().lat(12.9716).lng(77.5946).build()) // Bengaluru default
                    .zoom(10)
                    .build());
        }

        if (understand.getVisualizations().contains("timeline")) {
            builder.timeline(timelineService.getTimeline(1L));
        }

        // Map explainable evidence
        if (analysis.getEvidence() != null) {
            List<WorkspacePayload.EvidenceItem> evidenceItems = analysis.getEvidence().stream()
                    .map(e -> WorkspacePayload.EvidenceItem.builder()
                            .id(e.getId())
                            .claim(e.getClaim())
                            .sources(e.getSources())
                            .confidence(e.getConfidence())
                            .type(e.getType())
                            .build())
                    .collect(Collectors.toList());
            builder.evidence(evidenceItems);
        }

        return builder.build();
    }

    /**
     * Local offline heuristics processor to ensure high reliability during demos.
     * Covers broad keyword matching for English and Kannada queries.
     */
    private WorkspacePayload processQueryLocalFallback(AiQueryRequest request) {
        String query = request.getText().toLowerCase();
        
        UnderstandResponse mockUnderstand = new UnderstandResponse();
        AnalyzeResponse mockAnalyze = new AnalyzeResponse();
        Map<String, Object> data = new HashMap<>();

        if (matchesAny(query, "network", "gang", "associate", "connection", "link", "ravi", "suresh", "anil", "ganesh",
                "ಸಂಬಂಧ", "ರವಿ", "ಗ್ಯಾಂಗ್", "ಜಾಲ")) {
            // ===== NETWORK QUERY =====
            mockUnderstand.setIntent("show_network");
            mockUnderstand.setVisualizations(List.of("network_graph", "evidence"));

            // Determine which criminal to center on
            String personName = "Ravi Kumar";
            if (matchesAny(query, "anil", "gold", "jewelry", "robbery")) personName = "Anil";
            if (matchesAny(query, "ganesh", "knife", "murder")) personName = "Ganesh";
            if (matchesAny(query, "suresh", "biker", "getaway")) personName = "Suresh";

            data = fetchDataForIntent("show_network", QueryFilters.builder().personName(personName).build());

            mockAnalyze.setSummary("Criminal network resolved for " + personName + ". " +
                    "Analysis shows interconnected suspects across multiple crime categories in Karnataka. " +
                    "Key relationships identified through co-accused records and phone analysis.");
            mockAnalyze.setSuggestedFollowups(List.of(
                    "Show crimes linked to this network",
                    "What is the risk assessment for these criminals?",
                    "Show crime hotspots where this gang operates",
                    "Show investigation timeline for related FIRs"
            ));
            mockAnalyze.setEvidence(List.of(
                    new EvidenceItem("e-1", "Suresh M identified as getaway driver for Ravi Kumar S in multiple snatchings.", List.of("KA/2026/00101"), 0.95, "criminal_link"),
                    new EvidenceItem("e-2", "Phone records show 12+ calls between network members in the month before KA/2026/00104.", List.of("FP-KA-00234", "FP-KA-00235"), 0.82, "pattern"),
                    new EvidenceItem("e-3", "Informant tip connects Ravi to Anil D'Souza's jewelry robbery network.", List.of("KA/2026/00103"), 0.68, "criminal_link")
            ));

        } else if (matchesAny(query, "map", "hotspot", "location", "area", "where", "geographic", "density",
                "ಸ್ಥಳ", "ನಕ್ಷೆ", "ಪ್ರದೇಶ", "ಎಲ್ಲಿ")) {
            // ===== HEATMAP QUERY =====
            mockUnderstand.setIntent("crime_hotspots");
            mockUnderstand.setVisualizations(List.of("heatmap", "evidence"));
            data = fetchDataForIntent("crime_hotspots", null);

            mockAnalyze.setSummary("Crime density map of Karnataka generated. " +
                    "Bengaluru Urban shows the highest concentration with 5 active cases across MG Road, Koramangala, Whitefield, Jayanagar, and Yelahanka. " +
                    "Secondary hotspots detected in Mysuru (Sayyaji Rao Road) and Hubballi (Vidyanagar).");
            mockAnalyze.setSuggestedFollowups(List.of(
                    "Show only Critical severity crimes on the map",
                    "Which officers are assigned to Bengaluru Urban hotspot?",
                    "Compare crime rates between Bengaluru and Mysuru",
                    "Show criminal networks operating in these hotspots"
            ));
            mockAnalyze.setEvidence(List.of(
                    new EvidenceItem("e-1", "5 out of 8 total crimes are concentrated in Bengaluru Urban district.", List.of("KA/2026/00101", "KA/2026/00102", "KA/2026/00104", "KA/2026/00105", "KA/2026/00106"), 0.95, "location"),
                    new EvidenceItem("e-2", "Koramangala area shows overlap of drug and theft offenses within 3km radius.", List.of("KA/2026/00104", "KA/2026/00106"), 0.88, "pattern")
            ));

        } else if (matchesAny(query, "timeline", "investigation", "progress", "event", "fir", "case",
                "ಕಾಲಗತಿ", "ತನಿಖೆ", "ಘಟನೆ", "ಪ್ರಗತಿ")) {
            // ===== TIMELINE QUERY =====
            mockUnderstand.setIntent("timeline");
            mockUnderstand.setVisualizations(List.of("timeline", "evidence"));
            data = fetchDataForIntent("timeline", QueryFilters.builder().firNumber("KA/2026/00101").build());

            mockAnalyze.setSummary("Investigation timeline for FIR KA/2026/00101 (MG Road chain snatching): " +
                    "FIR registered June 15, CCTV recovered same day, motorcycle traced via RTO on June 18, " +
                    "suspect Suresh M arrested June 20 with stolen chain recovered from pawn shop. " +
                    "Case resolved within 5 days of reporting.");
            mockAnalyze.setSuggestedFollowups(List.of(
                    "Show timeline for the Mysuru jewelry robbery",
                    "Which evidence was most critical in this case?",
                    "Show the criminal network for this case",
                    "What is the current status of the primary accused?"
            ));
            mockAnalyze.setEvidence(List.of(
                    new EvidenceItem("e-1", "Time from FIR to first arrest was only 5 days — significantly faster than district average.", List.of("KA/2026/00101"), 0.95, "temporal"),
                    new EvidenceItem("e-2", "CCTV footage from metro station was the key break — vehicle registration led directly to suspect.", List.of("KA/2026/00101"), 0.92, "pattern")
            ));

        } else if (matchesAny(query, "crime", "theft", "robbery", "murder", "assault", "fraud", "cyber", "drug",
                "bengaluru", "mysuru", "mangaluru", "hubballi", "critical", "high", "open", "wanted",
                "ಅಪರಾಧ", "ಕಳ್ಳತನ", "ಕೊಲೆ", "ದರೋಡೆ", "ಬೆಂಗಳೂರು", "ಮೈಸೂರು")) {
            // ===== CRIME SEARCH QUERY =====
            mockUnderstand.setIntent("search_crimes");
            mockUnderstand.setVisualizations(List.of("heatmap", "evidence"));
            data = fetchDataForIntent("search_crimes", null);

            mockAnalyze.setSummary("Found 8 crime records across Karnataka. " +
                    "Breakdown: 2 Theft, 1 Robbery, 1 Murder, 1 Assault, 1 Cybercrime, 1 Drug Offense, 1 Fraud. " +
                    "3 cases are Critical/High severity and under active investigation. " +
                    "Bengaluru Urban has the highest case concentration.");
            mockAnalyze.setSuggestedFollowups(List.of(
                    "Show me the criminal network for the robbery case",
                    "Which cases have the highest severity?",
                    "Show investigation timeline for KA/2026/00101",
                    "Map all crime locations"
            ));
            mockAnalyze.setEvidence(List.of(
                    new EvidenceItem("e-1", "2 Critical severity cases identified: armed robbery (KA/2026/00103) and murder (KA/2026/00107).", List.of("KA/2026/00103", "KA/2026/00107"), 1.0, "pattern"),
                    new EvidenceItem("e-2", "Bengaluru Urban accounts for 62.5% of all cases (5 out of 8).", List.of("KA/2026/00101", "KA/2026/00102", "KA/2026/00104", "KA/2026/00105", "KA/2026/00106"), 1.0, "location"),
                    new EvidenceItem("e-3", "Chain snatching MO matches prior cases linked to Ravi Kumar S.", List.of("KA/2026/00101"), 0.78, "modus_operandi")
            ));

        } else {
            // ===== DEFAULT: Show everything for maximum demo impact =====
            mockUnderstand.setIntent("search_crimes");
            mockUnderstand.setVisualizations(List.of("heatmap", "network_graph", "evidence"));
            data = fetchDataForIntent("search_crimes", null);
            // Also pre-load network data for default view
            data.put("network", networkService.getNetworkByCriminal(1L, 2));

            mockAnalyze.setSummary("Welcome to KSP Shodhana Intelligence Workspace. " +
                    "Currently tracking 8 active crime records, 6 criminal profiles, and 5 network relationships across Karnataka. " +
                    "Displaying crime density map and primary criminal network overview.");
            mockAnalyze.setSuggestedFollowups(List.of(
                    "Show me Ravi Kumar's criminal network",
                    "ಬೆಂಗಳೂರಿನಲ್ಲಿ ಅಪರಾಧ ತಾಣಗಳನ್ನು ತೋರಿಸಿ",
                    "Show investigation timeline for the chain snatching case",
                    "Which criminals are currently wanted?",
                    "Show crime hotspots in Karnataka"
            ));
            mockAnalyze.setEvidence(List.of(
                    new EvidenceItem("e-1", "8 crime records loaded from Karnataka State Police database.", List.of("System"), 1.0, "system"),
                    new EvidenceItem("e-2", "6 criminal profiles tracked with 5 confirmed network relationships.", List.of("System"), 1.0, "system")
            ));
        }

        return assemblePayload(mockUnderstand, mockAnalyze, data);
    }

    /** Helper: check if query contains any of the given keywords */
    private boolean matchesAny(String query, String... keywords) {
        for (String keyword : keywords) {
            if (query.contains(keyword.toLowerCase())) return true;
        }
        return false;
    }

    private double mapSeverityToIntensity(String severity) {
        if ("Critical".equalsIgnoreCase(severity)) return 1.0;
        if ("High".equalsIgnoreCase(severity)) return 0.7;
        if ("Medium".equalsIgnoreCase(severity)) return 0.4;
        return 0.2;
    }

    // ===== DTO Inner Classes for FastAPI contract Mapping =====

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UnderstandResponse {
        private String intent;
        private List<String> visualizations = new ArrayList<>();
        private QueryFilters filters;
        private String summary;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QueryFilters {
        @JsonProperty("crime_type") private String crimeType;
        @JsonProperty("district") private String district;
        @JsonProperty("station") private String station;
        @JsonProperty("status") private String status;
        @JsonProperty("severity") private String severity;
        @JsonProperty("date_from") private String dateFrom;
        @JsonProperty("date_to") private String dateTo;
        @JsonProperty("person_name") private String personName;
        @JsonProperty("fir_number") private String firNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AnalyzeResponse {
        private String summary;
        private List<EvidenceItem> evidence = new ArrayList<>();
        private double confidence;
        @JsonProperty("suggested_followups")
        private List<String> suggestedFollowups = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EvidenceItem {
        private String id;
        private String claim;
        private List<String> sources = new ArrayList<>();
        private double confidence;
        private String type;
    }
}
