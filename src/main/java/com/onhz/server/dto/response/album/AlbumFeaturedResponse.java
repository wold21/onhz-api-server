package com.onhz.server.dto.response.album;

import com.onhz.server.dto.response.GenreResponse;
import com.onhz.server.dto.response.artist.ArtistSimpleResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.exception.ErrorCode;
import com.onhz.server.exception.NotFoundException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AlbumFeaturedResponse {
    private final Long id;
    private final String title;
    private final LocalDateTime releaseDate;
    private final LocalDateTime createdAt;
    private final String coverPath;
    private final List<GenreResponse> genres;
    private final List<ArtistSimpleResponse> artists;
    private final Double rating;

    public static AlbumFeaturedResponse of(AlbumEntity album, Double rating){
        if (album == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "앨범을 찾을 수 없습니다.");
        }
        return AlbumFeaturedResponse.builder()
                .id(album.getId())
                .title(album.getTitle())
                .releaseDate(album.getReleaseDate())
                .createdAt(album.getCreatedAt())
                .coverPath(album.getCoverPath())
                .genres(GenreResponse.from(album.getAlbumGenres()))
                .artists(ArtistSimpleResponse.from(album.getAlbumArtists()))
                .rating(rating)
                .build();
    }
}
