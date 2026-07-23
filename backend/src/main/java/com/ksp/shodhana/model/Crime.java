package com.ksp.shodhana.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

/**
 * Domain model representing a Crime record.
 * Maps to the "crimes" table in PostgreSQL/PostGIS & Spring Data JPA.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "crimes")
public class Crime {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "row_id")
    private Long rowId;

    @Column(name = "fir_number")
    private String firNumber;

    @Column(name = "crime_type")
    private String crimeType;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "severity")
    private String severity;

    @Column(name = "date_reported")
    private String dateReported;

    @Column(name = "date_occurred")
    private String dateOccurred;

    @Column(name = "district")
    private String district;

    @Column(name = "station")
    private String station;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    /**
     * LocationTech Point field mapped for PostGIS / Spatial Queries (ST_DWithin).
     */
    @JsonIgnore
    @Column(name = "location", columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @Column(name = "address", length = 1000)
    private String address;

    @Column(name = "investigating_officer")
    private String investigatingOfficer;

    @Column(name = "weapon_used")
    private String weaponUsed;

    @Column(name = "modus_operandi", length = 1000)
    private String modusOperandi;

    // Official KSP CaseMaster ER Diagram Alignment Columns
    @Column(name = "crime_no")
    private String crimeNo;

    @Column(name = "case_no")
    private String caseNo;

    @Column(name = "brief_facts", length = 2000)
    private String briefFacts;

    @Column(name = "incident_from_date")
    private String incidentFromDate;

    @Column(name = "incident_to_date")
    private String incidentToDate;

    @Column(name = "info_received_ps_date")
    private String infoReceivedPSDate;

    @Column(name = "police_station_id")
    private Integer policeStationId;

    @Column(name = "police_person_id")
    private Integer policePersonId;

    @Column(name = "case_category_id")
    private Integer caseCategoryId;

    @Column(name = "gravity_offence_id")
    private Integer gravityOffenceId;

    @Column(name = "crime_major_head_id")
    private Integer crimeMajorHeadId;

    @Column(name = "crime_minor_head_id")
    private Integer crimeMinorHeadId;

    @Column(name = "created_time")
    private String createdTime;

    @Column(name = "modified_time")
    private String modifiedTime;

    @PrePersist
    @PreUpdate
    public void syncLocation() {
        if (latitude != null && longitude != null) {
            this.location = GEOMETRY_FACTORY.createPoint(new Coordinate(longitude, latitude));
        }
    }
}
