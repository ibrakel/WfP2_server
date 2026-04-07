package com.example.ueberholserver.service;

import com.example.ueberholserver.api.dto.RideUploadRequest;
import com.example.ueberholserver.db.entity.RideEntity;
import com.example.ueberholserver.db.entity.RideEventEntity;
import com.example.ueberholserver.db.entity.RideGpsPoint;
import com.example.ueberholserver.db.entity.RideObsReading;
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

        // ── Build session ────────────────────────────────────────────────────
        RideEntity ride = new RideEntity();
        ride.setId(UUID.randomUUID().toString());
        ride.setClientRideId(req.clientRideId);
        ride.setDeviceName(req.deviceName);
        ride.setFirmware(req.firmware);
        ride.setSampleRateHz(req.sampleRateHz != null ? req.sampleRateHz : 10);
        ride.setStartedAtMs(req.startedAtMs);
        ride.setEndedAtMs(req.endedAtMs);
        ride.setUploadedAtMs(System.currentTimeMillis());

        // ── Split each incoming sample into GPS point + optional OBS reading ──
        for (var s : req.samples) {
            // GPS point — always written
            RideGpsPoint gps = new RideGpsPoint();
            gps.setRide(ride);
            gps.setTMs(s.tMs);
            gps.setLat(s.lat);
            gps.setLon(s.lon);
            gps.setSpeedMps(s.speedMps);
            gps.setAccuracyM(s.accuracyM);
            ride.getGpsPoints().add(gps);

            // OBS reading — only when the sensor contributed data
            boolean hasSensorData = s.leftM != null || s.rightM != null
                    || s.batteryPct != null || s.sensorMillis != null;
            if (hasSensorData) {
                RideObsReading obs = new RideObsReading();
                obs.setRide(ride);
                obs.setTMs(s.tMs);
                obs.setSensorMillis(s.sensorMillis);
                obs.setLeftM(s.leftM);
                obs.setRightM(s.rightM);
                obs.setBatteryPct(s.batteryPct);
                obs.setFlags(s.flags);
                ride.getObsReadings().add(obs);
            }
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
