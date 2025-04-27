package com.onhz.server.service.track;

import com.onhz.server.dto.response.track.TrackAlbumResponse;
import com.onhz.server.dto.response.track.TrackResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.track.TrackEntity;
import com.onhz.server.repository.TrackRatingSummaryRepository;
import com.onhz.server.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackService {
    private final TrackRepository trackRepository;
    private final TrackRatingSummaryRepository TrackRatingSummaryRepository;

    public List<TrackResponse> getTracksByAlbumId(Long albumId) {
        return TrackRatingSummaryRepository.findTrackByAlbumIdOrderByTrackRank(albumId);
    }

    public TrackAlbumResponse getTrackByIdWithAlbum(Long trackId) {
        TrackEntity track = trackRepository.findById(trackId)
                .orElseThrow(() -> new IllegalArgumentException("트랙을 찾을 수 없습니다."));
        AlbumEntity album = track.getAlbum();

        return TrackAlbumResponse.of(album, track);
    }
}
