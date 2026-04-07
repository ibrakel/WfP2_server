package com.example.ueberholserver.db.entity;

import jakarta.persistence.*;
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

    /** New column; old "uploaded_at" (Instant) column remains in DB but is unmapped. */
    @Column(name = "uploaded_at_ms")
    private Long uploadedAtMs;

    /** OBS firmware track UUID from CHAR_TRACK_ID; null if not transmitted by device. */
    private String bleTrackId;

    /** Handlebar offset from CHAR_OFFSET, in cm. */
    @Column(nullable = false, columnDefinition = "int default 0")
    private int offsetLeftCm;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int offsetRightCm;

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RideSampleEntity> samples = new ArrayList<>();

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RideEventEntity> events = new ArrayList<>();

    // ── Getters / Setters ──────────────────────────────────────────────────

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

    public Long getUploadedAtMs() { return uploadedAtMs; }
    public void setUploadedAtMs(Long uploadedAtMs) { this.uploadedAtMs = uploadedAtMs; }

    public String getBleTrackId() { return bleTrackId; }
    public void setBleTrackId(String bleTrackId) { this.bleTrackId = bleTrackId; }

    public int getOffsetLeftCm() { return offsetLeftCm; }
    public void setOffsetLeftCm(int offsetLeftCm) { this.offsetLeftCm = offsetLeftCm; }

    public int getOffsetRightCm() { return offsetRightCm; }
    public void setOffsetRightCm(int offsetRightCm) { this.offsetRightCm = offsetRightCm; }

    public List<RideSampleEntity> getSamples() { return samples; }
    public void setSamples(List<RideSampleEntity> samples) { this.samples = samples; }

    public List<RideEventEntity> getEvents() { return events; }
    public void setEvents(List<RideEventEntity> events) { this.events = events; }
}
