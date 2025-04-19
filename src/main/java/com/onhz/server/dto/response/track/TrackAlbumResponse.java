package com.onhz.server.dto.response.track;

import com.onhz.server.dto.response.GenreResponse;
import com.onhz.server.dto.response.artist.ArtistSimpleResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.track.TrackEntity;
import com.onhz.server.exception.ErrorCode;
import com.onhz.server.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TrackAlbumResponse {
    private final Long id;
    private final String title;
    private final Integer duration;
    private final Long albumId;
    private final String albumTitle;
    private final LocalDateTime releaseDate;
    private final LocalDateTime createdAt;
    private final String coverPath;
    private final List<GenreResponse> genres;
    private final List<ArtistSimpleResponse> artists;

    public static TrackAlbumResponse of(AlbumEntity album, TrackEntity track) {
        if (track == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "트랙을 찾을 수 없습니다.");
        }
        return TrackAlbumResponse.builder()
                .id(track.getId())
                .title(track.getTitle())
                .albumId(album.getId())
                .albumTitle(album.getTitle())
                .duration(track.getDuration())
                .releaseDate(album.getReleaseDate())
                .createdAt(album.getCreatedAt())
                .coverPath(album.getCoverPath())
                .genres(GenreResponse.from(album.getAlbumGenres()))
                .artists(ArtistSimpleResponse.from(album.getAlbumArtists()))
                .build();
    }
}
