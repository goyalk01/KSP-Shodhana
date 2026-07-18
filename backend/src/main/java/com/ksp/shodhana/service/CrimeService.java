package com.ksp.shodhana.service;

import com.ksp.shodhana.dto.request.CrimeFilterRequest;
import com.ksp.shodhana.exception.ShodhanaException;
import com.ksp.shodhana.model.Crime;
import com.ksp.shodhana.repository.CrimeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for Crime operations.
 * Handles business logic and delegates to repository for data access.
 */
@Service
public class CrimeService {

    private static final Logger log = LoggerFactory.getLogger(CrimeService.class);
    private final CrimeRepository crimeRepository;

    public CrimeService(CrimeRepository crimeRepository) {
        this.crimeRepository = crimeRepository;
    }

    /**
     * Find all crimes matching the given filters.
     */
    public List<Crime> findAll(CrimeFilterRequest filters) {
        log.debug("Finding crimes with filters: {}", filters);
        return crimeRepository.findAll(filters);
    }

    /**
     * Find a crime by its ROWID.
     * @throws ShodhanaException if not found
     */
    public Crime findById(Long id) {
        log.debug("Finding crime by ID: {}", id);
        return crimeRepository.findById(id)
                .orElseThrow(() -> new ShodhanaException("CRIME_NOT_FOUND", "Crime with ID " + id + " not found"));
    }

    /**
     * Find a crime by its FIR Number.
     */
    public Crime findByFirNumber(String firNumber) {
        log.debug("Finding crime by FIR: {}", firNumber);
        return crimeRepository.findByFirNumber(firNumber)
                .orElseThrow(() -> new ShodhanaException("CRIME_NOT_FOUND", "Crime with FIR " + firNumber + " not found"));
    }
}
