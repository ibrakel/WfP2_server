package com.example.ueberholserver.service;

import com.example.ueberholserver.db.entity.RideEntity;
import com.example.ueberholserver.db.entity.RideEventEntity;
import com.example.ueberholserver.db.entity.RideGpsPoint;
import com.example.ueberholserver.db.entity.RideObsReading;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CsvExportService {

    private static final String CRLF = "\r\n";
    private static final String HEADER =
            "Date;Time;Millis;Latitude;Longitude;Altitude;Course;Speed;HDOP;Satellites;" +
            "BatteryLevel;Left;Right;Confirmed;Marked;Invalid;InsidePrivacyArea;Factor;" +
            "Measurements;Tms1;Lus1;Rus1";

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter FILENAME_DT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH.mm.ss").withZone(ZoneOffset.UTC);

    public String buildCsv(RideEntity ride) {
        List<RideGpsPoint> gpsPoints = ride.getGpsPoints().stream()
                .sorted(Comparator.comparingLong(RideGpsPoint::getTMs))
                .collect(Collectors.toList());

        // O(1) lookup: tMs → OBS reading (exact match; same packet = same timestamp)
        Map<Long, RideObsReading> obsByTms = ride.getObsReadings().stream()
                .collect(Collectors.toMap(RideObsReading::getTMs, r -> r, (a, b) -> a));

        List<RideEventEntity> events = ride.getEvents();
        Set<Long> closePassTimes = events.stream()
                .filter(e -> "CLOSE_PASS_BUTTON".equals(e.getType()))
                .map(RideEventEntity::getTMs)
                .collect(Collectors.toSet());

        StringBuilder sb = new StringBuilder();

        // ── Line 1: metadata ─────────────────────────────────────────────────
        sb.append("OBSDataFormat=2")
          .append("&OBSFirmwareVersion=Android")
          .append("&OffsetLeft=").append(ride.getOffsetLeftCm())
          .append("&OffsetRight=").append(ride.getOffsetRightCm());
        if (ride.getBleTrackId() != null) {
            sb.append("&TrackId=").append(ride.getBleTrackId());
        }
        sb.append(CRLF);

        // ── Line 2: header ───────────────────────────────────────────────────
        sb.append(HEADER).append(CRLF);

        // ── Lines 3..N: one row per GPS fix ──────────────────────────────────
        for (RideGpsPoint gps : gpsPoints) {
            RideObsReading obs = obsByTms.get(gps.getTMs()); // null = no sensor data this tick

            Instant instant = Instant.ofEpochMilli(gps.getTMs());
            String date  = DATE_FMT.format(instant);
            String time  = TIME_FMT.format(instant);
            long millis  = (obs != null && obs.getSensorMillis() != null)
                    ? obs.getSensorMillis()
                    : gps.getTMs() - ride.getStartedAtMs();

            String lat   = gps.getLat()      != null ? String.valueOf(gps.getLat())   : "";
            String lon   = gps.getLon()      != null ? String.valueOf(gps.getLon())   : "";
            String speed = gps.getSpeedMps() != null
                    ? String.format("%.2f", gps.getSpeedMps() * 3.6) : "";

            Integer leftCm  = obs != null ? toCm(obs.getLeftM())  : null;
            Integer rightCm = obs != null ? toCm(obs.getRightM()) : null;
            String  left    = leftCm  != null ? String.valueOf(leftCm)  : "";
            String  right   = rightCm != null ? String.valueOf(rightCm) : "";

            boolean confirmed = events.stream()
                    .anyMatch(e -> Math.abs(e.getTMs() - gps.getTMs()) <= 500L);
            boolean marked  = closePassTimes.contains(gps.getTMs());
            boolean invalid = leftCm == null && rightCm == null;

            String lus1 = leftCm  != null ? String.valueOf(leftCm  * 58) : "";
            String rus1 = rightCm != null ? String.valueOf(rightCm * 58) : "";

            sb.append(date).append(';')
              .append(time).append(';')
              .append(millis).append(';')
              .append(lat).append(';')
              .append(lon).append(';')
              .append(';')                        // Altitude  (empty)
              .append(';')                        // Course    (empty)
              .append(speed).append(';')
              .append(';')                        // HDOP       (empty)
              .append(';')                        // Satellites (empty)
              .append(';')                        // BatteryLevel (empty)
              .append(left).append(';')
              .append(right).append(';')
              .append(confirmed ? "1" : "").append(';')
              .append(marked    ? "1" : "").append(';')
              .append(invalid   ? "1" : "0").append(';')
              .append('0').append(';')            // InsidePrivacyArea
              .append(';')                        // Factor (empty)
              .append('1').append(';')            // Measurements
              .append(millis).append(';')         // Tms1
              .append(lus1).append(';')
              .append(rus1)
              .append(CRLF);
        }

        return sb.toString();
    }

    public String buildFilename(RideEntity ride) {
        String dateTime = FILENAME_DT_FMT.format(Instant.ofEpochMilli(ride.getStartedAtMs()));
        String raw = ride.getBleTrackId() != null
                ? ride.getBleTrackId().replaceAll("[^A-Za-z0-9\\-]", "") : "";
        String suffix = raw.length() >= 4 ? raw.substring(raw.length() - 4) : "XXXX";
        return dateTime + "-" + suffix + ".obsdata.csv";
    }

    private static Integer toCm(Double metres) {
        if (metres == null) return null;
        return Math.min(999, Math.max(0, (int) Math.round(metres * 100)));
    }
}
