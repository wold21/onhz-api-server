package com.onhz.server.repository;

import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.album.AlbumRatingSummaryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AlbumRatingSummaryRepository extends JpaRepository<AlbumRatingSummaryEntity, Long> {
    Optional<AlbumRatingSummaryEntity> findByAlbum(AlbumEntity album);
    @Query("SELECT ars.album.id FROM AlbumRatingSummaryEntity ars")
    Page<Long> findAllIdsWithRating(Pageable pageable);

    @Query("SELECT ars.album.id FROM AlbumRatingSummaryEntity ars " +
            "LEFT JOIN ars.album.albumGenres ag " +
            "LEFT JOIN ag.genre g " +
            "WHERE LOWER(g.code) LIKE LOWER(CONCAT('%', :genreCode, '%'))" +
            "GROUP BY ars.album.id, ars.ratingCount, ars.averageRating")
    Page<Long> findAllIdsWithRatingAndGenre(@Param("genreCode")String genreCode, Pageable pageable);
}


