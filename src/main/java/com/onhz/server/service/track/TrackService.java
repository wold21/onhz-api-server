package com.onhz.server.service.track;

import com.onhz.server.dto.response.track.TrackAlbumResponse;
import com.onhz.server.dto.response.track.TrackResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.album.AlbumGenreEntity;
import com.onhz.server.entity.track.TrackEntity;
import com.onhz.server.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackService {
    private final TrackRepository trackRepository;

    public List<TrackResponse> getTracksByAlbumId(Long albumId) {
        List<TrackEntity> tracks = trackRepository.findTrackByAlbumIdOrderByTrackRank(albumId);
        return tracks.stream()
                .map(TrackResponse::from)
                .collect(Collectors.toList());
    }

    public TrackAlbumResponse getTrackByIdWithAlbum(Long trackId) {
        TrackEntity track = trackRepository.findById(trackId)
                .orElseThrow(() -> new IllegalArgumentException("트랙을 찾을 수 없습니다."));
        AlbumEntity album = track.getAlbum();

        return TrackAlbumResponse.of(album, track);
    }
}
