package com.ksp.shodhana.controller;

import com.ksp.shodhana.dto.response.ApiResponse;
import com.ksp.shodhana.dto.response.WorkspacePayload;
import com.ksp.shodhana.service.NetworkService;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for criminal network graph operations.
 */
@RestController
@RequestMapping("/api/v1/network")
public class NetworkController {

    private final NetworkService networkService;

    public NetworkController(NetworkService networkService) {
        this.networkService = networkService;
    }

    /** Get network graph centered on a specific criminal */
    @GetMapping("/{criminalId}")
    public ApiResponse<WorkspacePayload.NetworkGraphData> getNetworkByCriminal(
            @PathVariable Long criminalId,
            @RequestParam(defaultValue = "2") int depth) {
        var graphData = networkService.getNetworkByCriminal(criminalId, depth);
        return ApiResponse.ok(graphData);
    }

    /** Get network graph for all criminals linked to a crime */
    @GetMapping("/crime/{crimeId}")
    public ApiResponse<WorkspacePayload.NetworkGraphData> getNetworkByCrime(
            @PathVariable Long crimeId) {
        var graphData = networkService.getNetworkByCrime(crimeId);
        return ApiResponse.ok(graphData);
    }
}
