package com.onhz.server.dto.response;

import com.onhz.server.entity.GenreEntity;
import com.onhz.server.entity.album.AlbumEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AlbumGenreResponse {
    private Long albumId;
    private String title;
    private LocalDateTime releaseDate;
    private LocalDateTime createdAt;
    private String coverPath;
    private List<GenreResponse> genres;


    public static AlbumGenreResponse from(AlbumEntity album){
        return AlbumGenreResponse.builder()
                .albumId(album.getId())
                .title(album.getTitle())
                .releaseDate(album.getReleaseDate())
                .createdAt(album.getCreatedAt())
                .coverPath(album.getCoverPath())
                .build();
    }

}
