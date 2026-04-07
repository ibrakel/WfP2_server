package com.example.ueberholserver.service;

import com.example.ueberholserver.api.dto.RideUploadRequest;
import com.example.ueberholserver.db.entity.RideEntity;
import com.example.ueberholserver.db.entity.RideEventEntity;
import com.example.ueberholserver.db.entity.RideSampleEntity;
import com.example.ueberholserver.db.repo.RideRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class RideService {

    private final RideRepository rideRepository;

    public RideService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    @Transactional
    public String uploadRide(RideUploadRequest req) {
        // ── Validation ──────────────────────────────────────────────────────
        if (req.startedAtMs >= req.endedAtMs) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "startedAtMs must be before endedAtMs");
        }
        if (req.samples == null || req.samples.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "samples must not be empty — do not upload rides without GPS data");
        }

        // ── Build entity ─────────────────────────────────────────────────────
        RideEntity ride = new RideEntity();
        ride.setId(UUID.randomUUID().toString());
        ride.setClientRideId(req.clientRideId);
        ride.setDeviceName(req.deviceName);
        ride.setFirmware(req.firmware);
        ride.setSampleRateHz(req.sampleRateHz != null ? req.sampleRateHz : 10);
        ride.setStartedAtMs(req.startedAtMs);
        ride.setEndedAtMs(req.endedAtMs);
        ride.setUploadedAtMs(System.currentTimeMillis());
        // bleTrackId and offsets not transmitted by Android client yet — leave as defaults

        // ── Samples ──────────────────────────────────────────────────────────
        for (var s : req.samples) {
            RideSampleEntity se = new RideSampleEntity();
            se.setRide(ride);
            se.setTMs(s.tMs);
            se.setSensorMillis(s.sensorMillis);
            se.setLat(s.lat);
            se.setLon(s.lon);
            se.setAccuracyM(s.accuracyM);
            se.setSpeedMps(s.speedMps);
            se.setLeftM(s.leftM);
            se.setRightM(s.rightM);
            se.setBatteryPct(s.batteryPct);
            se.setFlags(s.flags);
            ride.getSamples().add(se);
        }

        // ── Events ───────────────────────────────────────────────────────────
        if (req.events != null) {
            for (var e : req.events) {
                RideEventEntity ee = new RideEventEntity();
                ee.setRide(ride);
                ee.setTMs(e.tMs);
                ee.setType(e.type);
                ee.setLat(e.lat);
                ee.setLon(e.lon);
                ee.setLeftM(e.leftM);
                ee.setRightM(e.rightM);
                ride.getEvents().add(ee);
            }
        }

        rideRepository.save(ride);
        return ride.getId();
    }
}
