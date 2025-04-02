package com.onhz.server.dto.response.track;

import com.onhz.server.entity.track.TrackEntity;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class TrackResponse {
    private final Long id;
    private final String trackName;
    private final int trackRank;
    private final int duration;
    private final Long albumId;
    private final LocalDateTime createdAt;

    public static TrackResponse from(TrackEntity track){
        if (track == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "트랙을 찾을 수 없습니다.");
        }
        Integer getRank = track.getTrackRank();
        int rank = getRank != null ? getRank : 0;
        Integer getDuration = track.getDuration();
        int duration = getDuration != null ? getDuration : 0;

        return TrackResponse.builder()
                .id(track.getId())
                .trackName(track.getTrackName())
                .trackRank(rank)
                .duration(duration)
                .albumId(track.getAlbum().getId())
                .createdAt(track.getCreatedAt())
                .build();
    }

    public static List<TrackResponse> from(List<TrackEntity> tracks) {
        return tracks.stream()
                .map(TrackResponse::from)
                .collect(Collectors.toList());
    }
}