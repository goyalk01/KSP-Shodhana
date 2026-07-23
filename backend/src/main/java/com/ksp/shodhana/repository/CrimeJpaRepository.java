package com.ksp.shodhana.repository;

import com.ksp.shodhana.model.Crime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Crime entities.
 * Supports PostGIS spatial queries (ST_DWithin) and standard JPA dynamic queries.
 */
@Repository
public interface CrimeJpaRepository extends JpaRepository<Crime, Long> {

    Optional<Crime> findByFirNumberIgnoreCase(String firNumber);

    List<Crime> findByCrimeTypeIgnoreCase(String crimeType);

    List<Crime> findByDistrictIgnoreCase(String district);

    List<Crime> findByStationIgnoreCase(String station);

    List<Crime> findByStatusIgnoreCase(String status);

    List<Crime> findBySeverityIgnoreCase(String severity);

    /**
     * Bounding box radius search query for distance calculations.
     */
    @Query("SELECT c FROM Crime c WHERE c.latitude BETWEEN :minLat AND :maxLat AND c.longitude BETWEEN :minLng AND :maxLng")
    List<Crime> findWithinBoundingBox(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLng") Double minLng,
            @Param("maxLng") Double maxLng
    );
}
