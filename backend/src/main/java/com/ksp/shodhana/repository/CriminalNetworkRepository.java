package com.ksp.shodhana.repository;

import com.ksp.shodhana.model.CriminalNetwork;
import com.ksp.shodhana.util.LocalDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for CriminalNetwork (relationship edges between criminals).
 * Used by NetworkService to build the force-directed graph.
 */
@Repository
public class CriminalNetworkRepository {

    private static final Logger log = LoggerFactory.getLogger(CriminalNetworkRepository.class);
    private final LocalDataStore localDataStore;

    public CriminalNetworkRepository(LocalDataStore localDataStore) {
        this.localDataStore = localDataStore;
    }

    public List<CriminalNetwork> findByCriminalId(Long criminalId) {
        return localDataStore.getNetwork().stream()
                .filter(edge -> edge.getCriminalARowId().equals(criminalId)
                        || edge.getCriminalBRowId().equals(criminalId))
                .collect(Collectors.toList());
    }

    public List<CriminalNetwork> findAll() {
        return localDataStore.getNetwork();
    }
}
