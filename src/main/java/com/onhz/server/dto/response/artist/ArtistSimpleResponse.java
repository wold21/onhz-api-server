package com.onhz.server.dto.response.artist;

import com.onhz.server.entity.artist.ArtistAlbumEntity;
import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Builder
public class ArtistSimpleResponse {
    private final Long id;
    private final String name;
    private final String profilePath;
    private final String createdAt;
    private final String role;

    public static ArtistSimpleResponse from(ArtistEntity artist){
        if (artist == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "아티스트를 찾을 수 없습니다.");
        }
        return ArtistSimpleResponse.builder()
                .id(artist.getId())
                .name(artist.getName())
                .profilePath(artist.getProfilePath())
                .createdAt(artist.getCreatedAt().toString())
                .build();
    }

    public static ArtistSimpleResponse fromWithRole(ArtistAlbumEntity artistAlbum) {
        if (artistAlbum == null || artistAlbum.getArtist() == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "아티스트를 찾을 수 없습니다.");
        }

        ArtistEntity artist = artistAlbum.getArtist();

        return ArtistSimpleResponse.builder()
                .id(artist.getId())
                .name(artist.getName())
                .profilePath(artist.getProfilePath())
                .role(artistAlbum.getArtistRole())
                .build();
    }

    public static List<ArtistSimpleResponse> from(List<ArtistAlbumEntity> albumArtists) {
        if (albumArtists == null) {
            return Collections.emptyList();
        }
        return albumArtists.stream()
                .map(ArtistSimpleResponse::fromWithRole)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}