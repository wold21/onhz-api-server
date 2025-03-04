package com.onhz.server.dto.response;

import com.onhz.server.entity.artist.ArtistEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ArtistResponse {
    private Long id;
    private String name;
    private String bio;
    private String profilePath;
    private LocalDateTime createdAt;
    private String country;

    public static ArtistResponse from(ArtistEntity artist){
        return ArtistResponse.builder()
                .id(artist.getId())
                .name(artist.getName())
                .bio(artist.getBio())
                .profilePath(artist.getProfilePath())
                .createdAt(artist.getCreatedAt())
                .country(artist.getCountry())
                .build();
    }
}