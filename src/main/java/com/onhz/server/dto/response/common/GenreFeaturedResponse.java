package com.onhz.server.dto.response.common;

import com.onhz.server.entity.GenreFeaturedEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GenreFeaturedResponse {
    private final Long id;
    private final String code;
    private final String name;
    private final String description;
    private final String imagePath;

    public static GenreFeaturedResponse of(GenreFeaturedEntity genreFeaturedEntity) {
        return GenreFeaturedResponse.builder()
                .id(genreFeaturedEntity.getId())
                .code(genreFeaturedEntity.getCode())
                .name(genreFeaturedEntity.getName())
                .description(genreFeaturedEntity.getDescription())
                .imagePath(genreFeaturedEntity.getImagePath())
                .build();
    }
}
