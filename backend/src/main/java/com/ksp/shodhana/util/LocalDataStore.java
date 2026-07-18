package com.ksp.shodhana.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksp.shodhana.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory local data store that loads seed data from JSON resources on startup.
 * Acts as a robust fallback/mock database for local development and offline demos.
 */
@Component
public class LocalDataStore {

    private static final Logger log = LoggerFactory.getLogger(LocalDataStore.class);
    private final ObjectMapper objectMapper;

    private List<Crime> crimes = new ArrayList<>();
    private List<Criminal> criminals = new ArrayList<>();
    private List<CrimeCriminalLink> links = new ArrayList<>();
    private List<CriminalNetwork> network = new ArrayList<>();
    private List<TimelineEvent> timelineEvents = new ArrayList<>();
    private List<Investigation> investigations = new ArrayList<>();

    public LocalDataStore(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        log.info("Initializing LocalDataStore with seed data from classpath...");
        try {
            this.crimes = loadData("seed-data/crimes.json", new TypeReference<List<Crime>>() {});
            // Assign sequential row IDs to crimes
            for (int i = 0; i < crimes.size(); i++) {
                crimes.get(i).setRowId((long) (i + 1));
            }
            log.info("Loaded {} crimes", crimes.size());

            this.criminals = loadData("seed-data/criminals.json", new TypeReference<List<Criminal>>() {});
            log.info("Loaded {} criminals", criminals.size());

            this.links = loadData("seed-data/crime_criminal_links.json", new TypeReference<List<CrimeCriminalLink>>() {});
            // Set sequential row IDs
            for (int i = 0; i < links.size(); i++) {
                links.get(i).setRowId((long) (i + 1));
            }
            log.info("Loaded {} crime-criminal links", links.size());

            this.network = loadData("seed-data/criminal_network.json", new TypeReference<List<CriminalNetwork>>() {});
            for (int i = 0; i < network.size(); i++) {
                network.get(i).setRowId((long) (i + 1));
            }
            log.info("Loaded {} criminal network edges", network.size());

            this.timelineEvents = loadData("seed-data/timeline_events.json", new TypeReference<List<TimelineEvent>>() {});
            for (int i = 0; i < timelineEvents.size(); i++) {
                timelineEvents.get(i).setRowId((long) (i + 1));
            }
            log.info("Loaded {} timeline events", timelineEvents.size());

            // Initialize default investigations mapping to the investigations table
            investigations.add(Investigation.builder()
                    .rowId(1L)
                    .crimeRowId(1L)
                    .title("MG Road Metro Chain Snatching Investigation")
                    .status("Active")
                    .leadOfficer("Inspector Ramesh K")
                    .startedDate("2026-06-15")
                    .notes("CCTV footage recovered, track suspect vehicle registration.")
                    .build());

            investigations.add(Investigation.builder()
                    .rowId(2L)
                    .crimeRowId(3L)
                    .title("Mysuru Jewelry Store Armed Robbery Investigation")
                    .status("Active")
                    .leadOfficer("Inspector Venkatesh N")
                    .startedDate("2026-06-20")
                    .notes("Fingerprint match found for Anil D'Souza. Track getaway van.")
                    .build());
            log.info("Initialized {} investigations", investigations.size());

        } catch (Exception e) {
            log.error("Failed to load seed data in LocalDataStore: {}", e.getMessage(), e);
        }
    }

    private <T> List<T> loadData(String path, TypeReference<List<T>> typeReference) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            if (!resource.exists()) {
                log.warn("Seed file not found at path: {}", path);
                return new ArrayList<>();
            }
            try (InputStream is = resource.getInputStream()) {
                return objectMapper.readValue(is, typeReference);
            }
        } catch (Exception e) {
            log.error("Error reading JSON from path: {}", path, e);
            return new ArrayList<>();
        }
    }

    // ===== Getters =====
    public List<Crime> getCrimes() { return crimes; }
    public List<Criminal> getCriminals() { return criminals; }
    public List<CrimeCriminalLink> getLinks() { return links; }
    public List<CriminalNetwork> getNetwork() { return network; }
    public List<TimelineEvent> getTimelineEvents() { return timelineEvents; }
    public List<Investigation> getInvestigations() { return investigations; }
}
