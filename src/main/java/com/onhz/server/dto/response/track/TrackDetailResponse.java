package com.onhz.server.dto.response.track;

import com.onhz.server.dto.response.artist.ArtistSimpleResponse;
import com.onhz.server.entity.track.TrackEntity;
import com.onhz.server.exception.ErrorCode;
import com.onhz.server.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class TrackDetailResponse {
    private final Long id;
    private final String title;
    private final int rank;
    private final int duration;
    private final Long albumId;
    private final String coverPath;
    private final LocalDateTime createdAt;
    private final List<ArtistSimpleResponse> artists;
    private final Double rating;
    private final LocalDateTime releaseDate;

    public static TrackDetailResponse from(TrackEntity track){
        if (track == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "트랙을 찾을 수 없습니다.");
        }
        return TrackDetailResponse.builder()
                .id(track.getId())
                .title(track.getTitle())
                .rank(track.getTrackRank())
                .duration(track.getDuration())
                .albumId(track.getAlbum().getId())
                .coverPath(track.getAlbum().getCoverPath())
                .createdAt(track.getCreatedAt())
                .releaseDate(track.getReleaseDate())
                .artists(new ArrayList<>())
                .rating(0.0)
                .build();
    }

    public static List<TrackDetailResponse> from(List<TrackEntity> tracks) {
        return tracks.stream()
                .map(TrackDetailResponse::from)
                .collect(Collectors.toList());
    }
}