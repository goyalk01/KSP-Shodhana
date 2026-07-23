package com.ksp.shodhana.repository;

import com.ksp.shodhana.model.Criminal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for Criminal entities.
 */
@Repository
public interface CriminalJpaRepository extends JpaRepository<Criminal, Long> {

    List<Criminal> findByDistrictIgnoreCase(String district);

    List<Criminal> findByRiskLevelIgnoreCase(String riskLevel);

    List<Criminal> findByStatusIgnoreCase(String status);

    List<Criminal> findByNameContainingIgnoreCase(String name);
}
