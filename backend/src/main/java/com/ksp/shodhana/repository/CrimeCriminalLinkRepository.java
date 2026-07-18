package com.ksp.shodhana.repository;

import com.ksp.shodhana.model.CrimeCriminalLink;
import com.ksp.shodhana.util.LocalDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for Crime-Criminal link records.
 * Resolves which criminals are involved in which crimes.
 */
@Repository
public class CrimeCriminalLinkRepository {

    private static final Logger log = LoggerFactory.getLogger(CrimeCriminalLinkRepository.class);
    private final LocalDataStore localDataStore;

    public CrimeCriminalLinkRepository(LocalDataStore localDataStore) {
        this.localDataStore = localDataStore;
    }

    public List<CrimeCriminalLink> findByCrimeId(Long crimeId) {
        return localDataStore.getLinks().stream()
                .filter(link -> link.getCrimeRowId().equals(crimeId))
                .collect(Collectors.toList());
    }

    public List<CrimeCriminalLink> findByCriminalId(Long criminalId) {
        return localDataStore.getLinks().stream()
                .filter(link -> link.getCriminalRowId().equals(criminalId))
                .collect(Collectors.toList());
    }
}
