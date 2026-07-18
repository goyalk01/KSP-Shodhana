package com.ksp.shodhana.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing a relationship between two criminals.
 * Maps to the "CriminalNetwork" table in Catalyst Data Store.
 * Used to build the criminal network graph.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriminalNetwork {

    private Long rowId;
    private Long criminalARowId;
    private Long criminalBRowId;
    private String relationshipType; // Associate, Gang Member, Family, Rival
    private Integer strength;        // 1-10
    private String evidenceSummary;
}
