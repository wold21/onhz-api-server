package com.onhz.server.service.track;

import com.onhz.server.dto.response.track.TrackResponse;
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
    public List<TrackResponse> getTrackResponsesByIds(List<Long> ids) {
        List<TrackEntity> tracks = trackRepository.findAllById(ids);

        Map<Long, TrackEntity> trackMap = tracks.stream()
                .collect(Collectors.toMap(TrackEntity::getId, Function.identity()));

        return ids.stream()
                .map(trackMap::get)
                .filter(Objects::nonNull)
                .map(TrackResponse::from)
                .collect(Collectors.toList());
    }

    public List<TrackResponse> getTracksByAlbumId(Long albumId) {
        List<TrackEntity> tracks = trackRepository.findTrackByAlbumIdOrderByTrackRank(albumId);
        return tracks.stream()
                .map(TrackResponse::from)
                .collect(Collectors.toList());
    }
}
