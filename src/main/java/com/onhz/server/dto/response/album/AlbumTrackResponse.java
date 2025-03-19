package com.onhz.server.dto.response.album;

import com.onhz.server.dto.response.GenreResponse;
import com.onhz.server.dto.response.track.TrackResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AlbumTrackResponse {
    private final Long id;
    private final String title;
    private final LocalDateTime releaseDate;
    private final LocalDateTime createdAt;
    private final String coverPath;
    private final List<TrackResponse> tracks;
    private final List<GenreResponse> genres;

    public static AlbumTrackResponse from(AlbumEntity album){
        if (album == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "앨범을 찾을 수 없습니다.");
        }
        return AlbumTrackResponse.builder()
                .id(album.getId())
                .title(album.getTitle())
                .releaseDate(album.getReleaseDate())
                .createdAt(album.getCreatedAt())
                .coverPath(album.getCoverPath())
                .tracks(TrackResponse.from(album.getTracks()))
                .genres(GenreResponse.from(album.getAlbumGenres()))
                .build();
    }
}