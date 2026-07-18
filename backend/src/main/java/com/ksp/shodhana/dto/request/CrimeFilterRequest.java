package com.ksp.shodhana.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for filtering crimes in GET /api/v1/crimes.
 * All fields are optional — acts as a filter builder.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrimeFilterRequest {

    private String crimeType;
    private String district;
    private String station;
    private String status;
    private String severity;
    private String dateFrom;      // yyyy-MM-dd
    private String dateTo;        // yyyy-MM-dd
    private String searchText;    // Full-text search on description/MO
    private Integer page;
    private Integer pageSize;
}
