package com.example.ueberholserver.db.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "ride_event",
        indexes = {@Index(name = "idx_event_ride_t", columnList = "ride_id,tMs")}
)
public class RideEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ride_id")
    private RideEntity ride;

    private Long tMs;
    private String type;

    private Double lat;
    private Double lon;

    private Double leftM;
    private Double rightM;

    // Getters/setters
    public Long getId() { return id; }

    public RideEntity getRide() { return ride; }
    public void setRide(RideEntity ride) { this.ride = ride; }

    public Long getTMs() { return tMs; }
    public void setTMs(Long tMs) { this.tMs = tMs; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }

    public Double getLeftM() { return leftM; }
    public void setLeftM(Double leftM) { this.leftM = leftM; }

    public Double getRightM() { return rightM; }
    public void setRightM(Double rightM) { this.rightM = rightM; }
}