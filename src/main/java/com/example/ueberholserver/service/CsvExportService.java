package com.example.ueberholserver.service;

import com.example.ueberholserver.db.entity.RideEntity;
import com.example.ueberholserver.db.entity.RideEventEntity;
import com.example.ueberholserver.db.entity.RideSampleEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
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

    /**
     * Builds the full .obsdata.csv content (OBS firmware v2 format) for the given ride.
     * The returned string uses CRLF line endings.
     */
    public String buildCsv(RideEntity ride) {
        List<RideSampleEntity> samples = ride.getSamples().stream()
                .sorted(Comparator.comparingLong(RideSampleEntity::getTMs))
                .collect(Collectors.toList());

        List<RideEventEntity> events = ride.getEvents();

        // Pre-compute event lookup structures
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

        // ── Lines 3..N: one row per sample ───────────────────────────────────
        for (RideSampleEntity s : samples) {
            Instant instant = Instant.ofEpochMilli(s.getTMs());

            String date   = DATE_FMT.format(instant);
            String time   = TIME_FMT.format(instant);
            long   millis = s.getSensorMillis() != null
                    ? s.getSensorMillis()
                    : s.getTMs() - ride.getStartedAtMs();

            String lat   = s.getLat()   != null ? String.valueOf(s.getLat())   : "";
            String lon   = s.getLon()   != null ? String.valueOf(s.getLon())   : "";
            String speed = s.getSpeedMps() != null
                    ? String.format("%.2f", s.getSpeedMps() * 3.6) : "";

            Integer leftCm  = toCm(s.getLeftM());
            Integer rightCm = toCm(s.getRightM());
            String  left    = leftCm  != null ? String.valueOf(leftCm)  : "";
            String  right   = rightCm != null ? String.valueOf(rightCm) : "";

            // Confirmed: any event within 500 ms
            boolean confirmed = events.stream()
                    .anyMatch(e -> Math.abs(e.getTMs() - s.getTMs()) <= 500L);
            // Marked: CLOSE_PASS_BUTTON at this exact tMs
            boolean marked = closePassTimes.contains(s.getTMs());
            // Invalid: both distances absent
            boolean invalid = s.getLeftM() == null && s.getRightM() == null;

            String lus1 = leftCm  != null ? String.valueOf(leftCm  * 58) : "";
            String rus1 = rightCm != null ? String.valueOf(rightCm * 58) : "";

            sb.append(date).append(';')
              .append(time).append(';')
              .append(millis).append(';')
              .append(lat).append(';')
              .append(lon).append(';')
              .append(';')          // Altitude (empty)
              .append(';')          // Course   (empty)
              .append(speed).append(';')
              .append(';')          // HDOP       (empty)
              .append(';')          // Satellites (empty)
              .append(';')          // BatteryLevel (empty per spec — not mapped to OBS column)
              .append(left).append(';')
              .append(right).append(';')
              .append(confirmed ? "1" : "").append(';')
              .append(marked    ? "1" : "").append(';')
              .append(invalid   ? "1" : "0").append(';')
              .append('0').append(';')   // InsidePrivacyArea
              .append(';')               // Factor (empty)
              .append('1').append(';')   // Measurements
              .append(millis).append(';') // Tms1
              .append(lus1).append(';')
              .append(rus1)
              .append(CRLF);
        }

        return sb.toString();
    }

    /**
     * Returns the suggested download filename:
     * {@code <yyyy-MM-dd'T'HH.mm.ss>-<LAST4_OF_TRACK_ID>.obsdata.csv}
     */
    public String buildFilename(RideEntity ride) {
        String dateTime = FILENAME_DT_FMT.format(Instant.ofEpochMilli(ride.getStartedAtMs()));
        String trackSuffix = ride.getBleTrackId() != null
                ? ride.getBleTrackId().replaceAll("[^A-Za-z0-9\\-]", "")
                       .substring(Math.max(0, ride.getBleTrackId().replaceAll("[^A-Za-z0-9\\-]", "").length() - 4))
                : "XXXX";
        return dateTime + "-" + trackSuffix + ".obsdata.csv";
    }

    /** Converts metres to cm, clamped to 0–999. Returns null when input is null. */
    private static Integer toCm(Double metres) {
        if (metres == null) return null;
        return Math.min(999, Math.max(0, (int) Math.round(metres * 100)));
    }
}
