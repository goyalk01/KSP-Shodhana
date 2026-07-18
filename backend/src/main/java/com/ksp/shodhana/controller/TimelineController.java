package com.ksp.shodhana.controller;

import com.ksp.shodhana.dto.response.ApiResponse;
import com.ksp.shodhana.dto.response.WorkspacePayload;
import com.ksp.shodhana.service.TimelineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for investigation timeline events.
 */
@RestController
@RequestMapping("/api/v1/timeline")
public class TimelineController {

    private final TimelineService timelineService;

    public TimelineController(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    /** Get timeline events for a specific investigation */
    @GetMapping("/{investigationId}")
    public ApiResponse<List<WorkspacePayload.TimelineEventData>> getTimeline(
            @PathVariable Long investigationId) {
        var events = timelineService.getTimeline(investigationId);
        return ApiResponse.ok(events);
    }
}
