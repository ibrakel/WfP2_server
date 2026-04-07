package com.example.ueberholserver.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * One OBS BLE sensor packet, linked to the GPS timestamp it arrived with.
 * Only written when the OBS device was connected — absent rows mean no sensor data
 * for that time window, not an error.
 */
@Entity
@Table(
        name = "ride_obs_reading",
        indexes = {@Index(name = "idx_obs_ride_t", columnList = "ride_id, t_ms")}
)
public class RideObsReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id")
    private RideEntity ride;

    @Column(name = "t_ms", nullable = false)
    private Long tMs;

    private Long    sensorMillis;   // device uptime ms from OBS packet
    private Double  leftM;
    private Double  rightM;
    private Integer batteryPct;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int flags;

    // ── Getters / Setters ──────────────────────────────────────────────────

    public Long getId() { return id; }

    public RideEntity getRide() { return ride; }
    public void setRide(RideEntity ride) { this.ride = ride; }

    public Long getTMs() { return tMs; }
    public void setTMs(Long tMs) { this.tMs = tMs; }

    public Long getSensorMillis() { return sensorMillis; }
    public void setSensorMillis(Long sensorMillis) { this.sensorMillis = sensorMillis; }

    public Double getLeftM() { return leftM; }
    public void setLeftM(Double leftM) { this.leftM = leftM; }

    public Double getRightM() { return rightM; }
    public void setRightM(Double rightM) { this.rightM = rightM; }

    public Integer getBatteryPct() { return batteryPct; }
    public void setBatteryPct(Integer batteryPct) { this.batteryPct = batteryPct; }

    public int getFlags() { return flags; }
    public void setFlags(int flags) { this.flags = flags; }
}
