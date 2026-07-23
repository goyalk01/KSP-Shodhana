package com.ksp.shodhana.repository;

import com.ksp.shodhana.dto.request.CrimeFilterRequest;
import com.ksp.shodhana.model.Crime;
import com.ksp.shodhana.util.LocalDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Hybrid Domain Repository for Crime entities.
 * Delegates to CrimeJpaRepository (Spring Data JPA) when database is populated,
 * with zero-dependency fallback to LocalDataStore in demo mode.
 */
@Repository
public class CrimeRepository {

    private static final Logger log = LoggerFactory.getLogger(CrimeRepository.class);

    private final CrimeJpaRepository crimeJpaRepository;
    private final LocalDataStore localDataStore;

    public CrimeRepository(CrimeJpaRepository crimeJpaRepository, LocalDataStore localDataStore) {
        this.crimeJpaRepository = crimeJpaRepository;
        this.localDataStore = localDataStore;
    }

    public List<Crime> findAll(CrimeFilterRequest filters) {
        log.debug("CrimeRepository.findAll called with filters: {}", filters);

        List<Crime> sourceList;
        try {
            if (crimeJpaRepository.count() > 0) {
                sourceList = crimeJpaRepository.findAll();
            } else {
                sourceList = localDataStore.getCrimes();
            }
        } catch (Exception e) {
            log.warn("JPA Repository query failed, falling back to LocalDataStore: {}", e.getMessage());
            sourceList = localDataStore.getCrimes();
        }

        Stream<Crime> stream = sourceList.stream();

        if (filters != null) {
            if (filters.getCrimeType() != null && !filters.getCrimeType().isEmpty()) {
                stream = stream.filter(c -> c.getCrimeType() != null && c.getCrimeType().equalsIgnoreCase(filters.getCrimeType()));
            }
            if (filters.getDistrict() != null && !filters.getDistrict().isEmpty()) {
                stream = stream.filter(c -> c.getDistrict() != null && c.getDistrict().equalsIgnoreCase(filters.getDistrict()));
            }
            if (filters.getStation() != null && !filters.getStation().isEmpty()) {
                stream = stream.filter(c -> c.getStation() != null && c.getStation().equalsIgnoreCase(filters.getStation()));
            }
            if (filters.getStatus() != null && !filters.getStatus().isEmpty()) {
                stream = stream.filter(c -> c.getStatus() != null && c.getStatus().equalsIgnoreCase(filters.getStatus()));
            }
            if (filters.getSeverity() != null && !filters.getSeverity().isEmpty()) {
                stream = stream.filter(c -> c.getSeverity() != null && c.getSeverity().equalsIgnoreCase(filters.getSeverity()));
            }
            if (filters.getSearchText() != null && !filters.getSearchText().isEmpty()) {
                String search = filters.getSearchText().toLowerCase();
                stream = stream.filter(c ->
                        (c.getDescription() != null && c.getDescription().toLowerCase().contains(search)) ||
                        (c.getModusOperandi() != null && c.getModusOperandi().toLowerCase().contains(search)) ||
                        (c.getFirNumber() != null && c.getFirNumber().toLowerCase().contains(search))
                );
            }
        }

        List<Crime> results = stream.collect(Collectors.toList());
        log.debug("Found {} crimes", results.size());
        return results;
    }

    public Optional<Crime> findById(Long id) {
        try {
            Optional<Crime> jpaResult = crimeJpaRepository.findById(id);
            if (jpaResult.isPresent()) {
                return jpaResult;
            }
        } catch (Exception e) {
            log.warn("JPA findById failed, falling back to LocalDataStore: {}", e.getMessage());
        }

        return localDataStore.getCrimes().stream()
                .filter(c -> c.getRowId() != null && c.getRowId().equals(id))
                .findFirst();
    }

    public Optional<Crime> findByFirNumber(String firNumber) {
        try {
            Optional<Crime> jpaResult = crimeJpaRepository.findByFirNumberIgnoreCase(firNumber);
            if (jpaResult.isPresent()) {
                return jpaResult;
            }
        } catch (Exception e) {
            log.warn("JPA findByFirNumber failed, falling back to LocalDataStore: {}", e.getMessage());
        }

        return localDataStore.getCrimes().stream()
                .filter(c -> c.getFirNumber() != null && c.getFirNumber().equalsIgnoreCase(firNumber))
                .findFirst();
    }
}
