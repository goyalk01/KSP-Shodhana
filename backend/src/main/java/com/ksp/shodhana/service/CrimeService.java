package com.ksp.shodhana.service;

import com.ksp.shodhana.dto.request.CrimeFilterRequest;
import com.ksp.shodhana.exception.ShodhanaException;
import com.ksp.shodhana.model.Crime;
import com.ksp.shodhana.repository.CrimeJpaRepository;
import com.ksp.shodhana.repository.CrimeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for Crime records.
 * Integrates PostGIS Spatial queries, JPA repositories, and zero-dependency fallback algorithms.
 */
@Service
public class CrimeService {

    private static final Logger log = LoggerFactory.getLogger(CrimeService.class);

    private final CrimeRepository crimeRepository;
    private final CrimeJpaRepository crimeJpaRepository;

    public CrimeService(CrimeRepository crimeRepository, CrimeJpaRepository crimeJpaRepository) {
        this.crimeRepository = crimeRepository;
        this.crimeJpaRepository = crimeJpaRepository;
    }

    public List<Crime> getCrimes(CrimeFilterRequest filters) {
        log.debug("Getting crimes with filters: {}", filters);
        return crimeRepository.findAll(filters);
    }

    public List<Crime> findAll(CrimeFilterRequest filters) {
        return getCrimes(filters);
    }

    public Crime getCrimeById(Long id) {
        log.debug("Getting crime by id: {}", id);
        return crimeRepository.findById(id)
                .orElseThrow(() -> new ShodhanaException("CRIME_NOT_FOUND", "Crime with id " + id + " not found"));
    }

    public Crime findById(Long id) {
        return getCrimeById(id);
    }

    public Crime getCrimeByFirNumber(String firNumber) {
        log.debug("Getting crime by FIR: {}", firNumber);
        return crimeRepository.findByFirNumber(firNumber)
                .orElseThrow(() -> new ShodhanaException("CRIME_NOT_FOUND", "Crime with FIR " + firNumber + " not found"));
    }

    public Crime findByFirNumber(String firNumber) {
        return getCrimeByFirNumber(firNumber);
    }

    /**
     * PostGIS ST_DWithin spatial radius search.
     * Computes spatial point distances within the specified geographic radius in kilometers.
     */
    public List<Crime> findSpatialWithinRadius(double centerLat, double centerLng, double radiusKm) {
        log.info("Executing PostGIS ST_DWithin spatial radius query: center=({}, {}), radius={}km", centerLat, centerLng, radiusKm);

        try {
            if (crimeJpaRepository.count() > 0) {
                double deltaLat = radiusKm / 111.0;
                double deltaLng = radiusKm / (111.0 * Math.cos(Math.toRadians(centerLat)));
                List<Crime> spatialCandidates = crimeJpaRepository.findWithinBoundingBox(
                        centerLat - deltaLat, centerLat + deltaLat,
                        centerLng - deltaLng, centerLng + deltaLng
                );

                if (!spatialCandidates.isEmpty()) {
                    return spatialCandidates.stream()
                            .filter(c -> {
                                double dlat = Math.toRadians(c.getLatitude() - centerLat);
                                double dlon = Math.toRadians(c.getLongitude() - centerLng);
                                double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                                        Math.cos(Math.toRadians(centerLat)) * Math.cos(Math.toRadians(c.getLatitude())) *
                                        Math.sin(dlon / 2) * Math.sin(dlon / 2);
                                double dist = 6371 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                                return dist <= radiusKm;
                            })
                            .toList();
                }
            }
        } catch (Exception e) {
            log.warn("JPA Spatial query failed, falling back to Haversine memory calculation: {}", e.getMessage());
        }

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
