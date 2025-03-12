package com.onhz.server.repository;

import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.entity.artist.ArtistRatingSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistRatingSummaryRepository extends JpaRepository<ArtistRatingSummaryEntity, Long> {
    Optional<ArtistRatingSummaryEntity> findByArtist(ArtistEntity artist);
}
