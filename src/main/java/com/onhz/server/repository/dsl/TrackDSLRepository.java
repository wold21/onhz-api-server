package com.onhz.server.repository.dsl;

import com.onhz.server.dto.response.track.TrackDetailResponse;
import com.onhz.server.entity.track.TrackEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrackDSLRepository {
    List<Long> findTrackIdsByArtistId(Long artistId, Long lastId, String lastOrderValue, Pageable pageable);
    List<TrackDetailResponse> getTracksWithArtistsAndRatingByIds(List<Long> trackIds);
    List<Long> findTracksByKeyword(String keyword, Long lastId, String lastOrderValue, Pageable pageable);
    List<TrackDetailResponse> findTracksWithDetailsByKeyword(String keyword, Long lastId, String lastOrderValue, Pageable pageable);
}
