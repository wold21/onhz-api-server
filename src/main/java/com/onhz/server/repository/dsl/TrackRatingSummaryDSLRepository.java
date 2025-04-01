package com.onhz.server.repository.dsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrackRatingSummaryDSLRepository {
    List<Long> findTrackIdsByArtistIdWithRating(Long artistId, Pageable pageable);

}
