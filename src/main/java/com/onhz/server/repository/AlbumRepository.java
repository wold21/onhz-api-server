package com.onhz.server.repository;

import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.repository.dsl.AlbumDSLRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<AlbumEntity, Long>, AlbumDSLRepository {
    @Query("SELECT a.id FROM AlbumEntity a " +
            "LEFT JOIN a.albumGenres ag " +
            "LEFT JOIN ag.genre g " +
            "WHERE LOWER(g.code) LIKE LOWER(CONCAT('%', :genreCode, '%'))" +
            "GROUP BY a.id")
    Page<Long> findAlbumIdsByGenreCode(@Param("genreCode") String genreCode, Pageable pageable);

    @Query("SELECT aat.album.id from ArtistAlbumEntity aat " +
            "JOIN aat.album a " +
            "WHERE aat.artist.id = :artistId")
    Page<Long> findAlbumIdsByArtistId(@Param("artistId") Long artistId, Pageable pageable);

    AlbumEntity findByTracksId(Long trackId);

}
