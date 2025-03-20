package com.onhz.server.dto.response.common;

import com.onhz.server.entity.GenreFeaturedEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GenreFeaturedSimpleResponse {
    private final Long id;
    private final String code;
    private final String name;
    private final String imagePath;


    public static GenreFeaturedSimpleResponse of(GenreFeaturedEntity genreFeaturedEntity) {
        return GenreFeaturedSimpleResponse.builder()
                .id(genreFeaturedEntity.getId())
                .code(genreFeaturedEntity.getCode())
                .name(genreFeaturedEntity.getName())
                .imagePath(genreFeaturedEntity.getImagePath())
                .build();
    }

    public static List<GenreFeaturedSimpleResponse> fromList(List<GenreFeaturedEntity> genreFeaturedEntityList) {
        return genreFeaturedEntityList.stream()
                .map(GenreFeaturedSimpleResponse::of)
                .collect(Collectors.toList());
    }
}
