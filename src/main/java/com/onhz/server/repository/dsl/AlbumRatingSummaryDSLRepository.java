package com.onhz.server.repository.dsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AlbumRatingSummaryDSLRepository {

    Page<Long> findAllIdsWithRatingAndGenre(String genreCode, Pageable pageable);
    List<Long> findAlbumIdsByArtistIdWithRating(Long artistId, Pageable pageable);
}
