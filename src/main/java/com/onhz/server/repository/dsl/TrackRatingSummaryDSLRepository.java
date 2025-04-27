package com.onhz.server.repository.dsl;

import com.onhz.server.dto.response.track.TrackResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRatingSummaryDSLRepository {
    List<Long> findTrackIdsByArtistIdWithRating(Long artistId, Pageable pageable);
    List<TrackResponse> findTrackByAlbumIdOrderByTrackRank(Long albumId);
}
