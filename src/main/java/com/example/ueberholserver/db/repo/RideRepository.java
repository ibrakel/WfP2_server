package com.example.ueberholserver.db.repo;

import com.example.ueberholserver.api.dto.RideSummaryDto;
import com.example.ueberholserver.db.entity.RideEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RideRepository extends JpaRepository<RideEntity, String> {

    @Query(value =
            "SELECT new com.example.ueberholserver.api.dto.RideSummaryDto(" +
            "  r.id, r.clientRideId, r.deviceName," +
            "  r.startedAtMs, r.endedAtMs, r.uploadedAtMs," +
            "  r.bleTrackId, SIZE(r.gpsPoints), SIZE(r.obsReadings), SIZE(r.events)" +
            ") FROM RideEntity r",
           countQuery = "SELECT COUNT(r) FROM RideEntity r")
    Page<RideSummaryDto> findAllSummaries(Pageable pageable);
}
