package com.onhz.server.dto.response;

import com.onhz.server.entity.GenreEntity;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.album.AlbumGenreEntity;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.exception.example.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class AlbumGenreResponse {
    private final Long id;
    private final String title;
    private final LocalDateTime releaseDate;
    private final LocalDateTime createdAt;
    private final String coverPath;
    private final List<GenreResponse> genres;


    public static AlbumGenreResponse from(AlbumEntity album){
        if (album == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "앨범을 찾을 수 없습니다.");
        }
        return AlbumGenreResponse.builder()
                .id(album.getId())
                .title(album.getTitle())
                .releaseDate(album.getReleaseDate())
                .createdAt(album.getCreatedAt())
                .coverPath(album.getCoverPath())
                .genres(GenreResponse.from(album.getAlbumGenres()))
                .build();
    }
}
