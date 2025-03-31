package com.onhz.server.repository;

import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.album.AlbumRatingSummaryEntity;
import com.onhz.server.repository.dsl.AlbumRatingSummaryDSLRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlbumRatingSummaryRepository extends JpaRepository<AlbumRatingSummaryEntity, Long>, AlbumRatingSummaryDSLRepository {
    Optional<AlbumRatingSummaryEntity> findByAlbum(AlbumEntity album);
    @Query("SELECT ars.album.id FROM AlbumRatingSummaryEntity ars")
    List<Long> findAllIdsWithRating(Pageable pageable);
    Optional<AlbumRatingSummaryEntity> findByAlbumId(Long albumId);
    List<AlbumRatingSummaryEntity> findByAlbumIdIn(List<Long> albumIds);
}


