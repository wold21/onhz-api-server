package com.onhz.server.repository;

import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.entity.artist.ArtistRatingSummaryEntity;
import com.onhz.server.entity.track.TrackEntity;
import com.onhz.server.entity.track.TrackRatingSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackRatingSummaryRepository extends JpaRepository<TrackRatingSummaryEntity, Long> {
    Optional<TrackRatingSummaryEntity> findByTrack(TrackEntity track);
}
