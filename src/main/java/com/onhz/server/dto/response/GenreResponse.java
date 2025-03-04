package com.onhz.server.dto.response;

import com.onhz.server.entity.GenreEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GenreResponse{
    private Long id;
    private String code;
    private String name;

    public static GenreResponse from(GenreEntity genre){
        return GenreResponse.builder()
                .id(genre.getId())
                .code(genre.getCode())
                .name(genre.getName())
                .build();
    }
}
