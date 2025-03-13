package com.onhz.server.repository;

import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.album.AlbumRatingSummaryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AlbumRatingSummaryRepository extends JpaRepository<AlbumRatingSummaryEntity, Long> {
    Optional<AlbumRatingSummaryEntity> findByAlbum(AlbumEntity album);
    @Query("SELECT ars.album.id FROM AlbumRatingSummaryEntity ars")
    Page<Long> findAllIdsWithRating(Pageable pageable);
}
