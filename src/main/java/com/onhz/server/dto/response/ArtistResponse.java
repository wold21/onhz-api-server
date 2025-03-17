package com.onhz.server.dto.response;

import com.onhz.server.entity.artist.ArtistAlbumEntity;
import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.exception.example.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class ArtistResponse {
    private final Long id;
    private final String name;
    private final String bio;
    private final String profilePath;
    private final LocalDateTime createdAt;
    private final String country;

    @Builder
    protected ArtistResponse(Long id, String name, String bio, String profilePath,
                             LocalDateTime createdAt, String country) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.profilePath = profilePath;
        this.createdAt = createdAt;
        this.country = country;
    }

    public static ArtistResponse from(ArtistEntity artist){
        if (artist == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "아티스트를 찾을 수 없습니다.");
        }
        return ArtistResponse.builder()
                .id(artist.getId())
                .name(artist.getName())
                .bio(artist.getBio())
                .profilePath(artist.getProfilePath())
                .createdAt(artist.getCreatedAt())
                .country(artist.getCountry())
                .build();
    }

    public static List<ArtistResponse> from(List<ArtistAlbumEntity> albumArtists) {
        if (albumArtists == null) {
            return Collections.emptyList();
        }
        return albumArtists.stream()
                .map(albumArtist -> {
                    ArtistEntity artist = albumArtist.getArtist();
                    if (artist == null) {
                        return null;
                    }
                    return ArtistResponse.builder()
                            .id(artist.getId())
                            .name(artist.getName())
                            .bio(artist.getBio())
                            .profilePath(artist.getProfilePath())
                            .createdAt(artist.getCreatedAt())
                            .country(artist.getCountry())
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}