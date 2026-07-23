package com.ksp.shodhana.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing a Criminal record.
 * Maps to the "criminals" table in PostgreSQL/PostGIS & Spring Data JPA.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "criminals")
public class Criminal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "row_id")
    private Long rowId;

    @Column(name = "name")
    private String name;

    @Column(name = "alias")
    private String alias;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    private String gender;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address", length = 1000)
    private String address;

    @Column(name = "district")
    private String district;

    @Column(name = "criminal_history", length = 2000)
    private String criminalHistory;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "status")
    private String status;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "fingerprint_id")
    private String fingerprintId;

    // Official KSP Accused ER Diagram Alignment Columns
    @Column(name = "accused_master_id")
    private Integer accusedMasterId;

    @Column(name = "accused_name")
    private String accusedName;

    @Column(name = "age_year")
    private Integer ageYear;

    @Column(name = "gender_id")
    private Integer genderId;

    @Column(name = "person_id")
    private String personId;

    @Column(name = "created_time")
    private String createdTime;

    @Column(name = "modified_time")
    private String modifiedTime;
}
