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

            log.info("Parsed intent: {}, visualizations: {}, filters: {}", understand.getIntent(), understand.getVisualizations(), understand.getFilters());

            // Step 2: Fetch relevant data based on intent
            Map<String, Object> data = fetchDataForIntent(understand.getIntent(), understand.getFilters(), request.getText());

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

            // Step 4: Assemble WorkspacePayload dynamically
            return assemblePayload(understand, analysis, data, understand.getFilters(), request.getText());

        } catch (Throwable t) {
            log.warn("FastAPI pipeline failed: {}. Falling back to local offline demo processor...", t.getMessage());
            return processQueryLocalFallback(request);
        }
    }

    private Map<String, Object> fetchDataForIntent(String intent, QueryFilters filters, String rawQuery) {
        Map<String, Object> data = new HashMap<>();

        String personName = filters != null ? filters.getPersonName() : null;
        String firNumber = filters != null ? filters.getFirNumber() : null;
        String district = filters != null ? filters.getDistrict() : null;

        if (personName == null || personName.trim().isEmpty()) {
            personName = extractSuspectNameFromText(rawQuery);
        }
        if (district == null || district.trim().isEmpty()) {
            district = extractDistrictFromText(rawQuery);
        }

        if ("show_network".equals(intent) || personName != null) {
            List<Criminal> criminals = criminalService.findAll(null, null, null, personName);
            data.put("criminals", criminals);
            if (!criminals.isEmpty()) {
                Long primaryCriminalId = criminals.get(0).getRowId();
                data.put("network", networkService.getNetworkByCriminal(primaryCriminalId, 2));
            } else {
                data.put("network", networkService.getNetworkByCriminal(1L, 2));
            }
        } else if ("find_criminal".equals(intent)) {
            String status = filters != null ? filters.getStatus() : null;
            String risk = filters != null ? filters.getSeverity() : null;
            List<Criminal> criminals = criminalService.findAll(district, risk, status, personName);
            data.put("criminals", criminals);
            if (!criminals.isEmpty()) {
                data.put("network", networkService.getNetworkByCriminal(criminals.get(0).getRowId(), 2));
            }
        } else if ("timeline".equals(intent)) {
            if (firNumber != null && !firNumber.isEmpty()) {
                try {
                    Crime crime = crimeService.findByFirNumber(firNumber);
                    data.put("crime", crime);
                    data.put("network", networkService.getNetworkByCrime(crime.getRowId()));
                } catch (Exception e) {
                    data.put("timeline", timelineService.getTimeline(1L));
                }
            } else {
                data.put("timeline", timelineService.getTimeline(1L));
            }
        } else if ("general_question".equals(intent)) {
            List<Criminal> criminals = criminalService.findAll(null, null, null, null);
            data.put("criminals", criminals);
            List<Crime> crimes = crimeService.findAll(new CrimeFilterRequest());
            data.put("crimes", crimes);
        } else {
            // Search crimes with dynamic district & criteria filters
            CrimeFilterRequest filterRequest = CrimeFilterRequest.builder()
                    .crimeType(filters != null ? filters.getCrimeType() : null)
                    .district(district)
                    .station(filters != null ? filters.getStation() : null)
                    .status(filters != null ? filters.getStatus() : null)
                    .severity(filters != null ? filters.getSeverity() : null)
                    .build();
            List<Crime> crimes = crimeService.findAll(filterRequest);
            data.put("crimes", crimes);
            List<Criminal> criminals = criminalService.findAll(district, null, null, null);
            data.put("criminals", criminals);
            if (!criminals.isEmpty()) {
                data.put("network", networkService.getNetworkByCriminal(criminals.get(0).getRowId(), 2));
            }
        }

        return data;
    }

    private WorkspacePayload assemblePayload(UnderstandResponse understand, AnalyzeResponse analysis, Map<String, Object> data, QueryFilters filters, String rawQuery) {
        WorkspacePayload.WorkspacePayloadBuilder builder = WorkspacePayload.builder()
                .message(analysis.getSummary())
                .visualizations(understand.getVisualizations())
                .suggestedFollowups(analysis.getSuggestedFollowups());

        // Dynamic Network Graph resolution
        if (understand.getVisualizations().contains("network_graph")) {
            if (data.containsKey("network")) {
                builder.networkGraph((WorkspacePayload.NetworkGraphData) data.get("network"));
            } else if (data.containsKey("criminals") && !((List<?>) data.get("criminals")).isEmpty()) {
                Criminal c = ((List<Criminal>) data.get("criminals")).get(0);
                builder.networkGraph(networkService.getNetworkByCriminal(c.getRowId(), 2));
            } else {
                builder.networkGraph(networkService.getNetworkByCriminal(1L, 2));
            }
        }

        // Dynamic Heatmap resolution based on exact location/district filter
        if (understand.getVisualizations().contains("heatmap")) {
            String targetDistrict = filters != null ? filters.getDistrict() : null;
            if (targetDistrict == null || targetDistrict.trim().isEmpty()) {
                targetDistrict = extractDistrictFromText(rawQuery);
            }

            CrimeFilterRequest filterRequest = CrimeFilterRequest.builder()
                    .crimeType(filters != null ? filters.getCrimeType() : null)
                    .district(targetDistrict)
                    .station(filters != null ? filters.getStation() : null)
                    .status(filters != null ? filters.getStatus() : null)
                    .severity(filters != null ? filters.getSeverity() : null)
                    .build();

            List<Crime> crimes = crimeService.findAll(filterRequest);

            // Map incidents to geographic points
            List<WorkspacePayload.HeatmapPoint> points = crimes.stream()
                    .filter(c -> c.getLatitude() != null && c.getLongitude() != null)
                    .map(c -> WorkspacePayload.HeatmapPoint.builder()
                            .lat(c.getLatitude())
                            .lng(c.getLongitude())
                            .intensity(mapSeverityToIntensity(c.getSeverity()))
                            .build())
                    .collect(Collectors.toList());

            // Determine optimal map center and zoom level based on district
            double centerLat = 14.2000;
            double centerLng = 75.8000;
            int zoomLevel = 7; // Default Karnataka state view

            if (targetDistrict != null && !targetDistrict.isEmpty()) {
                if (targetDistrict.toLowerCase().contains("bengaluru")) {
                    centerLat = 12.9716;
                    centerLng = 77.5946;
                    zoomLevel = 11;
                } else if (targetDistrict.toLowerCase().contains("mysuru") || targetDistrict.toLowerCase().contains("mysore")) {
                    centerLat = 12.2958;
                    centerLng = 76.6394;
                    zoomLevel = 12;
                } else if (targetDistrict.toLowerCase().contains("hubballi") || targetDistrict.toLowerCase().contains("hubli")) {
                    centerLat = 15.3647;
                    centerLng = 75.1240;
                    zoomLevel = 12;
                } else if (targetDistrict.toLowerCase().contains("dakshina") || targetDistrict.toLowerCase().contains("mangaluru")) {
                    centerLat = 12.9141;
                    centerLng = 74.8560;
                    zoomLevel = 12;
                } else if (!points.isEmpty()) {
                    centerLat = points.get(0).getLat();
                    centerLng = points.get(0).getLng();
                    zoomLevel = 11;
                }
            } else if (!points.isEmpty()) {
                // Average center coordinates for filtered subset
                double sumLat = 0, sumLng = 0;
                for (WorkspacePayload.HeatmapPoint p : points) {
                    sumLat += p.getLat();
                    sumLng += p.getLng();
                }
                centerLat = sumLat / points.size();
                centerLng = sumLng / points.size();
                zoomLevel = points.size() < 4 ? 11 : 8;
            }

            builder.heatmap(WorkspacePayload.HeatmapData.builder()
                    .points(points)
                    .center(WorkspacePayload.GeoCenter.builder().lat(centerLat).lng(centerLng).build())
                    .zoom(zoomLevel)
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

    private String extractSuspectNameFromText(String rawQuery) {
        if (rawQuery == null) return null;
        String q = rawQuery.toLowerCase();
        if (q.contains("rajesh") || q.contains("shetty") || q.contains("raja")) return "Rajesh";
        if (q.contains("farooq")) return "Farooq";
        if (q.contains("anil")) return "Anil";
        if (q.contains("suresh")) return "Suresh";
        if (q.contains("kiran")) return "Kiran";
        if (q.contains("vikram") || q.contains("vicky")) return "Vikram";
        if (q.contains("deepak")) return "Deepak";
        if (q.contains("basavaraj") || q.contains("patil")) return "Basavaraj";
        if (q.contains("santhosh")) return "Santhosh";
        if (q.contains("mohammed") || q.contains("zaid")) return "Mohammed";
        if (q.contains("shivaji")) return "Shivaji";
        if (q.contains("naveen")) return "Naveen";
        if (q.contains("chandru")) return "Chandru";
        if (q.contains("ramanjaneya") || q.contains("ramu")) return "Ramanjaneya";
        if (q.contains("praveen")) return "Praveen";
        if (q.contains("venkatesh") || q.contains("venky")) return "Venkatesh";
        return null;
    }

    private String extractDistrictFromText(String rawQuery) {
        if (rawQuery == null) return null;
        String q = rawQuery.toLowerCase();
        if (q.contains("bengaluru") || q.contains("bangalore") || q.contains("ಬೆಂಗಳೂರು")) return "Bengaluru Urban";
        if (q.contains("mysuru") || q.contains("mysore") || q.contains("ಮೈಸೂರು")) return "Mysuru";
        if (q.contains("hubballi") || q.contains("hubli") || q.contains("dharwad") || q.contains("ಹುಬ್ಬಳ್ಳಿ")) return "Hubballi-Dharwad";
        if (q.contains("mangaluru") || q.contains("mangalore") || q.contains("mangaluru")) return "Dakshina Kannada";
        if (q.contains("udupi") || q.contains("ಉಡುಪಿ")) return "Udupi";
        if (q.contains("belagavi") || q.contains("belgaum") || q.contains("ಬೆಳಗಾವಿ")) return "Belagavi";
        return null;
    }

    /**
     * Local offline heuristics processor to ensure high reliability during demos.
     */
    private WorkspacePayload processQueryLocalFallback(AiQueryRequest request) {
        String query = request.getText().toLowerCase();
        
        UnderstandResponse mockUnderstand = new UnderstandResponse();
        AnalyzeResponse mockAnalyze = new AnalyzeResponse();
        Map<String, Object> data = new HashMap<>();

        if (matchesAny(query, "network", "gang", "associate", "connection", "link", "rajesh", "shetty", "farooq", "anil", "suresh", "kiran", "vikram", "deepak", "basavaraj", "santhosh", "mohammed", "zaid", "shivaji", "naveen", "chandru", "ramanjaneya", "praveen", "venkatesh",
                "ಸಂಬಂಧ", "ರವಿ", "ರಾಜೇಶ್", "ಗ್ಯಾಂಗ್", "ಜಾಲ")) {
            mockUnderstand.setIntent("show_network");
            mockUnderstand.setVisualizations(List.of("network_graph", "evidence"));

            String personName = extractSuspectNameFromText(query);
            if (personName == null) personName = "Rajesh Shetty";

            data = fetchDataForIntent("show_network", QueryFilters.builder().personName(personName).build(), query);

            mockAnalyze.setSummary("Criminal network resolved for " + personName + ". " +
                    "Analysis shows interconnected suspects across multiple crime categories in Karnataka. " +
                    "Key relationships identified through co-accused records and intelligence links.");
            mockAnalyze.setSuggestedFollowups(List.of(
                    "Show crimes linked to " + personName,
                    "What is the risk assessment for suspects in this network?",
                    "Show crime hotspots where this network operates"
            ));
            mockAnalyze.setEvidence(List.of(
                    new EvidenceItem("e-1", personName + " identified in intelligence database with active network links.", List.of("KSP-DB-2026"), 0.95, "criminal_link")
            ));

        } else if (matchesAny(query, "map", "hotspot", "location", "area", "where", "geographic", "density",
                "ಸ್ಥಳ", "ನಕ್ಷೆ", "ಪ್ರದೇಶ", "ಎಲ್ಲಿ")) {
            mockUnderstand.setIntent("crime_hotspots");
            mockUnderstand.setVisualizations(List.of("heatmap", "evidence"));
            String district = extractDistrictFromText(query);
            data = fetchDataForIntent("crime_hotspots", QueryFilters.builder().district(district).build(), query);

            mockAnalyze.setSummary("Crime density map of Karnataka generated for target query. " +
                    "Displays geographic concentration across active districts in Karnataka.");
            mockAnalyze.setSuggestedFollowups(List.of(
                    "Show only Critical severity crimes on the map",
                    "Show criminal networks operating in these hotspots"
            ));
            mockAnalyze.setEvidence(List.of(
                    new EvidenceItem("e-1", "Crimes concentrated across major commercial and residential hubs.", List.of("KA/2026/00101"), 0.95, "location")
            ));

        } else {
            mockUnderstand.setIntent("find_criminal");
            mockUnderstand.setVisualizations(List.of("network_graph", "evidence"));
            String personName = extractSuspectNameFromText(query);
            data = fetchDataForIntent("find_criminal", QueryFilters.builder().personName(personName).build(), query);

            mockAnalyze.setSummary("Resolved criminal profile search across Karnataka State Police records.");
            mockAnalyze.setSuggestedFollowups(List.of(
                    "Show criminal network for top suspect",
                    "Show investigation timeline"
            ));
            mockAnalyze.setEvidence(List.of(
                    new EvidenceItem("e-1", "Suspect records retrieved from Karnataka state police registry.", List.of("KSP-DB-2026"), 0.90, "pattern")
            ));
        }

        return assemblePayload(mockUnderstand, mockAnalyze, data, null, query);
    }

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
