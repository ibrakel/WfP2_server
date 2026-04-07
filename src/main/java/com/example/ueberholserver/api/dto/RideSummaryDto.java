package com.example.ueberholserver.api.dto;

/** Returned by GET /api/rides (paginated list — no inline samples or events). */
public class RideSummaryDto {

    public final String serverRideId;
    public final Long clientRideId;
    public final String deviceName;
    public final Long startedAtMs;
    public final Long endedAtMs;
    public final Long uploadedAtMs;
    public final String bleTrackId;
    public final int sampleCount;
    public final int eventCount;

    /** JPQL constructor expression — parameter order must match the @Query exactly. */
    public RideSummaryDto(
            String serverRideId,
            Long clientRideId,
            String deviceName,
            Long startedAtMs,
            Long endedAtMs,
            Long uploadedAtMs,
            String bleTrackId,
            int sampleCount,
            int eventCount) {
        this.serverRideId  = serverRideId;
        this.clientRideId  = clientRideId;
        this.deviceName    = deviceName;
        this.startedAtMs   = startedAtMs;
        this.endedAtMs     = endedAtMs;
        this.uploadedAtMs  = uploadedAtMs;
        this.bleTrackId    = bleTrackId;
        this.sampleCount   = sampleCount;
        this.eventCount    = eventCount;
    }
}
