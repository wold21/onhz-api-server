package com.onhz.server.repository.album;



import com.onhz.server.dto.response.AlbumGenreResponse;
import com.onhz.server.entity.album.AlbumEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<AlbumEntity, Long> {
    @Query("SELECT DISTINCT a FROM AlbumEntity a LEFT JOIN FETCH a.albumGenres")
    Page<AlbumEntity> findAllWithGenres(Pageable pageable);

    @Query("""
        SELECT DISTINCT a FROM AlbumEntity a 
        LEFT JOIN FETCH a.albumGenres ag 
        LEFT JOIN a.ratingSummary rs
        """)
    Page<AlbumEntity> findAllWithRatings(Pageable pageable);

}
