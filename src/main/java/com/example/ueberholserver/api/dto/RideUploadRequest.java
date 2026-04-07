package com.example.ueberholserver.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class RideUploadRequest {

    public Long clientRideId;
    public String deviceName;
    public String firmware;
    public Integer sampleRateHz;

    @NotNull public Long startedAtMs;
    @NotNull public Long endedAtMs;

    @NotNull public List<RideSampleDto> samples;
    public List<RideEventDto> events;

    public static class RideSampleDto {
        public Long tMs;
        public Long sensorMillis;       // device uptime ms; nullable
        public Double lat, lon, accuracyM, speedMps;
        public Double leftM, rightM;
        public Integer batteryPct;
        public int flags;               // default 0
    }

    public static class RideEventDto {
        public Long tMs;
        public String type;
        public Double lat, lon, leftM, rightM;
    }
}
