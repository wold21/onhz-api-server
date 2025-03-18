package com.onhz.server.repository.dsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlbumRatingSummaryDSLRepository {

    Page<Long>  findAllIdsWithRatingAndGenre(String genreCode, Pageable pageable);
    Page<Long> findAlbumIdsByArtistIdWithRating(Long artistId, Pageable pageable);
}
