package com.onhz.server.dto.response;

import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.entity.track.TrackEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrackResponse {
    private Long id;
    private String trackName;
    private int trackRank;
    private int duration;
    private Long albumId;
    private LocalDateTime createdAt;
    private String country;

    public static TrackResponse from(TrackEntity trackEntity){
        return TrackResponse.builder()
                .id(trackEntity.getId())
                .trackName(trackEntity.getTrackName())
                .trackRank(trackEntity.getTrackRank())
                .duration(trackEntity.getDuration())
                .albumId(trackEntity.getAlbum().getId())
                .createdAt(trackEntity.getCreatedAt())
                .build();
    }
}