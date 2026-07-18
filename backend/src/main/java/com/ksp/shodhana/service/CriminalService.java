package com.ksp.shodhana.service;

import com.ksp.shodhana.exception.ShodhanaException;
import com.ksp.shodhana.model.Criminal;
import com.ksp.shodhana.repository.CriminalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for Criminal record operations.
 */
@Service
public class CriminalService {

    private static final Logger log = LoggerFactory.getLogger(CriminalService.class);
    private final CriminalRepository criminalRepository;

    public CriminalService(CriminalRepository criminalRepository) {
        this.criminalRepository = criminalRepository;
    }

    public List<Criminal> findAll(String district, String riskLevel, String status, String search) {
        log.debug("Finding criminals - district: {}, risk: {}, status: {}, search: {}",
                district, riskLevel, status, search);
        return criminalRepository.findAll(district, riskLevel, status, search);
    }

    public Criminal findById(Long id) {
        log.debug("Finding criminal by ID: {}", id);
        return criminalRepository.findById(id)
                .orElseThrow(() -> new ShodhanaException("CRIMINAL_NOT_FOUND", "Criminal with ID " + id + " not found"));
    }
}
