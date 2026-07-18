package com.ksp.shodhana.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing a Crime record.
 * Maps to the "Crime" table in Catalyst Data Store.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Crime {

    private Long rowId;
    private String firNumber;
    private String crimeType;
    private String description;
    private String status;
    private String severity;
    private String dateReported;
    private String dateOccurred;
    private String district;
    private String station;
    private Double latitude;
    private Double longitude;
    private String address;
    private String investigatingOfficer;
    private String weaponUsed;
    private String modusOperandi;
    private String createdTime;
    private String modifiedTime;
}
