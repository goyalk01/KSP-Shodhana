package com.ksp.shodhana.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GraphServiceTest {

    @Autowired
    private GraphService graphService;

    @Test
    @DisplayName("Verify Multi-Hop Graph Traversal between Criminal Suspects")
    public void testMultiHopGraphTraversal() {
        // Rajesh Shetty (4) to Farooq Ahmed (8)
        Map<String, Object> result = graphService.findShortestCriminalPath(4L, 8L, 3);

        assertNotNull(result);
        assertTrue((Boolean) result.get("connected"));
        assertTrue((Integer) result.get("hops") >= 1);
    }
}
