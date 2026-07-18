package com.ksp.shodhana.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing an Investigation.
 * Maps to the "Investigation" table in Catalyst Data Store.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Investigation {

    private Long rowId;
    private Long crimeRowId;
    private String title;
    private String status;        // Active, Closed, Pending
    private String leadOfficer;
    private String startedDate;
    private String closedDate;
    private String notes;
    private String createdTime;
    private String modifiedTime;
}
