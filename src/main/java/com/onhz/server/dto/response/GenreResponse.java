package com.onhz.server.dto.response;

import com.onhz.server.entity.GenreEntity;
import com.onhz.server.entity.album.AlbumGenreEntity;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.exception.example.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
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
        return albumGenres.stream()
                .map(albumGenre -> from(albumGenre.getGenre()))
                .collect(Collectors.toList());
    }
}
