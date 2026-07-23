package com.ksp.shodhana.repository;

import com.ksp.shodhana.model.Crime;
import com.ksp.shodhana.model.Criminal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("demo")
class JpaRepositoryTest {

    @Autowired
    private CrimeJpaRepository crimeJpaRepository;

    @Autowired
    private CriminalJpaRepository criminalJpaRepository;

    @Test
    @DisplayName("Should successfully query Crime entities from JPA Database")
    void testCrimeJpaRepository() {
        List<Crime> crimes = crimeJpaRepository.findAll();
        assertThat(crimes).isNotEmpty();
        assertThat(crimes.size()).isGreaterThanOrEqualTo(16);

        Crime crime = crimes.get(0);
        assertThat(crime.getRowId()).isNotNull();
        assertThat(crime.getFirNumber()).isNotNull();
        assertThat(crime.getLatitude()).isNotNull();
        assertThat(crime.getLongitude()).isNotNull();
    }

    @Test
    @DisplayName("Should successfully query Criminal entities from JPA Database")
    void testCriminalJpaRepository() {
        List<Criminal> criminals = criminalJpaRepository.findAll();
        assertThat(criminals).isNotEmpty();
        assertThat(criminals.size()).isGreaterThanOrEqualTo(16);

        Criminal criminal = criminals.get(0);
        assertThat(criminal.getRowId()).isNotNull();
        assertThat(criminal.getName()).isNotNull();
    }

    @Test
    @DisplayName("Should execute spatial bounding box query using JPA")
    void testSpatialBoundingBoxQuery() {
        // Bangalore Central Coordinates (approx 12.97, 77.59)
        List<Crime> spatialCrimes = crimeJpaRepository.findWithinBoundingBox(12.80, 13.10, 77.40, 77.70);
        assertThat(spatialCrimes).isNotEmpty();
    }
}
