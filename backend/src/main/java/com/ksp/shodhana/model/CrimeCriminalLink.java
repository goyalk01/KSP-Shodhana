package com.ksp.shodhana.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing a link between a crime and a criminal.
 * Maps to the "CrimeCriminalLink" table in Catalyst Data Store.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrimeCriminalLink {

    private Long rowId;
    private Long crimeRowId;
    private Long criminalRowId;
    private String role;            // Accused, Suspect, Witness, Victim
    private String involvementDetail;
}
