package com.onhz.server.dto.response;

import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.exception.example.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ArtistResponse {
    private final Long id;
    private final String name;
    private final String bio;
    private final String profilePath;
    private final LocalDateTime createdAt;
    private final String country;

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
}