package com.onhz.server.dto.response.artist;

import com.onhz.server.entity.artist.ArtistAlbumEntity;
import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.exception.ErrorCode;
import com.onhz.server.exception.NotFoundException;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Builder
public class ArtistSearchResponse {
    private final Long id;
    private final String name;
    private final String profilePath;
    private final String createdAt;
    private final Double score;

    public static ArtistSearchResponse from(ArtistEntity artist, Double score) {
        if (artist == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "아티스트를 찾을 수 없습니다.");
        }
        return ArtistSearchResponse.builder()
                .id(artist.getId())
                .name(artist.getName())
                .profilePath(artist.getProfilePath())
                .createdAt(artist.getCreatedAt().toString())
                .score(score)
                .build();
    }
}