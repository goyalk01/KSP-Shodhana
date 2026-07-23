package com.ksp.shodhana.service;

import com.ksp.shodhana.model.CriminalNetwork;
import com.ksp.shodhana.repository.CriminalNetworkRepository;
import com.ksp.shodhana.repository.CriminalRepository;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Enterprise Graph Service providing multi-hop graph analytics and shortest criminal path algorithms.
 * Supports Cypher queries on Neo4j Graph Database with in-memory BFS fallback.
 */
@Service
public class GraphService {

    private static final Logger log = LoggerFactory.getLogger(GraphService.class);

    private final CriminalRepository criminalRepository;
    private final CriminalNetworkRepository criminalNetworkRepository;
    private final Driver neo4jDriver;

    public GraphService(
            CriminalRepository criminalRepository,
            CriminalNetworkRepository criminalNetworkRepository,
            @Autowired(required = false) Driver neo4jDriver) {
        this.criminalRepository = criminalRepository;
        this.criminalNetworkRepository = criminalNetworkRepository;
        this.neo4jDriver = neo4jDriver;
    }

    /**
     * Perform multi-hop graph path analysis between two criminal suspects.
     * Executes Cypher graph traversal on Neo4j if available, or falls back to in-memory BFS.
     */
    public Map<String, Object> findShortestCriminalPath(Long criminalAId, Long criminalBId, int maxHops) {
        if (neo4jDriver != null) {
            try (Session session = neo4jDriver.session()) {
                log.info("Executing Neo4j Cypher shortest path query between criminal {} and {}", criminalAId, criminalBId);
                String cypher = "MATCH (a:Criminal {rowId: $aId}), (b:Criminal {rowId: $bId}), " +
                        "p = shortestPath((a)-[:CO_ACCUSED|ASSOCIATE*..5]-(b)) " +
                        "RETURN [n IN nodes(p) | n.rowId] AS pathIds, length(p) AS hops";

                Record record = session.run(cypher, Map.of("aId", criminalAId, "bId", criminalBId)).single();
                if (record != null) {
                    List<Object> rawPath = record.get("pathIds").asList();
                    List<Long> pathIds = new ArrayList<>();
                    for (Object obj : rawPath) {
                        pathIds.add(((Number) obj).longValue());
                    }
                    int hops = record.get("hops").asInt();

                    Map<String, Object> result = new HashMap<>();
                    result.put("connected", true);
                    result.put("hops", hops);
                    result.put("pathIds", pathIds);
                    result.put("mode", "Neo4j Cypher");
                    return result;
                }
            } catch (Exception e) {
                log.warn("Neo4j database query unavailable ({}), using fallback graph traversal", e.getMessage());
            }
        }

        log.info("Executing in-memory multi-hop BFS path search from criminal {} to {} (maxHops: {})", criminalAId, criminalBId, maxHops);

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
        result.put("mode", "BFS Traversal Engine");
        return result;
    }
}
