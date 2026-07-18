package com.ksp.shodhana.repository;

import com.ksp.shodhana.model.Investigation;
import com.ksp.shodhana.util.LocalDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Investigation records.
 */
@Repository
public class InvestigationRepository {

    private static final Logger log = LoggerFactory.getLogger(InvestigationRepository.class);
    private final LocalDataStore localDataStore;

    public InvestigationRepository(LocalDataStore localDataStore) {
        this.localDataStore = localDataStore;
    }

    public Optional<Investigation> findById(Long id) {
        return localDataStore.getInvestigations().stream()
                .filter(inv -> inv.getRowId().equals(id))
                .findFirst();
    }

    public Optional<Investigation> findByCrimeId(Long crimeId) {
        return localDataStore.getInvestigations().stream()
                .filter(inv -> inv.getCrimeRowId().equals(crimeId))
                .findFirst();
    }
}
