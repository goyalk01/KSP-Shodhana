package com.ksp.shodhana.config;

import com.ksp.shodhana.model.Crime;
import com.ksp.shodhana.model.Criminal;
import com.ksp.shodhana.repository.CrimeJpaRepository;
import com.ksp.shodhana.repository.CriminalJpaRepository;
import com.ksp.shodhana.util.LocalDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Spring Boot Data Initializer.
 * Seeds initial Crime and Criminal records into JPA database (H2 / PostgreSQL) on startup.
 */
@Component
public class JpaDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(JpaDataInitializer.class);

    private final CrimeJpaRepository crimeJpaRepository;
    private final CriminalJpaRepository criminalJpaRepository;
    private final LocalDataStore localDataStore;

    public JpaDataInitializer(
            CrimeJpaRepository crimeJpaRepository,
            CriminalJpaRepository criminalJpaRepository,
            LocalDataStore localDataStore) {
        this.crimeJpaRepository = crimeJpaRepository;
        this.criminalJpaRepository = criminalJpaRepository;
        this.localDataStore = localDataStore;
    }

    @Override
    public void run(String... args) {
        log.info("Checking Spring Data JPA Database state...");

        if (crimeJpaRepository.count() == 0) {
            List<Crime> crimes = localDataStore.getCrimes();
            for (Crime c : crimes) {
                c.syncLocation();
            }
            crimeJpaRepository.saveAll(crimes);
            log.info("Successfully populated {} Crime entities into JPA Database", crimes.size());
        } else {
            log.info("Crime JPA Database already populated: {} records", crimeJpaRepository.count());
        }

        if (criminalJpaRepository.count() == 0) {
            List<Criminal> criminals = localDataStore.getCriminals();
            criminalJpaRepository.saveAll(criminals);
            log.info("Successfully populated {} Criminal entities into JPA Database", criminals.size());
        } else {
            log.info("Criminal JPA Database already populated: {} records", criminalJpaRepository.count());
        }
    }
}
