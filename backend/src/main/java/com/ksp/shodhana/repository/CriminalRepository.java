package com.ksp.shodhana.repository;

import com.ksp.shodhana.model.Criminal;
import com.ksp.shodhana.util.LocalDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Repository for Criminal records.
 * Uses LocalDataStore for local/demo mode.
 */
@Repository
public class CriminalRepository {

    private static final Logger log = LoggerFactory.getLogger(CriminalRepository.class);
    private final LocalDataStore localDataStore;

    public CriminalRepository(LocalDataStore localDataStore) {
        this.localDataStore = localDataStore;
    }

    public List<Criminal> findAll(String district, String riskLevel, String status, String search) {
        log.debug("CriminalRepository.findAll - district: {}, risk: {}, status: {}, search: {}",
                district, riskLevel, status, search);

        Stream<Criminal> stream = localDataStore.getCriminals().stream();

        if (district != null && !district.isEmpty()) {
            stream = stream.filter(c -> c.getDistrict().equalsIgnoreCase(district));
        }
        if (riskLevel != null && !riskLevel.isEmpty()) {
            stream = stream.filter(c -> c.getRiskLevel().equalsIgnoreCase(riskLevel));
        }
        if (status != null && !status.isEmpty()) {
            stream = stream.filter(c -> c.getStatus().equalsIgnoreCase(status));
        }
        if (search != null && !search.isEmpty()) {
            String searchLower = search.toLowerCase();
            stream = stream.filter(c ->
                    c.getName().toLowerCase().contains(searchLower) ||
                    (c.getAlias() != null && c.getAlias().toLowerCase().contains(searchLower))
            );
        }

        List<Criminal> results = stream.collect(Collectors.toList());
        log.debug("Found {} criminals", results.size());
        return results;
    }

    public Optional<Criminal> findById(Long id) {
        return localDataStore.getCriminals().stream()
                .filter(c -> c.getRowId().equals(id))
                .findFirst();
    }
}
