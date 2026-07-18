package com.ksp.shodhana.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing an event in an investigation timeline.
 * Maps to the "TimelineEvent" table in Catalyst Data Store.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineEvent {

    private Long rowId;
    private Long investigationRowId;
    private String eventType;       // FIR Filed, Arrest, Witness Statement, Evidence Found, etc.
    private String eventDate;
    private String title;
    private String description;
    private String createdBy;
}
