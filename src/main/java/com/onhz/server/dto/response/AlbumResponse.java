package com.onhz.server.dto.response;

import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AlbumResponse {
    private final Long id;
    private final String title;
    private final LocalDateTime releaseDate;
    private final LocalDateTime createdAt;
    private final String coverPath;
    private final List<GenreResponse> genres;

    public static AlbumResponse from(AlbumEntity album){
        if (album == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "앨범을 찾을 수 없습니다.");
        }
        return AlbumResponse.builder()
                .id(album.getId())
                .title(album.getTitle())
                .releaseDate(album.getReleaseDate())
                .createdAt(album.getCreatedAt())
                .coverPath(album.getCoverPath())
                .genres(GenreResponse.from(album.getAlbumGenres()))
                .build();
    }
}