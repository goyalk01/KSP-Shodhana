package com.ksp.shodhana.controller;

import com.ksp.shodhana.dto.request.CrimeFilterRequest;
import com.ksp.shodhana.dto.response.ApiResponse;
import com.ksp.shodhana.model.Crime;
import com.ksp.shodhana.service.CrimeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Crime management endpoints.
 */
@RestController
@RequestMapping("/api/v1/crimes")
public class CrimeController {

    private final CrimeService crimeService;

    public CrimeController(CrimeService crimeService) {
        this.crimeService = crimeService;
    }

    /** List crimes with optional filters */
    @GetMapping
    public ApiResponse<List<Crime>> listCrimes(CrimeFilterRequest filters) {
        List<Crime> crimes = crimeService.findAll(filters);
        return ApiResponse.ok(crimes);
    }

    /** PostGIS ST_DWithin Spatial Radius Search (Static route mapped before /{id}) */
    @GetMapping("/spatial/radius")
    public ApiResponse<List<Crime>> getCrimesSpatialRadius(
            @RequestParam(defaultValue = "12.9716") double lat,
            @RequestParam(defaultValue = "77.5946") double lng,
            @RequestParam(defaultValue = "15.0") double radiusKm) {
        List<Crime> crimes = crimeService.findSpatialWithinRadius(lat, lng, radiusKm);
        return ApiResponse.ok(crimes);
    }

    /** Get a single crime by ROWID */
    @GetMapping("/{id}")
    public ApiResponse<Crime> getCrime(@PathVariable Long id) {
        Crime crime = crimeService.findById(id);
        return ApiResponse.ok(crime);
    }
}
