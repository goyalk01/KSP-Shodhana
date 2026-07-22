package com.ksp.shodhana.controller;

import com.ksp.shodhana.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller for handling root HTTP GET / requests.
 */
@RestController
public class RootController {

    @GetMapping("/")
    public ApiResponse<Map<String, String>> rootStatus() {
        return ApiResponse.ok(Map.of(
                "status", "UP",
                "service", "KSP Shodhana Backend API",
                "version", "0.1.0"
        ));
    }
}
