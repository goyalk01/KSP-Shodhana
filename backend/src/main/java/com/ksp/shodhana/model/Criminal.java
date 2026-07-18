package com.ksp.shodhana.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing a Criminal record.
 * Maps to the "Criminal" table in Catalyst Data Store.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Criminal {

    private Long rowId;
    private String name;
    private String alias;
    private Integer age;
    private String gender;
    private String idNumber;
    private String phone;
    private String address;
    private String district;
    private String criminalHistory;
    private String riskLevel;
    private String status;
    private String photoUrl;
    private String fingerprintId;
    private String createdTime;
    private String modifiedTime;
}
