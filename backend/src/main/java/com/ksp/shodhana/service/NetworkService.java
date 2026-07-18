package com.ksp.shodhana.service;

import com.ksp.shodhana.dto.response.WorkspacePayload;
import com.ksp.shodhana.model.Crime;
import com.ksp.shodhana.model.CrimeCriminalLink;
import com.ksp.shodhana.model.Criminal;
import com.ksp.shodhana.model.CriminalNetwork;
import com.ksp.shodhana.repository.CrimeCriminalLinkRepository;
import com.ksp.shodhana.repository.CrimeRepository;
import com.ksp.shodhana.repository.CriminalNetworkRepository;
import com.ksp.shodhana.repository.CriminalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service layer for criminal network graph operations.
 * Builds graph nodes and links from CriminalNetwork and CrimeCriminalLink tables.
 */
@Service
public class NetworkService {

    private static final Logger log = LoggerFactory.getLogger(NetworkService.class);

    private final CriminalRepository criminalRepository;
    private final CriminalNetworkRepository criminalNetworkRepository;
    private final CrimeCriminalLinkRepository crimeCriminalLinkRepository;
    private final CrimeRepository crimeRepository;

    public NetworkService(
            CriminalRepository criminalRepository,
            CriminalNetworkRepository criminalNetworkRepository,
            CrimeCriminalLinkRepository crimeCriminalLinkRepository,
            CrimeRepository crimeRepository) {
        this.criminalRepository = criminalRepository;
        this.criminalNetworkRepository = criminalNetworkRepository;
        this.crimeCriminalLinkRepository = crimeCriminalLinkRepository;
        this.crimeRepository = crimeRepository;
    }

    /**
     * Build a network graph centered on a criminal, with specified traversal depth.
     */
    public WorkspacePayload.NetworkGraphData getNetworkByCriminal(Long criminalId, int depth) {
        log.info("Building network graph for criminal {} with depth {}", criminalId, depth);

        Map<String, WorkspacePayload.GraphNode> nodesMap = new HashMap<>();
        List<WorkspacePayload.GraphLink> links = new ArrayList<>();

        // Fetch center criminal
        Optional<Criminal> centerCriminalOpt = criminalRepository.findById(criminalId);
        if (centerCriminalOpt.isEmpty()) {
            return WorkspacePayload.NetworkGraphData.builder()
                    .nodes(Collections.emptyList())
                    .links(Collections.emptyList())
                    .build();
        }

        Criminal centerCriminal = centerCriminalOpt.get();
        String centerId = "criminal-" + centerCriminal.getRowId();
        nodesMap.put(centerId, mapCriminalToNode(centerCriminal));

        // Fetch network connections
        List<CriminalNetwork> networks = criminalNetworkRepository.findByCriminalId(criminalId);
        
        for (CriminalNetwork edge : networks) {
            Long otherId = edge.getCriminalARowId().equals(criminalId) 
                    ? edge.getCriminalBRowId() 
                    : edge.getCriminalARowId();
            
            Optional<Criminal> otherCriminalOpt = criminalRepository.findById(otherId);
            if (otherCriminalOpt.isPresent()) {
                Criminal other = otherCriminalOpt.get();
                String otherKey = "criminal-" + other.getRowId();
                
                if (!nodesMap.containsKey(otherKey)) {
                    nodesMap.put(otherKey, mapCriminalToNode(other));
                }

                links.add(WorkspacePayload.GraphLink.builder()
                        .source("criminal-" + edge.getCriminalARowId())
                        .target("criminal-" + edge.getCriminalBRowId())
                        .type(edge.getRelationshipType())
                        .strength(edge.getStrength() != null ? edge.getStrength() : 5)
                        .build());
            }
        }

        // Add crimes these criminals are linked to, for richer graph context
        for (WorkspacePayload.GraphNode node : new ArrayList<>(nodesMap.values())) {
            if ("criminal".equals(node.getType())) {
                Long cId = Long.parseLong(node.getId().replace("criminal-", ""));
                List<CrimeCriminalLink> cLinks = crimeCriminalLinkRepository.findByCriminalId(cId);
                
                for (CrimeCriminalLink link : cLinks) {
                    Optional<Crime> crimeOpt = crimeRepository.findById(link.getCrimeRowId());
                    if (crimeOpt.isPresent()) {
                        Crime crime = crimeOpt.get();
                        String crimeKey = "crime-" + crime.getRowId();
                        
                        if (!nodesMap.containsKey(crimeKey)) {
                            nodesMap.put(crimeKey, WorkspacePayload.GraphNode.builder()
                                    .id(crimeKey)
                                    .name(crime.getFirNumber() + " (" + crime.getCrimeType() + ")")
                                    .type("crime")
                                    .status(crime.getStatus())
                                    .riskLevel(crime.getSeverity())
                                    .build());
                        }

                        links.add(WorkspacePayload.GraphLink.builder()
                                .source(node.getId())
                                .target(crimeKey)
                                .type(link.getRole())
                                .strength(6)
                                .build());
                    }
                }
            }
        }

        return WorkspacePayload.NetworkGraphData.builder()
                .nodes(new ArrayList<>(nodesMap.values()))
                .links(links)
                .build();
    }

