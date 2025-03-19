package com.onhz.server.dto.response;

import com.onhz.server.entity.GenreEntity;
import com.onhz.server.entity.album.AlbumGenreEntity;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.exception.ErrorCode;
import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Builder
public class GenreResponse{
    private final Long id;
    private final String code;
    private final String name;

    public static GenreResponse from(GenreEntity genre){
        if (genre == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "장르를 찾을 수 없습니다.");
        }
        return GenreResponse.builder()
                .id(genre.getId())
                .code(genre.getCode())
                .name(genre.getName())
                .build();
    }

    public static List<GenreResponse> from(List<AlbumGenreEntity> albumGenres) {
        if (albumGenres == null) {
            return Collections.emptyList();
        }
        return albumGenres.stream()
                .map(albumGenre -> {
                    GenreEntity genre = albumGenre.getGenre();
                    if (genre == null) {
                        return null;
                    }
                    return GenreResponse.builder()
                            .id(genre.getId())
                            .code(genre.getCode())
                            .name(genre.getName())
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
