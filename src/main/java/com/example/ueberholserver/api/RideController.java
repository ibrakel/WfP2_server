package com.example.ueberholserver.api;

import com.example.ueberholserver.api.dto.RideSummaryDto;
import com.example.ueberholserver.api.dto.RideUploadRequest;
import com.example.ueberholserver.api.dto.RideUploadResponse;
import com.example.ueberholserver.db.entity.RideEntity;
import com.example.ueberholserver.db.repo.RideRepository;
import com.example.ueberholserver.service.CsvExportService;
import com.example.ueberholserver.service.RideService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;
    private final RideRepository rideRepository;
    private final CsvExportService csvExportService;

    public RideController(RideService rideService,
                          RideRepository rideRepository,
                          CsvExportService csvExportService) {
        this.rideService       = rideService;
        this.rideRepository    = rideRepository;
        this.csvExportService  = csvExportService;
    }

    // ── POST /api/rides ───────────────────────────────────────────────────

    @PostMapping
    public RideUploadResponse upload(@Valid @RequestBody RideUploadRequest request) {
        String serverRideId = rideService.uploadRide(request);
        return new RideUploadResponse(serverRideId);
    }

    // ── GET /api/rides ────────────────────────────────────────────────────

    @GetMapping
    public Page<RideSummaryDto> list(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        PageRequest pageable = PageRequest.of(
                page, size, Sort.by(Sort.Direction.DESC, "startedAtMs"));
        return rideRepository.findAllSummaries(pageable);
    }

    // ── GET /api/rides/{id} ───────────────────────────────────────────────

    @GetMapping("/{id}")
    public RideEntity get(@PathVariable String id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ride not found: " + id));
    }

    // ── DELETE /api/rides/{id} ────────────────────────────────────────────

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        RideEntity ride = rideRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ride not found: " + id));
        rideRepository.delete(ride);   // CascadeType.ALL + orphanRemoval handles children
        return ResponseEntity.noContent().build();
    }

    // ── GET /api/rides/{id}/export/csv ────────────────────────────────────

    @GetMapping("/{id}/export/csv")
    public ResponseEntity<byte[]> exportCsv(@PathVariable String id) {
        RideEntity ride = rideRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ride not found: " + id));

        String csv      = csvExportService.buildCsv(ride);
        String filename = csvExportService.buildFilename(ride);
        byte[] bytes    = csv.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .body(bytes);
    }
}
