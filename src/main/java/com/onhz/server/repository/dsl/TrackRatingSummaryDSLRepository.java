package com.onhz.server.repository.dsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrackRatingSummaryDSLRepository {
    Page<Long> findTrackIdsByArtistIdWithRating(Long artistId, Pageable pageable);

}
