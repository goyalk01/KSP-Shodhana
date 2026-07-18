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
 * Repository for Crime records.
 * Uses LocalDataStore for local/demo mode.
 * When deployed to Zoho Catalyst, this can be extended to use ZCQL via the Catalyst SDK.
 */
@Repository
public class CrimeRepository {

    private static final Logger log = LoggerFactory.getLogger(CrimeRepository.class);
    private final LocalDataStore localDataStore;

    public CrimeRepository(LocalDataStore localDataStore) {
        this.localDataStore = localDataStore;
    }

    public List<Crime> findAll(CrimeFilterRequest filters) {
        log.debug("CrimeRepository.findAll called with filters: {}", filters);

        Stream<Crime> stream = localDataStore.getCrimes().stream();

        if (filters.getCrimeType() != null && !filters.getCrimeType().isEmpty()) {
            stream = stream.filter(c -> c.getCrimeType().equalsIgnoreCase(filters.getCrimeType()));
        }
        if (filters.getDistrict() != null && !filters.getDistrict().isEmpty()) {
            stream = stream.filter(c -> c.getDistrict().equalsIgnoreCase(filters.getDistrict()));
        }
        if (filters.getStation() != null && !filters.getStation().isEmpty()) {
            stream = stream.filter(c -> c.getStation().equalsIgnoreCase(filters.getStation()));
        }
        if (filters.getStatus() != null && !filters.getStatus().isEmpty()) {
            stream = stream.filter(c -> c.getStatus().equalsIgnoreCase(filters.getStatus()));
        }
        if (filters.getSeverity() != null && !filters.getSeverity().isEmpty()) {
            stream = stream.filter(c -> c.getSeverity().equalsIgnoreCase(filters.getSeverity()));
        }
        if (filters.getSearchText() != null && !filters.getSearchText().isEmpty()) {
            String search = filters.getSearchText().toLowerCase();
            stream = stream.filter(c ->
                    c.getDescription().toLowerCase().contains(search) ||
                    (c.getModusOperandi() != null && c.getModusOperandi().toLowerCase().contains(search)) ||
                    (c.getFirNumber() != null && c.getFirNumber().toLowerCase().contains(search))
            );
        }

        List<Crime> results = stream.collect(Collectors.toList());
        log.debug("Found {} crimes", results.size());
        return results;
    }

    public Optional<Crime> findById(Long id) {
        return localDataStore.getCrimes().stream()
                .filter(c -> c.getRowId().equals(id))
                .findFirst();
    }

    public Optional<Crime> findByFirNumber(String firNumber) {
        return localDataStore.getCrimes().stream()
                .filter(c -> c.getFirNumber().equalsIgnoreCase(firNumber))
                .findFirst();
    }
}
