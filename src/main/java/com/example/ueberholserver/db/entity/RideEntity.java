package com.example.ueberholserver.db.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ride")
public class RideEntity {

    @Id
    private String id; // serverRideId (UUID string)

    private Long clientRideId;
    private Integer sampleRateHz;
    private Long startedAtMs;
    private Long endedAtMs;

    private String deviceName;
    private String firmware;

    private Instant uploadedAt;

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RideSampleEntity> samples = new ArrayList<>();

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RideEventEntity> events = new ArrayList<>();

    // Getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getClientRideId() { return clientRideId; }
    public void setClientRideId(Long clientRideId) { this.clientRideId = clientRideId; }

    public Integer getSampleRateHz() { return sampleRateHz; }
    public void setSampleRateHz(Integer sampleRateHz) { this.sampleRateHz = sampleRateHz; }

    public Long getStartedAtMs() { return startedAtMs; }
    public void setStartedAtMs(Long startedAtMs) { this.startedAtMs = startedAtMs; }

    public Long getEndedAtMs() { return endedAtMs; }
    public void setEndedAtMs(Long endedAtMs) { this.endedAtMs = endedAtMs; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getFirmware() { return firmware; }
    public void setFirmware(String firmware) { this.firmware = firmware; }

    public Instant getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }

    public List<RideSampleEntity> getSamples() { return samples; }
    public void setSamples(List<RideSampleEntity> samples) { this.samples = samples; }

    public List<RideEventEntity> getEvents() { return events; }
    public void setEvents(List<RideEventEntity> events) { this.events = events; }
}