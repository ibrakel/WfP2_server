package com.example.ueberholserver.api.dto;

public class RideSummaryDto {

    public final String serverRideId;
    public final Long   clientRideId;
    public final String deviceName;
    public final Long   startedAtMs;
    public final Long   endedAtMs;
    public final Long   uploadedAtMs;
    public final String bleTrackId;
    public final int    gpsPointCount;
    public final int    obsReadingCount;
    public final int    eventCount;

    /** JPQL constructor expression — order must match the @Query exactly. */
    public RideSummaryDto(
            String serverRideId,
            Long   clientRideId,
            String deviceName,
            Long   startedAtMs,
            Long   endedAtMs,
            Long   uploadedAtMs,
            String bleTrackId,
            int    gpsPointCount,
            int    obsReadingCount,
            int    eventCount) {
        this.serverRideId    = serverRideId;
        this.clientRideId    = clientRideId;
        this.deviceName      = deviceName;
        this.startedAtMs     = startedAtMs;
        this.endedAtMs       = endedAtMs;
        this.uploadedAtMs    = uploadedAtMs;
        this.bleTrackId      = bleTrackId;
        this.gpsPointCount   = gpsPointCount;
        this.obsReadingCount = obsReadingCount;
        this.eventCount      = eventCount;
    }
}
