package com.onhz.server.repository;

import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.album.AlbumRatingSummaryEntity;
import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.entity.artist.ArtistRatingSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlbumRatingSummaryRepository extends JpaRepository<AlbumRatingSummaryEntity, Long> {
    Optional<AlbumRatingSummaryEntity> findByAlbum(AlbumEntity album);
}
