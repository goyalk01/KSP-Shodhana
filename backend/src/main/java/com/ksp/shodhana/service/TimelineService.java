package com.ksp.shodhana.service;

import com.ksp.shodhana.dto.response.WorkspacePayload;
import com.ksp.shodhana.model.TimelineEvent;
import com.ksp.shodhana.repository.TimelineEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for investigation timeline operations.
 */
@Service
public class TimelineService {

    private static final Logger log = LoggerFactory.getLogger(TimelineService.class);
    private final TimelineEventRepository timelineEventRepository;

    public TimelineService(TimelineEventRepository timelineEventRepository) {
        this.timelineEventRepository = timelineEventRepository;
    }

    public List<WorkspacePayload.TimelineEventData> getTimeline(Long investigationId) {
        log.debug("Getting timeline for investigation {}", investigationId);
        List<TimelineEvent> events = timelineEventRepository.findByInvestigationId(investigationId);
        
        return events.stream()
                .map(event -> WorkspacePayload.TimelineEventData.builder()
                        .id(event.getRowId().toString())
                        .eventType(event.getEventType())
                        .eventDate(event.getEventDate())
                        .title(event.getTitle())
                        .description(event.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
