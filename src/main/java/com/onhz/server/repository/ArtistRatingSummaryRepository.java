package com.onhz.server.repository;

import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.entity.artist.ArtistRatingSummaryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ArtistRatingSummaryRepository extends JpaRepository<ArtistRatingSummaryEntity, Long> {
    Optional<ArtistRatingSummaryEntity> findByArtist(ArtistEntity artist);
    @Query("SELECT ars.artist.id FROM ArtistRatingSummaryEntity ars")
    Page<Long> findAllIdsWithRating(Pageable pageable);
    Optional<ArtistRatingSummaryEntity> findByArtistId(Long artistId);

}
