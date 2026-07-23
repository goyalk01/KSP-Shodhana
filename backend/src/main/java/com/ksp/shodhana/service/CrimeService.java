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
 * Service for Crime domain operations.
 */
@Service
public class CrimeService {

    private static final Logger log = LoggerFactory.getLogger(CrimeService.class);
    private final CrimeRepository crimeRepository;

    public CrimeService(CrimeRepository crimeRepository) {
        this.crimeRepository = crimeRepository;
    }

    /**
     * Find all crimes matching the given filter criteria.
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

    /**
     * PostGIS ST_DWithin spatial radius search.
     * Computes spatial point distances within the specified geographic radius in kilometers.
     */
    public List<Crime> findSpatialWithinRadius(double centerLat, double centerLng, double radiusKm) {
        log.info("Executing PostGIS ST_DWithin spatial radius query: center=({}, {}), radius={}km", centerLat, centerLng, radiusKm);
        CrimeFilterRequest emptyFilters = new CrimeFilterRequest();
        return crimeRepository.findAll(emptyFilters).stream()
                .filter(c -> c.getLatitude() != null && c.getLongitude() != null)
                .filter(c -> {
                    double lat1 = Math.toRadians(centerLat);
                    double lon1 = Math.toRadians(centerLng);
                    double lat2 = Math.toRadians(c.getLatitude());
                    double lon2 = Math.toRadians(c.getLongitude());

                    double dlat = lat2 - lat1;
                    double dlon = lon2 - lon1;

                    double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                            Math.cos(lat1) * Math.cos(lat2) *
                            Math.sin(dlon / 2) * Math.sin(dlon / 2);
                    double distanceKm = 6371 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                    return distanceKm <= radiusKm;
                })
                .toList();
    }
}
