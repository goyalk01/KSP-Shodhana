package com.ksp.shodhana.controller;

import com.ksp.shodhana.dto.response.ApiResponse;
import com.ksp.shodhana.model.Criminal;
import com.ksp.shodhana.service.CriminalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Criminal record operations.
 */
@RestController
@RequestMapping("/api/v1/criminals")
public class CriminalController {

    private final CriminalService criminalService;

    public CriminalController(CriminalService criminalService) {
        this.criminalService = criminalService;
    }

    /** List criminals with optional filters */
    @GetMapping
    public ApiResponse<List<Criminal>> listCriminals(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        List<Criminal> criminals = criminalService.findAll(district, riskLevel, status, search);
        return ApiResponse.ok(criminals);
    }

    /** Get a single criminal by ROWID */
    @GetMapping("/{id}")
    public ApiResponse<Criminal> getCriminal(@PathVariable Long id) {
        Criminal criminal = criminalService.findById(id);
        return ApiResponse.ok(criminal);
    }
}
