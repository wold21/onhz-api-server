package com.onhz.server.service.search;

import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.dto.response.track.TrackDetailResponse;
import com.onhz.server.entity.track.TrackEntity;
import com.onhz.server.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchTrackService {
    private final TrackRepository trackRepository;
    public List searchTrack(String keyword, Long lastId, String lastOrderValue, int limit, String orderBy) {
        Pageable pageable = PageUtils.createPageable(0, limit, orderBy, TrackEntity.class);
        List<TrackDetailResponse> tracks = trackRepository.findTracksWithDetailsByKeyword(keyword, lastId, lastOrderValue, pageable);
        return tracks;
    }
}
