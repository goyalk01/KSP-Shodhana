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
 * Hybrid Domain Repository for Criminal entities.
 * Delegates to CriminalJpaRepository (Spring Data JPA) when database is populated,
 * with zero-dependency fallback to LocalDataStore in demo mode.
 */
@Repository
public class CriminalRepository {

    private static final Logger log = LoggerFactory.getLogger(CriminalRepository.class);

    private final CriminalJpaRepository criminalJpaRepository;
    private final LocalDataStore localDataStore;

    public CriminalRepository(CriminalJpaRepository criminalJpaRepository, LocalDataStore localDataStore) {
        this.criminalJpaRepository = criminalJpaRepository;
        this.localDataStore = localDataStore;
    }

    public List<Criminal> findAll(String district, String riskLevel, String status, String search) {
        log.debug("CriminalRepository.findAll - district: {}, risk: {}, status: {}, search: {}",
                district, riskLevel, status, search);

        List<Criminal> sourceList;
        try {
            if (criminalJpaRepository.count() > 0) {
                sourceList = criminalJpaRepository.findAll();
            } else {
                sourceList = localDataStore.getCriminals();
            }
        } catch (Exception e) {
            log.warn("JPA Criminal query failed, falling back to LocalDataStore: {}", e.getMessage());
            sourceList = localDataStore.getCriminals();
        }

        Stream<Criminal> stream = sourceList.stream();

        if (district != null && !district.isEmpty()) {
            stream = stream.filter(c -> c.getDistrict() != null && c.getDistrict().equalsIgnoreCase(district));
        }
        if (riskLevel != null && !riskLevel.isEmpty()) {
            stream = stream.filter(c -> c.getRiskLevel() != null && c.getRiskLevel().equalsIgnoreCase(riskLevel));
        }
        if (status != null && !status.isEmpty()) {
            stream = stream.filter(c -> c.getStatus() != null && c.getStatus().equalsIgnoreCase(status));
        }
        if (search != null && !search.isEmpty()) {
            String searchLower = search.toLowerCase();
            stream = stream.filter(c ->
                    (c.getName() != null && c.getName().toLowerCase().contains(searchLower)) ||
                    (c.getAlias() != null && c.getAlias().toLowerCase().contains(searchLower))
            );
        }

        List<Criminal> results = stream.collect(Collectors.toList());
        log.debug("Found {} criminals", results.size());
        return results;
    }

    public Optional<Criminal> findById(Long id) {
        try {
            Optional<Criminal> jpaResult = criminalJpaRepository.findById(id);
            if (jpaResult.isPresent()) {
                return jpaResult;
            }
        } catch (Exception e) {
            log.warn("JPA findById failed, falling back to LocalDataStore: {}", e.getMessage());
        }

        return localDataStore.getCriminals().stream()
                .filter(c -> c.getRowId() != null && c.getRowId().equals(id))
                .findFirst();
    }
}
