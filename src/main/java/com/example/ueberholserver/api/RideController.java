package com.example.ueberholserver.api;

import com.example.ueberholserver.api.dto.RideUploadRequest;
import com.example.ueberholserver.api.dto.RideUploadResponse;
import com.example.ueberholserver.db.entity.RideEntity;
import com.example.ueberholserver.db.repo.RideRepository;
import com.example.ueberholserver.service.RideService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;
    private final RideRepository rideRepository;

    public RideController(RideService rideService, RideRepository rideRepository) {
        this.rideService = rideService;
        this.rideRepository = rideRepository;
    }

    @PostMapping
    public RideUploadResponse upload(@Valid @RequestBody RideUploadRequest request) {
        String serverRideId = rideService.uploadRide(request);
        return new RideUploadResponse(serverRideId);
    }

    @GetMapping("/{id}")
    public RideEntity get(@PathVariable String id) {
        return rideRepository.findById(id).orElseThrow();
    }

    @GetMapping
    public List<RideEntity> list() {
        return rideRepository.findAll();
    }
}