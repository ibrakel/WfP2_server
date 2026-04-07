package com.example.ueberholserver.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/** One GPS fix from the phone, recorded at ~sampleRateHz. Always present for each sample. */
@Entity
@Table(
        name = "ride_gps_point",
        indexes = {@Index(name = "idx_gps_ride_t", columnList = "ride_id, t_ms")}
)
public class RideGpsPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id")
    private RideEntity ride;

    @Column(name = "t_ms", nullable = false)
    private Long tMs;

    private Double lat;        // null inside a privacy zone or no fix
    private Double lon;
    private Double speedMps;
    private Double accuracyM;

    // ── Getters / Setters ──────────────────────────────────────────────────

    public Long getId() { return id; }

    public RideEntity getRide() { return ride; }
    public void setRide(RideEntity ride) { this.ride = ride; }

    public Long getTMs() { return tMs; }
    public void setTMs(Long tMs) { this.tMs = tMs; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }

    public Double getSpeedMps() { return speedMps; }
    public void setSpeedMps(Double speedMps) { this.speedMps = speedMps; }

    public Double getAccuracyM() { return accuracyM; }
    public void setAccuracyM(Double accuracyM) { this.accuracyM = accuracyM; }
}
