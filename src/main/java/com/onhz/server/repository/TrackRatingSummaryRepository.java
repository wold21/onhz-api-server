package com.onhz.server.repository;

import com.onhz.server.entity.track.TrackEntity;
import com.onhz.server.entity.track.TrackRatingSummaryEntity;
import com.onhz.server.repository.dsl.TrackRatingSummaryDSLRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackRatingSummaryRepository extends JpaRepository<TrackRatingSummaryEntity, Long>, TrackRatingSummaryDSLRepository {
    Optional<TrackRatingSummaryEntity> findByTrack(TrackEntity track);
    Optional<TrackRatingSummaryEntity> findByTrackId(Long trackId);

}
