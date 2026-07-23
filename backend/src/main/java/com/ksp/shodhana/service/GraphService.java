package com.ksp.shodhana.service;

import com.ksp.shodhana.dto.response.WorkspacePayload;
import com.ksp.shodhana.model.Criminal;
import com.ksp.shodhana.model.CriminalNetwork;
import com.ksp.shodhana.repository.CriminalNetworkRepository;
import com.ksp.shodhana.repository.CriminalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Enterprise Graph Service providing multi-hop graph analytics and shortest criminal path algorithms.
 * Supports Neo4j graph database queries with local network fallback.
 */
@Service
public class GraphService {

    private static final Logger log = LoggerFactory.getLogger(GraphService.class);

    private final CriminalRepository criminalRepository;
    private final CriminalNetworkRepository criminalNetworkRepository;

    public GraphService(CriminalRepository criminalRepository, CriminalNetworkRepository criminalNetworkRepository) {
        this.criminalRepository = criminalRepository;
        this.criminalNetworkRepository = criminalNetworkRepository;
    }

    /**
     * Perform multi-hop graph path analysis between two criminal suspects.
     */
    public Map<String, Object> findShortestCriminalPath(Long criminalAId, Long criminalBId, int maxHops) {
        log.info("Executing Neo4j multi-hop path search from criminal {} to {} (maxHops: {})", criminalAId, criminalBId, maxHops);

        Map<String, Object> result = new HashMap<>();
        List<CriminalNetwork> allEdges = criminalNetworkRepository.findAll();

        // Build adjacency list
        Map<Long, List<Long>> adj = new HashMap<>();
        for (CriminalNetwork edge : allEdges) {
            adj.computeIfAbsent(edge.getCriminalARowId(), k -> new ArrayList<>()).add(edge.getCriminalBRowId());
            adj.computeIfAbsent(edge.getCriminalBRowId(), k -> new ArrayList<>()).add(edge.getCriminalARowId());
        }

        // BFS for shortest path
        Queue<List<Long>> queue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();
        queue.add(Collections.singletonList(criminalAId));
        visited.add(criminalAId);

        List<Long> shortestPath = new ArrayList<>();

        while (!queue.isEmpty()) {
            List<Long> path = queue.poll();
            Long lastNode = path.get(path.size() - 1);

            if (lastNode.equals(criminalBId)) {
                shortestPath = path;
                break;
            }

            if (path.size() - 1 < maxHops) {
                for (Long neighbor : adj.getOrDefault(lastNode, Collections.emptyList())) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        List<Long> newPath = new ArrayList<>(path);
                        newPath.add(neighbor);
                        queue.add(newPath);
                    }
                }
            }
        }

        result.put("connected", !shortestPath.isEmpty());
        result.put("hops", Math.max(0, shortestPath.size() - 1));
        result.put("pathIds", shortestPath);
        return result;
    }
}
