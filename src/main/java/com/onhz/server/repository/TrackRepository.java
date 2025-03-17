package com.onhz.server.repository;

import com.onhz.server.entity.track.TrackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<TrackEntity, Long> {
    @Query("SELECT at.track.id from ArtistTrackEntity at " +
            "JOIN at.track t " +
            "WHERE at.artist.id = :artistId")
    Page<Long> findTrackIdsByArtistId(@Param("artistId") Long artistId, Pageable pageable);
}