    /**
     * Build a network graph showing all criminals linked to a specific crime.
     */
    public WorkspacePayload.NetworkGraphData getNetworkByCrime(Long crimeId) {
        log.info("Building network graph for crime {}", crimeId);

        Map<String, WorkspacePayload.GraphNode> nodesMap = new HashMap<>();
        List<WorkspacePayload.GraphLink> links = new ArrayList<>();

        Optional<Crime> crimeOpt = crimeRepository.findById(crimeId);
        if (crimeOpt.isEmpty()) {
            return WorkspacePayload.NetworkGraphData.builder()
                    .nodes(Collections.emptyList())
                    .links(Collections.emptyList())
                    .build();
                
        }

        Crime crime = crimeOpt.get();
        String crimeKey = "crime-" + crime.getRowId();
        nodesMap.put(crimeKey, WorkspacePayload.GraphNode.builder()
                .id(crimeKey)
                .name(crime.getFirNumber() + " (" + crime.getCrimeType() + ")")
                .type("crime")
                .status(crime.getStatus())
                .riskLevel(crime.getSeverity())
                .build());

        // Get linked criminals
        List<CrimeCriminalLink> crimeLinks = crimeCriminalLinkRepository.findByCrimeId(crimeId);
        List<Long> linkedCriminalIds = new ArrayList<>();

        for (CrimeCriminalLink link : crimeLinks) {
            Optional<Criminal> criminalOpt = criminalRepository.findById(link.getCriminalRowId());
            if (criminalOpt.isPresent()) {
                Criminal criminal = criminalOpt.get();
                String crimKey = "criminal-" + criminal.getRowId();
                linkedCriminalIds.add(criminal.getRowId());

                nodesMap.put(crimKey, mapCriminalToNode(criminal));

                links.add(WorkspacePayload.GraphLink.builder()
                        .source(crimKey)
                        .target(crimeKey)
                        .type(link.getRole())
                        .strength(8)
                        .build());
            }
        }

        // Also add relationship edges between these co-accused criminals if they exist
        if (linkedCriminalIds.size() > 1) {
            List<CriminalNetwork> allEdges = criminalNetworkRepository.findAll();
            for (CriminalNetwork edge : allEdges) {
                if (linkedCriminalIds.contains(edge.getCriminalARowId()) && 
                    linkedCriminalIds.contains(edge.getCriminalBRowId())) {
                    links.add(WorkspacePayload.GraphLink.builder()
                            .source("criminal-" + edge.getCriminalARowId())
                            .target("criminal-" + edge.getCriminalBRowId())
                            .type(edge.getRelationshipType())
                            .strength(edge.getStrength() != null ? edge.getStrength() : 5)
                            .build());
                }
            }
        }

        return WorkspacePayload.NetworkGraphData.builder()
                .nodes(new ArrayList<>(nodesMap.values()))
                .links(links)
                .build();
    }

    private WorkspacePayload.GraphNode mapCriminalToNode(Criminal criminal) {
        return WorkspacePayload.GraphNode.builder()
                .id("criminal-" + criminal.getRowId())
                .name(criminal.getName() + (criminal.getAlias() != null && !criminal.getAlias().isEmpty() ? " (" + criminal.getAlias() + ")" : ""))
                .type("criminal")
                .riskLevel(criminal.getRiskLevel())
                .status(criminal.getStatus())
                .build();
    }
}
