package com.ksp.shodhana.repository;

import com.ksp.shodhana.model.TimelineEvent;
import com.ksp.shodhana.util.LocalDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for TimelineEvent records.
 * Used by TimelineService to build investigation timelines.
 */
@Repository
public class TimelineEventRepository {

    private static final Logger log = LoggerFactory.getLogger(TimelineEventRepository.class);
    private final LocalDataStore localDataStore;

    public TimelineEventRepository(LocalDataStore localDataStore) {
        this.localDataStore = localDataStore;
    }

    public List<TimelineEvent> findByInvestigationId(Long investigationId) {
        return localDataStore.getTimelineEvents().stream()
                .filter(event -> event.getInvestigationRowId().equals(investigationId))
                .collect(Collectors.toList());
    }
}
